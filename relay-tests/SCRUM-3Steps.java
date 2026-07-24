```java
package org.rohit.kata;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rohit.kata.domain.accounts.Account;
import org.rohit.kata.domain.accounts.exception.InsufficientFundsException;
import org.rohit.kata.domain.accounts.repo.AccountRepository;
import org.rohit.kata.domain.transfer.exception.InvalidAccountException;
import org.rohit.kata.domain.transfer.exception.InvalidAmountException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Executable tests for the debit-card acceptance-test prerequisites exposed by
 * the supplied source code.
 *
 * The supplied source contains no debit-card entity, repository, service,
 * controller, lookup operation, allocation operation, persistence model, or
 * logging implementation. Consequently, debit-card acceptance steps cannot be
 * implemented without inventing production APIs. These tests cover the
 * existing Account and TransferService behaviour that is actually present.
 */
class DebitCardAcceptanceTest {

    @Test
    void accountAtMinimumBalanceHasBalanceOfExactlyFiveThousand() {
        Account account = new Account("account-5000", new BigDecimal("5000"));

        assertEquals(new BigDecimal("5000"), account.getBalance());
    }

    @Test
    void accountBelowMinimumBalanceRetainsItsInsufficientBalance() {
        Account account = new Account("account-below-minimum", new BigDecimal("4999.99"));

        assertEquals(new BigDecimal("4999.99"), account.getBalance());
    }

    @Test
    void transferRejectsAnAccountIdThatRepositoryDoesNotRecognize()
            throws InvalidAmountException, InsufficientFundsException {
        AccountRepository repository = Mockito.mock(AccountRepository.class);
        Mockito.when(repository.hasAccount("missing-account")).thenReturn(false);
        Mockito.when(repository.hasAccount("existing-account")).thenReturn(true);

        TransferService service = new TransferService(repository);
        Account missingAccount =
                new Account("missing-account", new BigDecimal("5000"));
        Account existingAccount =
                new Account("existing-account", new BigDecimal("5000"));

        assertThrows(
                InvalidAccountException.class,
                () -> service.transfer(
                        missingAccount,
                        existingAccount,
                        BigDecimal.ONE));
    }

    @Test
    void transferRejectsNullSourceAccountAsInvalid() {
        AccountRepository repository = Mockito.mock(AccountRepository.class);
        Mockito.when(repository.hasAccount("existing-account")).thenReturn(true);

        TransferService service = new TransferService(repository);
        Account existingAccount =
                new Account("existing-account", new BigDecimal("5000"));

        assertThrows(
                InvalidAccountException.class,
                () -> service.transfer(
                        null,
                        existingAccount,
                        BigDecimal.ONE));
    }

    @Test
    void accountWithdrawRejectsAmountGreaterThanBalance()
            throws InvalidAmountException {
        Account account = new Account("account-1", new BigDecimal("4999"));

        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(new BigDecimal("5000")));
    }
}
```