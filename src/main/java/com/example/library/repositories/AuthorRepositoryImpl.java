package com.example.library.repositories;

import com.example.library.models.Author;
import com.example.library.utils.Repo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;

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
        if (rs != null) {
            repo.executeUpdate(sqlUpdate);
        } else {
            repo.executeUpdate(sqlInsert);
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

}
