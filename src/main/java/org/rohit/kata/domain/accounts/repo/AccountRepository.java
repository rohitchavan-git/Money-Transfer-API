package org.rohit.kata.domain.accounts.repo;

import org.rohit.kata.domain.accounts.Account;

public interface AccountRepository {
    void save(Account account);
    boolean hasAccount(String accountId);
}
