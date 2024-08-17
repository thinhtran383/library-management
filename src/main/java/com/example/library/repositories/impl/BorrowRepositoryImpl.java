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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<String> emails = new ArrayList<>();
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
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    @Override
    public Map<String, String> getAllEmailWithMessagesByBorrowIds(List<String> borrowIds) {
        String sql = """
                SELECT r.readerEmail, bks.bookName, br.status
                FROM borrow br
                         JOIN readers r ON br.readerId = r.readerId
                         JOIN books bks ON br.bookId = bks.bookId
                WHERE br.borrowId IN (%s)
                """.formatted(borrowIds.stream().map(id -> "'" + id + "'").collect(Collectors.joining(",")));

        ResultSet rs = DbConnect.getInstance().executeQuery(sql);
        Map<String, Map<String, List<String>>> emailBooksStatusMap = new HashMap<>();

        try {
            while (rs != null && rs.next()) {
                String email = rs.getString("readerEmail");
                String bookName = rs.getString("bookName");
                String status = rs.getString("status");

                // Add the book name to the list associated with the email and status
                emailBooksStatusMap
                        .computeIfAbsent(email, k -> new HashMap<>())
                        .computeIfAbsent(status, k -> new ArrayList<>())
                        .add(bookName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Map<String, String> emailMessages = new HashMap<>();
        for (Map.Entry<String, Map<String, List<String>>> entry : emailBooksStatusMap.entrySet()) {
            String email = entry.getKey();
            Map<String, List<String>> booksByStatus = entry.getValue();

            StringBuilder message = new StringBuilder();
            message.append("<div style='font-family: Arial, sans-serif; padding: 10px;'>");

            // Append approved books with green title if they exist
            if (booksByStatus.containsKey("REQUEST")) {
                List<String> approvedBooks = booksByStatus.get("REQUEST");
                message.append("<h2 style='color: #4CAF50;'>Your request has been approved!</h2>");
                message.append("<p>Please come to the library to get the following books:</p>");
                message.append("<ul style='list-style-type: none; padding: 0;'>");

                for (String bookName : approvedBooks) {
                    message.append("<li style='padding: 5px; background-color: #f9f9f9; margin-bottom: 5px; border-radius: 5px;'>")
                            .append("&#x2022; ").append(bookName).append("</li>");
                }

                message.append("</ul>");
            }

            // Append declined books with red title if they exist
            if (booksByStatus.containsKey("DECLINE")) {
                List<String> declinedBooks = booksByStatus.get("DECLINE");
                message.append("<h2 style='color: #FF0000;'>Your request has been declined!</h2>");
                message.append("<p>The following books have been declined:</p>");
                message.append("<ul style='list-style-type: none; padding: 0;'>");

                for (String bookName : declinedBooks) {
                    message.append("<li style='padding: 5px; background-color: #f9f9f9; margin-bottom: 5px; border-radius: 5px;'>")
                            .append("&#x2022; ").append(bookName).append("</li>");
                }

                message.append("</ul>");
            }

            message.append("<p>Thank you for using our library services!</p>");
            message.append("</div>");

            emailMessages.put(email, message.toString());
        }

        return emailMessages;
    }


    @Override
    public void declineRequest(List<String> borrowIds) {
        String sql = """
                update borrow
                set status = 'DECLINE'
                where borrowId in (%s);
                """.formatted(String.join(",", borrowIds));
        dbConnect.executeUpdate(sql);
    }
}
