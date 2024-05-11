package com.example.library.services;

import com.example.library.models.Borrow;
import javafx.collections.ObservableList;

public interface IBorrowService {
    ObservableList<Borrow> getBorrowByReaderId(String readerId);
    void returnBook(String borrowId);
}
