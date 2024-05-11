package com.example.library.repositories;

import com.example.library.models.Borrow;
import javafx.collections.ObservableList;

public interface IBorrowRepository {
    ObservableList<Borrow> getBorrowByReaderId(String readerId);
    void returnBook(String borrowId);
}
