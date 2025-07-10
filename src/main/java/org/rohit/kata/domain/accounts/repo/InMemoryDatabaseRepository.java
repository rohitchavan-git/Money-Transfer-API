package org.rohit.kata.domain.accounts.repo;

import org.rohit.kata.domain.accounts.Account;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryDatabaseRepository implements AccountRepository {
    private final List<Account> database= new CopyOnWriteArrayList<>();

    @Override
    public void save(Account account) {
        database.add(account);
    }

    @Override
    public boolean hasAccount(String accountId) {
        return database.stream()
                .anyMatch(account -> account.getAccountId().equals(accountId));
    }
}
