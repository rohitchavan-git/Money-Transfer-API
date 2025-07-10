package org.rohit.kata.domain.transfer.repo;

import org.rohit.kata.domain.accounts.Account;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryDatabaseRepository {
    private final List<Account> database= new CopyOnWriteArrayList<>();

    public void save(Account account) {
        database.add(account);
    }

    public boolean check(String accountId) {
        return database.stream()
                .anyMatch(account -> account.getAcc().equals(accountId));
    }
}
