package com.example.library.repositories;

import com.example.library.models.Reader;
import javafx.collections.ObservableList;

import java.util.Optional;

public interface IReaderRepository {
    ObservableList<Reader> getAllReaders();
    void save(Reader reader);
    void delete(Reader reader);
    ObservableList<String> getAllReaderId();
    String getReaderNameById(String readerId);
    String getReaderIdByName(String readerName);
    int getTotalReader();
    String getReaderId();
    Optional<Reader> getReaderById(String readerId);
}
