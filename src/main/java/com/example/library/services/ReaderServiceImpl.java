package com.example.library.services;

import com.example.library.models.Reader;
import com.example.library.repositories.IReaderRepository;
import com.example.library.repositories.ReaderRepositoryImpl;
import com.example.library.utils.AlertUtil;
import javafx.collections.ObservableList;

import java.util.Optional;

public class ReaderServiceImpl implements IReaderService {
    private final IReaderRepository readerRepository;


    public ReaderServiceImpl() {
        this.readerRepository = new ReaderRepositoryImpl();
    }

    @Override
    public ObservableList<Reader> getAllReaders() {
        return readerRepository.getAllReaders();
    }

    @Override
    public ObservableList<String> getAllReaderId() {
        return readerRepository.getAllReaderId();
    }

    @Override
    public String getReaderNameById(String readerId) {
        return readerRepository.getReaderNameById(readerId);
    }

    @Override
    public void saveReader(Reader reader) {
        readerRepository.save(reader);

    }

    @Override
    public void updateReader(Reader reader) throws Exception {
        boolean isPhoneNumberExisted = readerRepository.isExistReaderPhoneNumber(reader.getReaderPhone());
        boolean isEmailExisted = readerRepository.isExistReaderEmail(reader.getReaderEmail());

        if (isPhoneNumberExisted || isEmailExisted) {
            throw new IllegalArgumentException("Phone number or email already exists");
        }

        readerRepository.save(reader);

    }

    @Override
    public void deleteReader(Reader reader) {
        readerRepository.delete(reader);
    }

    @Override
    public String getReaderId() {
        return readerRepository.getReaderId();
    }

    @Override
    public Reader getReaderByUsername(String username) {
        return readerRepository.getReaderByUsername(username);
    }

    public Optional<Reader> getReaderById(String readerId) {
        return readerRepository.getReaderById(readerId);
    }


}
