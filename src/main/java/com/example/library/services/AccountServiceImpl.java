package com.example.library.services;

import com.example.library.models.Account;
import com.example.library.models.Reader;
import com.example.library.repositories.AccountRepositoryImpl;
import com.example.library.repositories.IAccountRepository;
import com.example.library.repositories.IReaderRepository;
import com.example.library.repositories.ReaderRepositoryImpl;
import javafx.beans.property.ObjectProperty;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Optional;


public class AccountServiceImpl implements IAccountService {

    private final IAccountRepository accountRepository;
    private final IReaderRepository readerRepository;

    public AccountServiceImpl() {
        this.accountRepository = new AccountRepositoryImpl();
        this.readerRepository = new ReaderRepositoryImpl();
    }

    @Override
    public boolean checkAccount(Account account) {
        Optional<Account> accountFromDb = accountRepository.getAccountAndRoleByUsername(account.getUsername());
        if (accountFromDb.isPresent()) {
            Account accountGet = accountFromDb.get();
            return account.getPassword().equals(accountGet.getPassword()) && account.getRole().equalsIgnoreCase(accountGet.getRole());
        }

        return false;
    }

    @Override
    public boolean registerAccount(Account account, Reader reader) {
        if (accountRepository.isExistUsername(account.getUsername())) {
            return false;
        }

        boolean isExistReaderPhoneNumber = readerRepository.isExistReaderPhoneNumber(reader.getReaderPhone());
        boolean isExistReaderEmail = readerRepository.isExistReaderEmail(reader.getReaderEmail());

        if (isExistReaderPhoneNumber || isExistReaderEmail) {
            throw new IllegalArgumentException("Phone number or email already exists");
        }

        accountRepository.save(account);

        try {
            readerRepository.save(reader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
