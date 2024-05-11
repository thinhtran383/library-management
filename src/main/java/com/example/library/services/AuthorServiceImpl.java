package com.example.library.services;

import com.example.library.models.Author;
import com.example.library.repositories.AuthorRepositoryImpl;
import com.example.library.repositories.IAuthorRepository;
import javafx.collections.ObservableList;

public class AuthorServiceImpl implements IAuthorService {
    private final IAuthorRepository authorRepository;

    public AuthorServiceImpl() {
        this.authorRepository = new AuthorRepositoryImpl();
    }

    @Override
    public ObservableList<Author> getAllAuthors() {
        return authorRepository.getAllAuthors();
    }

    @Override
    public void saveAuthor(Author author) {
        System.out.println("save on service");
        authorRepository.save(author);
    }

    @Override
    public void deleteAuthor(Author author) {
        authorRepository.delete(author);
    }

    @Override
    public void updateAuthor(Author author) {
        authorRepository.save(author);
    }
}
