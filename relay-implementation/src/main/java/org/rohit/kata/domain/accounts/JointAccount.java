// Implements: AC-1
package org.rohit.kata.domain.accounts;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class JointAccount extends Account {

    private final List<Account> accountHolders;

    public JointAccount(List<Account> accountHolders) {
        super(UUID.randomUUID().toString(), BigDecimal.ZERO);
        this.accountHolders = List.copyOf(Objects.requireNonNull(accountHolders, "accountHolders must not be null"));
    }

    public List<Account> getAccountHolders() {
        return accountHolders;
    }
}
