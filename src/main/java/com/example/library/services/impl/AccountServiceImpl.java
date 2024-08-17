package com.example.library.services.impl;

import com.example.library.models.Account;
import com.example.library.models.Reader;
import com.example.library.repositories.impl.AccountRepositoryImpl;
import com.example.library.repositories.IAccountRepository;
import com.example.library.repositories.IReaderRepository;
import com.example.library.repositories.impl.ReaderRepositoryImpl;
import com.example.library.services.IAccountService;
import com.example.library.utils.UserContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public class AccountServiceImpl implements IAccountService {

    private final IAccountRepository accountRepository;
    private final IReaderRepository readerRepository;
    private final MailService mailService;

    public AccountServiceImpl() {
        this.accountRepository = new AccountRepositoryImpl();
        this.readerRepository = new ReaderRepositoryImpl();
        this.mailService = new MailService("smtp.gmail.com");
    }

    public boolean isBlocked(String username) {
        return accountRepository.isBlocked(username);
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
    public boolean registerAccount(Account account, Reader reader) throws Exception {
        if (accountRepository.isExistUsername(account.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        boolean isExistReaderPhoneNumber = readerRepository.isExistReaderPhoneNumber(reader.getReaderPhone());
        boolean isExistReaderEmail = readerRepository.isExistReaderEmail(reader.getReaderEmail());

        if (isExistReaderPhoneNumber || isExistReaderEmail) {
            throw new IllegalArgumentException("Phone number or email already exists");
        }

        accountRepository.save(account);

        int userId = accountRepository.getUserIdByUsername(account.getUsername());
        reader.setUserId(userId);
        try {
            readerRepository.save(reader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public void resetPassword(String email) throws Exception {
        Map<String, String> account = accountRepository.getAccountInfoByEmail(email);

        System.out.println(account);

        if (account == null) {
            throw new Exception("Email not found");
        } else {
            String username = account.get("username");
            String name = account.get("readerName");
            String password = UUID.randomUUID().toString().substring(0, 8);

            mailService.sendMail(
                    Map.of(email, String.format("Hello <b>%s</b>," +
                            " your new password is <b>%s</b> and username is <b>%s</b>," +
                            " please change after login", name, password, username)),
                    "Reset password"

            );

            accountRepository.save(new Account(username, password, "reader"));
        }
    }

    @Override
    public void changPassword(Account account, String newPassword) throws Exception {
        Account existAccount = accountRepository.getAccountAndRoleByUsername(account.getUsername())
                .orElseThrow();

        if (existAccount.getPassword().equals(account.getPassword())) {
            accountRepository.save(new Account(account.getUsername(), newPassword, UserContext.getInstance().getRole()));

        } else {
            throw new Exception("Current password not match");
        }

    }
}
