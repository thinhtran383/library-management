package com.example.library.services;

import com.example.library.models.Borrow;
import com.example.library.repositories.BookRepositoryImpl;
import com.example.library.repositories.BorrowRepositoryImpl;
import com.example.library.repositories.IBookRepository;
import com.example.library.repositories.IBorrowRepository;
import javafx.collections.ObservableList;

public class BorrowServiceImpl implements IBorrowService{
    private final IBorrowRepository borrowRepository;
    private final IBookRepository bookRepository;

    public BorrowServiceImpl(){
        borrowRepository = new BorrowRepositoryImpl();
        bookRepository = new BookRepositoryImpl();
    }
    @Override
    public ObservableList<Borrow> getBorrowByReaderId(String readerId) {
        return borrowRepository.getBorrowByReaderId(readerId);
    }

    @Override
    public void returnBook(String borrowId) {

    }

}
