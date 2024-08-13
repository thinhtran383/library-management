package com.example.library.services;

import com.example.library.models.Account;
import com.example.library.models.Reader;

public interface IAccountService {
    boolean checkAccount(Account account);
    boolean registerAccount(Account account, Reader reader);
}
