package com.example.library.services;

import com.example.library.models.Book;
import com.example.library.repositories.AuthorRepositoryImpl;
import com.example.library.repositories.BookRepositoryImpl;
import com.example.library.repositories.IAuthorRepository;
import com.example.library.repositories.IBookRepository;
import javafx.collections.ObservableList;

public class BookServiceImpl implements IBookService {

    private final IBookRepository bookRepository;
    private final IAuthorRepository authorRepository;

    public BookServiceImpl() {
        this.bookRepository = new BookRepositoryImpl();
        this.authorRepository = new AuthorRepositoryImpl();
    }

    @Override
    public ObservableList<Book> getAllBook() {
        return bookRepository.getAllBook();
    }

    @Override
    public ObservableList<String> getAllAuthorName() {
        return authorRepository.getAllAuthorName();
    }

    @Override
    public ObservableList<String> getAllCategoryName() {
        return bookRepository.getAllCategoryName();
    }

    @Override
    public void saveBook(Book book) {
        setBook(book);
        bookRepository.save(book);

    }

    @Override
    public void deleteBook(Book book) {
        bookRepository.delete(book);
    }

    @Override
    public void updateBook(Book book) {
        setBook(book);
        bookRepository.save(book);

    }

    private void setBook(Book book) {
        String categoryId = bookRepository.getCategoryIdByName(book.getCategory());
        String authorId = bookRepository.getAuthorIdByName(book.getAuthor());

        if (categoryId == null) {
            bookRepository.saveCategory(book.getCategory());
            categoryId = bookRepository.getCategoryIdByName(book.getCategory());
        }

        book.setCategory(categoryId);
        book.setAuthor(authorId);

    }

}
