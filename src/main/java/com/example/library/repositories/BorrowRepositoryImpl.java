package com.example.library.repositories;

import com.example.library.models.Borrow;
import com.example.library.utils.Repo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.time.LocalDate;

public class BorrowRepositoryImpl implements IBorrowRepository {
    private final Repo repo = Repo.getInstance();

    @Override
    public ObservableList<Borrow> getBorrowByReaderId(String readerId) {
        ObservableList<Borrow> result = FXCollections.observableArrayList();
        String sql = String.format(
                """
                        select br.borrowId, b.bookName, r.readerName, br.borrowDate, br.returnDate from borrow br
                        join library.books b on b.bookId = br.bookId
                        join library.readers r on r.readerId = br.readerId
                        where b.isDelete = false and r.isDelete = false and r.readerId = '%s';
                        """, readerId
        );

        ResultSet rs = repo.executeQuery(sql);
        try {
            while (rs.next()) {
                result.add(Borrow.builder()
                        .borrowId(rs.getString("borrowId"))
                        .bookName(rs.getString("bookName"))
                        .readerName(rs.getString("readerName"))
                        .borrowDate(rs.getDate("borrowDate").toLocalDate())
                        .returnDate(rs.getDate("returnDate").toLocalDate())
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void returnBook(String borrowId) {
        String sql = String.format(
                """
                        update borrow
                        set returnDate = '%s'
                        where borrowId = '%s';
                        """, LocalDate.now(), borrowId
        );
        repo.executeUpdate(sql);
    }
}
