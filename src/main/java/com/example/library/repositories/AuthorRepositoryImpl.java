package com.example.library.repositories;

import com.example.library.models.Author;
import com.example.library.utils.Repo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.random.RandomGenerator;

public class AuthorRepositoryImpl implements IAuthorRepository {
    private final Repo repo = Repo.getInstance();

    @Override
    public ObservableList<String> getAllAuthorName() {
        ObservableList<String> result = FXCollections.observableArrayList();
        String sql = """
                select authorName from authors where isDelete = false;
                """;
        ResultSet rs = repo.executeQuery(sql);

        try {
            while (rs.next()) {

                String authorName = rs.getString("authorName");

                result.add(authorName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public ObservableList<Author> getAllAuthors() {
        ObservableList<Author> result = FXCollections.observableArrayList();
        String sql = """
                select * from authors where isDelete = false;
                """;

        ResultSet rs = repo.executeQuery(sql);

        try {
            while (rs.next()) {
                Author author = Author.builder()
                        .authorId(rs.getString("authorId"))
                        .authorName(rs.getString("authorName"))
                        .address(rs.getString("address"))
                        .build();
                result.add(author);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public void save(Author author) {
        String sqlInsert = String.format(
                """
                        insert into authors(authorId, authorName, address) values('%s','%s','%s');
                        """, author.getAuthorId(), author.getAuthorName(), author.getAddress()
        );

        String sqlUpdate = String.format(
                """
                                update authors set authorName = '%s', address = '%s' where authorId = '%s';
                        """, author.getAuthorName(), author.getAddress(), author.getAuthorId()
        );

        String sqlCheck = String.format(
                """
                        select * from authors where authorId = '%s';
                        """, author.getAuthorId()
        );

        ResultSet rs = repo.executeQuery(sqlCheck);
        try {
            if (rs.next()) {
                repo.executeUpdate(sqlUpdate);
            } else {
                repo.executeUpdate(sqlInsert);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Author author) {
        String sql = String.format(
                """
                        update authors set isDelete = true where authorId = '%s';
                        """, author.getAuthorId()
        );
        repo.executeUpdate(sql);

    }

    @Override
    public String getAuthorIdByName(String author) {
        String sql = String.format("""
                select authorId from authors where authorName = '%s';
                """, author);
        ResultSet rs = repo.executeQuery(sql);
        try {
            if (rs.next()) {
                return rs.getString("authorId");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public String getAuthorId() {
        String sql = "select count(*) from authors";

        int id = RandomGenerator.getDefault().nextInt();

        ResultSet rs = repo.executeQuery(sql);

        try{
            if(rs.next()){
                id = rs.getInt(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return String.format("Aut%d", id);
    }

}
