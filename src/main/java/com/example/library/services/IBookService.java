package com.example.library.services;

import com.example.library.models.Book;
import javafx.collections.ObservableList;

public interface IBookService {
    ObservableList<Book> getAllBook();
    ObservableList<String> getAllAuthorName();
    ObservableList<String> getAllCategoryName();
    void saveBook(Book book);
    void deleteBook(Book book);
    void updateBook(Book book);
}
