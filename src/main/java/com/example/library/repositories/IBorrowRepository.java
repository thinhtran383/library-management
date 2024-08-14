package com.example.library.repositories;

import com.example.library.models.Borrow;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;

public interface IBorrowRepository {
    ObservableList<Borrow> getBorrowByReaderId(String readerId);
    ObservableList<Borrow> getAllBookBorrowed();
    void returnBook(String borrowId);
    void save(Borrow borrow);
    int getTotalBorrow();
    int getTotalLate();
    int getTotalReturn();
    int getTotalBorrowByReaderId(String readerId);
    boolean isReaderLate(String readerId);

    void requestBorrow(String bookId, LocalDate returnDate);

    void approveRequest(List<String> borrowId);

    void deleteRequest(List<String> borrowId);

    List<String> getAllEmailByBorrowIds(List<String> borrowIds);


}
