package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.security.jwt.InvalidTransferException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Transfer sendMoney(Long senderId, Long receiverId, BigDecimal amount) throws InvalidTransferException {

        String senderAccountBalanceSql = "SELECT balance FROM account WHERE account_id = ?";
        BigDecimal senderBalance = jdbcTemplate.queryForObject(senderAccountBalanceSql, BigDecimal.class, senderId);

        if (senderBalance.longValue() < amount.longValue()) {
            throw new InvalidTransferException("Not enough funds available");

        } else if (amount.longValue() < 0) {
            throw new InvalidTransferException("Please enter positive $ amount");
        } else if (senderId == receiverId) {
            throw new InvalidTransferException("Cannot send money to yourself");
        } else {

            String sql = "UPDATE account " +
                    "SET balance = balance - ? " +
                    "WHERE account_id = ?;" +
                    "\n" +
                    "UPDATE account " +
                    "SET balance = balance + ?" +
                    "WHERE account_id = ?";

            jdbcTemplate.update(sql, amount, senderId, amount, receiverId);

            String addTransferSql = "INSERT INTO transfer (transfer_type_id,transfer_status_id,account_from, account_to, amount)\n" +
                    "\n" +
                    "VALUES ((SELECT transfer_type_id FROM transfer_type WHERE transfer_type_desc = 'Send'), \n" +
                    "(SELECT transfer_status_id FROM transfer_status WHERE transfer_status_desc = 'Approved'), \n" +
                    "? ,  ?,  ?) RETURNING transfer_id;";

            Long transferId = jdbcTemplate.queryForObject(addTransferSql, Long.class, senderId, receiverId, amount);
            sql = "SELECT * \n" +
                    "FROM transfer\n" +
                    "WHERE transfer_id = ?;";

            Transfer transfer = new Transfer();

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferId);
            if (rowSet.next()) {

                transfer = mapRowToTransfer(rowSet);
            }
            return transfer;
        }
    }

    public List<Transfer> getTransfersByAccountId(int id) {


        List<Transfer> transfers = new ArrayList<>();

        String sql = "SELECT ac.user_id, tu.username to_user, tuf.username from_user, ac.account_id, transfer_id, transfer_type_desc, transfer_status_desc, amount, account_to\n" +
                "FROM transfer tr \n" +
                "JOIN account ac ON ac.account_id = tr.account_to \n" +
                "JOIN tenmo_user tu ON tu.user_id = ac.user_id\n" +
                "JOIN account acf ON acf.account_id = tr.account_from\n" +
                "JOIN tenmo_user tuf ON tuf.user_id = acf.user_id\n" +
                "JOIN transfer_status ts ON ts.transfer_status_id = tr.transfer_status_id\n" +
                "JOIN transfer_type tt ON tt.transfer_type_id = tr.transfer_type_id\n" +
                "WHERE ac.user_id = ? OR acf.user_id = ?;";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id, id);

        while (rowSet.next()) {

            transfers.add(mapRowToTransferWithNames(rowSet));
        }
        return transfers;
    }

    public Transfer getTransferDetails(int id){
        String sql = "SELECT ac.user_id, tu.username to_user, tuf.username from_user, ac.account_id, transfer_id, transfer_type_desc, transfer_status_desc, amount, account_to\n" +
                "FROM transfer tr \n" +
                "JOIN account ac ON ac.account_id = tr.account_to \n" +
                "JOIN tenmo_user tu ON tu.user_id = ac.user_id\n" +
                "JOIN account acf ON acf.account_id = tr.account_from\n" +
                "JOIN tenmo_user tuf ON tuf.user_id = acf.user_id\n" +
                "JOIN transfer_status ts ON ts.transfer_status_id = tr.transfer_status_id\n" +
                "JOIN transfer_type tt ON tt.transfer_type_id = tr.transfer_type_id\n" +
                "WHERE tr.transfer_id = ?;";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql,id);

        Transfer transfer = new Transfer();

        while(rowSet.next()){
            transfer= mapRowToTransferWithNames(rowSet);
        }

        return transfer;

    }


    private Transfer mapRowToTransferWithNames(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getLong("transfer_id"));
        transfer.setType(rs.getString("transfer_type_desc"));
        transfer.setStatus(rs.getString("transfer_status_desc"));
        transfer.setSenderName(rs.getString("from_user"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        transfer.setReceiverName(rs.getString("to_user"));

        return transfer;
    }


    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getLong("transfer_id"));
        transfer.setTransferTypeId(rs.getLong("transfer_type_id"));
        transfer.setTransferStatusId(rs.getLong("transfer_status_id"));
        transfer.setSenderAccount(rs.getLong("account_from"));
        transfer.setReceiverAccount(rs.getLong("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    }


}
