package com.ingenico.epay.service;

import com.ingenico.epay.api.exception.InsufficientBalanceException;
import com.ingenico.epay.domain.Account;
import com.ingenico.epay.domain.TransactionType;
import com.ingenico.epay.repository.AccountRepository;
import com.ingenico.epay.repository.TransactionRepository;
import com.ingenico.epay.service.TransactionExecutorService;
import com.ingenico.epay.util.StanGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.math.BigDecimal;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {TransactionExecutorService.class})
public class TransactionExecutorServiceTest {

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private StanGenerator stanGenerator;

    @Autowired
    private TransactionExecutorService transactionExecutorService;

    @Before
    public void mockDependencies() {
        Mockito.when(stanGenerator.generate()).thenReturn("abcd");
        System.out.println(transactionExecutorService);
    }

    @Test
    public void withDraw_successful() {
        TransactionType transactionType = TransactionType.WITHDRAW;
        Account account = new Account();
        account.setBalance(new BigDecimal("1000"));
        BigDecimal amount = new BigDecimal("100");

        String stan = transactionExecutorService.execute(transactionType, account, amount);

        assertNotNull(stan);
        assertEquals(stan, "abcd");
    }

    @Test
    public void withDraw_successful2() {
        TransactionType transactionType = TransactionType.WITHDRAW;
        Account account = new Account();
        account.setBalance(new BigDecimal("1000"));
        BigDecimal amount = new BigDecimal("1000");

        String stan = transactionExecutorService.execute(transactionType, account, amount);

        assertNotNull(stan);
        assertEquals(stan, "abcd");
    }

    @Test
    public void deposit_successful() {
        TransactionType transactionType = TransactionType.DEPOSIT;
        Account account = new Account();
        account.setBalance(new BigDecimal("0"));
        BigDecimal amount = new BigDecimal("100");

        String stan = transactionExecutorService.execute(transactionType, account, amount);

        assertNotNull(stan);
        assertEquals(stan, "abcd");
    }

    @Test(expected = InsufficientBalanceException.class)
    public void insufficientBalance() {
        TransactionType transactionType = TransactionType.WITHDRAW;
        Account account = new Account();
        account.setBalance(new BigDecimal("100"));
        BigDecimal amount = new BigDecimal("201");

        transactionExecutorService.execute(transactionType, account, amount);
    }

}
