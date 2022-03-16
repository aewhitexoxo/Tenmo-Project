package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.security.UserIdNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Account findByUserId(int userId) throws UserIdNotFoundException {
        String sql = "SELECT * FROM account\n" +
                "WHERE user_id = ?";

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
            if (rowSet.next()) {
                return mapRowToAccount(rowSet);
            }
            throw new UserIdNotFoundException("User_id :" + userId + " was not found. ");

        }

    @Override
    public List<User> getListOfUsers() {
        List<User> userList = new ArrayList<>();
        User user = null;
        String sql = "SELECT * FROM tenmo_user";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next()) {
            user = mapRowToUser(rowSet);
            userList.add(user);
        }
        return userList;
    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        return user;
    }


    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getLong("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(BigDecimal.valueOf(rs.getDouble("balance")));
        return account;
    }


}
