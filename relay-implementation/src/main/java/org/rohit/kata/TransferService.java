// Implements: AC-1, AC-2, AC-3
package org.rohit.kata;

import org.rohit.kata.domain.accounts.Account;
import org.rohit.kata.domain.accounts.JointAccount;
import org.rohit.kata.domain.accounts.exception.InsufficientFundsException;
import org.rohit.kata.domain.accounts.repo.AccountRepository;
import org.rohit.kata.domain.accounts.repo.InMemoryDatabaseRepository;
import org.rohit.kata.domain.transfer.TransactionalStatus;
import org.rohit.kata.domain.transfer.exception.InvalidAccountException;
import org.rohit.kata.domain.transfer.exception.InvalidAmountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    final AccountRepository accountRepository;

    public TransferService() {
        this(new InMemoryDatabaseRepository());
    }

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository, "accountRepository must not be null");
    }

    public JointAccount createJointAccount(List<Account> accountHolders) {
        validateJointAccountRequest(accountHolders);

        JointAccount jointAccount = new JointAccount(accountHolders);
        accountRepository.save(jointAccount);
        LOGGER.info("Created joint account {} for {} account holders",
                jointAccount.getAccountId(), accountHolders.size());
        return jointAccount;
    }

    public String transfer(Account sourceAccount, Account destinationAccount,
                           BigDecimal amount) throws InvalidAmountException, InsufficientFundsException {

        if (notValidAccount(sourceAccount)) {
            throw new InvalidAccountException("Source not Found.");
        }
        if (notValidAccount(destinationAccount)) {
            throw new InvalidAccountException("destination not Found");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException();
        }

        return performTransfer(sourceAccount, destinationAccount, amount);
    }

    private void validateJointAccountRequest(List<Account> accountHolders) {
        if (accountHolders == null || accountHolders.size() < 2) {
            throw new InvalidJointAccountRequestException(
                    "A joint account requires at least two account holders");
        }

        long distinctHolderCount = accountHolders.stream()
                .filter(Objects::nonNull)
                .map(Account::getAccountId)
                .filter(this::hasText)
                .distinct()
                .count();

        boolean hasInvalidHolder = accountHolders.stream().anyMatch(holder ->
                holder == null
                        || !hasText(holder.getAccountId())
                        || holder.getBalance() == null
                        || holder.getBalance().compareTo(BigDecimal.ZERO) < 0);

        if (hasInvalidHolder || distinctHolderCount != accountHolders.size()) {
            throw new InvalidJointAccountRequestException(
                    "Account-holder information is incomplete or invalid");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String performTransfer(Account sourceAccount, Account destinationAccount,
                                          BigDecimal amount)
            throws InvalidAmountException, InsufficientFundsException {
        Object lock1;
        Object lock2;
        if (sourceAccount.getAccountId().compareTo(destinationAccount.getAccountId()) < 0) {
            lock1 = sourceAccount;
            lock2 = destinationAccount;
        } else {
            lock1 = destinationAccount;
            lock2 = sourceAccount;
        }

        synchronized (lock1) {
            synchronized (lock2) {
                try {
                    sourceAccount.withdraw(amount);
                    destinationAccount.deposit(amount);
                    return TransactionalStatus.SUCCEED.getName();
                } catch (Exception exception) {
                    sourceAccount.deposit(amount);
                    return TransactionalStatus.FAILED.getName();
                }
            }
        }
    }

    private boolean notValidAccount(Account account) {
        return Optional.ofNullable(account)
                .map(Account::getAccountId)
                .filter(accountRepository::hasAccount)
                .isEmpty();
    }
}
