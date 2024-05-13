package com.example.library.repositories;

import com.example.library.models.Author;
import javafx.collections.ObservableList;

public interface IAuthorRepository {
    ObservableList<String> getAllAuthorName();
    ObservableList<Author> getAllAuthors();
    void save(Author author);
    void delete(Author author);
    String getAuthorIdByName(String author);
    String getAuthorId();

}
