package com.example.library.repositories;

import com.example.library.models.Account;
import com.example.library.models.Reader;
import com.example.library.utils.Repo;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AccountRepositoryImpl implements IAccountRepository {
    private final Repo repo = Repo.getInstance();

    @Override
    public Optional<Account> getAccountAndRoleByUsername(String username) {
        String query = String.format("SELECT * FROM users WHERE username = '%s'", username);
        ResultSet rs = repo.executeQuery(query);
        try {
            if (rs.next()) {
                return Optional.of(Account.builder()
                        .username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .role(rs.getString("role"))
                        .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public boolean isExistUsername(String username) {
        String query = String.format("SELECT COUNT(*) FROM users WHERE username = '%s'", username);

        ResultSet rs = repo.executeQuery(query);
        try {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    @Override
    public void save(Account account) {
        String query = String.format("INSERT INTO users (username, password) VALUES ('%s', '%s')", account.getUsername(), account.getPassword());
        String queryUpdate = String.format("UPDATE users SET password = '%s' WHERE username = '%s'", account.getPassword(), account.getUsername());

        ResultSet rs = repo.executeQuery(String.format("SELECT * FROM users WHERE username = '%s'", account.getUsername()));

        try {
            if (rs.next()) {
                repo.executeUpdate(queryUpdate);
            } else {
                repo.executeUpdate(query);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Reader getInformation(String username) {
        String sql = String.format("""
                select r.* from readers r
                join library.users u on u.userId = r.userId
                where u.username = '%s';
                """, username);

        ResultSet rs = repo.executeQuery(sql);

        try {
            if(rs.next()){
                Reader reader = Reader.builder()
                        .readerId(rs.getString("readerId"))
                        .readerName(rs.getString("readerName"))
                        .readerEmail(rs.getString("readerEmail"))
                        .readerPhone(rs.getString("readerPhoneNumber"))
                        .readerDOB(rs.getDate("readerDOB").toLocalDate())
                        .readerAddress(rs.getString("address"))
                        .build();
                return reader;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public static void main(String[] args) {
        AccountRepositoryImpl accountRepository = new AccountRepositoryImpl();
        System.out.println(accountRepository.getInformation("test"));

    }
}
