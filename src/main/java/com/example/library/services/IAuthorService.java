package com.example.library.services;

import com.example.library.models.Author;
import javafx.collections.ObservableList;

public interface IAuthorService {
    ObservableList<Author> getAllAuthors();
    void saveAuthor(Author author);
    void deleteAuthor(Author author);
    void updateAuthor(Author author);
    String getAuthorId();
}
