package com.example.library.repositories;

import com.example.library.models.Account;
import com.example.library.models.Reader;

import java.util.Map;
import java.util.Optional;

public interface IAccountRepository {
    Optional<Account> getAccountAndRoleByUsername(String username);

    boolean isExistUsername(String username);

    void save(Account account);
}
