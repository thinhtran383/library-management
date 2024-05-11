package com.example.library.services;

import com.example.library.models.Reader;
import javafx.collections.ObservableList;

public interface IReaderService {
    ObservableList<Reader> getAllReaders();
    void saveReader(Reader reader);
    void deleteReader(Reader reader);
}
