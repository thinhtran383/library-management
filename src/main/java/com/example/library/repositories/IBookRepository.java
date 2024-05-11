package com.example.library.repositories;

import com.example.library.models.Author;
import com.example.library.models.Book;
import javafx.collections.ObservableList;

public interface IBookRepository {
    void save(Book book);
    void delete(Book book);
    void saveCategory(String category);
    String getCategoryIdByName(String categoryName);
    ObservableList<Book> getAllBook();

    ObservableList<String> getAllCategoryName();

    String getAuthorIdByName(String author);

    void increaseQuantity(String bookId);
    void decreaseQuantity(String bookId);
}
