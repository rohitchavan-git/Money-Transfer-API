package org.rohit.kata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rohit.kata.domain.accounts.Account;
import org.rohit.kata.domain.transfer.repo.InMemoryDatabaseRepository;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    private static final String ROHIT_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String JOHN_ACCOUNT_ID = UUID.randomUUID().toString();
    private InMemoryDatabaseRepository inMemoryRepo;


    @BeforeEach
    public void init() {
     inMemoryRepo = new InMemoryDatabaseRepository();
    }

    @Test
    public void
    test_add_new_account() {

       Account rohitAccount=  new Account(ROHIT_ACCOUNT_ID ,new BigDecimal(1000));
       inMemoryRepo.save(rohitAccount);
       assertTrue(inMemoryRepo.check(ROHIT_ACCOUNT_ID));
       assertFalse(inMemoryRepo.check(JOHN_ACCOUNT_ID));
    }


}
