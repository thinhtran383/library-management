package com.example.library.repositories.impl;

import com.example.library.models.Borrow;
import com.example.library.repositories.IBorrowRepository;
import com.example.library.utils.DbConnect;
import com.example.library.utils.UserContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowRepositoryImpl implements IBorrowRepository {
    private final DbConnect dbConnect = DbConnect.getInstance();

    @Override
    public ObservableList<Borrow> getBorrowByReaderId(String readerId) {
        ObservableList<Borrow> result = FXCollections.observableArrayList();
        String sql = String.format(
                """
                        select br.borrowId, b.bookName, r.readerName, br.borrowDate, br.returnDate, br.dueDate, br.status from borrow br
                        join library.books b on b.bookId = br.bookId
                        join library.readers r on r.readerId = br.readerId
                        where r.readerId = '%s';
                        """, readerId
        );

        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            while (rs.next()) {
                result.add(Borrow.builder()
                        .borrowId(rs.getString("borrowId"))
                        .bookName(rs.getString("bookName"))
                        .readerName(rs.getString("readerName"))
                        .borrowDate(rs.getDate("borrowDate").toLocalDate())
                        .returnDate(rs.getDate("returnDate").toLocalDate())
                        .dueDate(rs.getString("dueDate"))
                        .status(rs.getString("status"))
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public ObservableList<Borrow> getAllBookBorrowed() {
        ObservableList<Borrow> result = FXCollections.observableArrayList();
        String sql = """
                select br.borrowId, b.bookName, r.readerName, br.borrowDate, br.returnDate, br.dueDate, br.status, r.readerId from borrow br
                        join library.books b on b.bookId = br.bookId
                        join library.readers r on r.readerId = br.readerId
                     
                """;

        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            while (rs.next()) {
                result.add(Borrow.builder()
                        .borrowId(rs.getString("borrowId"))
                        .bookName(rs.getString("bookName"))
                        .readerName(rs.getString("readerName"))
                        .borrowDate(rs.getDate("borrowDate").toLocalDate())
                        .returnDate(rs.getDate("returnDate").toLocalDate())
                        .dueDate(rs.getString("dueDate"))
                        .status(rs.getString("status"))
                        .readerId(rs.getString("readerId"))
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
                        set dueDate = '%s'
                        where borrowId = '%s';
                        """, LocalDate.now(), borrowId
        );
        dbConnect.executeUpdate(sql);
    }

    @Override
    public void save(Borrow borrow) {
        String bookId = borrow.getBookName();
        String sql = String.format(
                """
                        insert into borrow( bookId, readerId, borrowDate, returnDate)
                        values ( '%s', '%s', '%s', '%s');
                        """, bookId, borrow.getReaderName(), LocalDate.now(), borrow.getReturnDate()
        );

        dbConnect.executeUpdate(sql);
    }

    @Override
    public void requestBorrow(String bookId, LocalDate returnDate) {
        String sql = String.format("""
                insert into borrow(bookId, readerId, borrowDate, returnDate, status)
                values('%s','%s','%s','%s','%s');
                """, bookId, UserContext.getInstance().getReaderId(), LocalDate.now(), returnDate, "REQUEST");

        dbConnect.executeUpdate(sql);
    }


    @Override
    public int getTotalBorrow() {
        String sql = """
                select count(*) as total from borrow;
                """;

        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public int getTotalLate() {
        String sql = """
                select count(*) as total from borrow where returnDate < current_date();
                """;

        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public int getTotalReturn() {
        String sql = """
                SELECT COUNT(*) AS total_books_returned_on_time
                FROM borrow
                WHERE dueDate IS NOT NULL
                  AND dueDate <= returnDate;
                           
                                """;
        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public int getTotalBorrowByReaderId(String readerId) {
        String sql = String.format(
                """
                        select count(*) as total from borrow where readerId = '%s' and dueDate is null;
                        """, readerId
        );

        ResultSet rs = dbConnect.executeQuery(sql);
        try {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public boolean isReaderLate(String readerId) {
        String sql = String.format("""
                select count(*) from borrow
                where dueDate is null and returnDate < now() and readerId = '%s';
                """, readerId);
        ResultSet rs = dbConnect.executeQuery(sql);
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
    public void approveRequest(List<String> borrowIds) {
        String sql = """
                update borrow
                set status = ''
                where borrowId in (%s);
                """.formatted(String.join(",", borrowIds));
        dbConnect.executeUpdate(sql);
    }

    @Override
    public void deleteRequest(List<String> borrowId) {
        String sql = """
                delete from borrow
                where borrowId in (%s);
                """.formatted(String.join(",", borrowId));
        dbConnect.executeUpdate(sql);
    }

    @Override
    public List<String> getAllEmailByBorrowIds(List<String> borrowIds) {
        String sql = """
                SELECT DISTINCT r.readerEmail
                FROM borrow b
                         JOIN readers r ON b.readerId = r.readerId
                WHERE b.borrowId IN (%s)
                """.formatted(String.join(",", borrowIds));

        ResultSet rs = dbConnect.executeQuery(sql);
        List<String> emails =new ArrayList<>();
        try {
            while (rs.next()) {
                emails.add(rs.getString("readerEmail"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return emails;
    }

    @Override
    public List<String> getAllBookIdByBorrowId(List<String> borrowId) {
        String sql = """
                select bookId from borrow where borrowId in (%s);
                """.formatted(String.join(",", borrowId));
        ResultSet rs = dbConnect.executeQuery(sql);
        List<String> bookIds = new ArrayList<>();
        try {
            while (rs.next()) {
                bookIds.add(rs.getString("bookId"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return bookIds;
    }

    @Override
    public boolean isAlreadyRequest(String readerId, String bookId) {
        String sql = String.format("""
                select count(*) from borrow
                where bookId = '%s' and readerId = '%s' and dueDate is null;
                """, bookId, readerId);

        ResultSet rs = dbConnect.executeQuery(sql);

        try {
            if(rs.next()){
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }


    public static void main(String[] args) {
        BorrowRepositoryImpl borrowRepository = new BorrowRepositoryImpl();

        System.out.println(borrowRepository.isAlreadyRequest("R26BF1", "B005"));
    }
}
