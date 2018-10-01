package com.ingenico.epay.service;

import com.ingenico.epay.api.exception.InsufficientBalanceException;
import com.ingenico.epay.domain.Account;
import com.ingenico.epay.domain.Transaction;
import com.ingenico.epay.domain.TransactionType;
import com.ingenico.epay.repository.AccountRepository;
import com.ingenico.epay.repository.TransactionRepository;
import com.ingenico.epay.util.StanGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.math.BigDecimal;

@Service
public class TransactionExecutorService {

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private StanGenerator stanGenerator;
    private Logger LOGGER = LoggerFactory.getLogger(TransactionExecutorService.class);

    @Autowired
    public TransactionExecutorService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            StanGenerator stanGenerator) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.stanGenerator = stanGenerator;
    }

    @Transactional
    public String execute(
            TransactionType transactionType ,
            Account account,
            BigDecimal amount) {
        verify(transactionType , account.getBalance() , amount);
        LOGGER.info("transaction is valid according to currentBalance");

        BigDecimal updatedBalance
                = calculateUpdatedBalance(transactionType , account.getBalance() , amount);
        Transaction transaction
                = persistTransaction(transactionType , account, updatedBalance , amount);
        LOGGER.info("transaction has been persisted(not committed yet)");
        updateAccountBalance(account , updatedBalance);
        LOGGER.info("balance in accoount has been updated(not committed yet)");
        return transaction.getStan();
    }

    private void verify(TransactionType transactionType, BigDecimal balance, BigDecimal amount) {
        if(transactionType.equals(TransactionType.WITHDRAW) &&
                balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
    }

    private BigDecimal calculateUpdatedBalance(TransactionType transactionType, BigDecimal currentBalance, BigDecimal amount) {
        if(transactionType.equals(TransactionType.WITHDRAW)) {
            return currentBalance.subtract(amount);
        }
        return currentBalance.add(amount);
    }


    private void updateAccountBalance(Account account, BigDecimal updatedBalance) {
        account.setBalance(updatedBalance);
        accountRepository.save(account);
    }

    private Transaction persistTransaction(
            TransactionType transactionType ,
            Account account,
            BigDecimal updatedBalance,
            BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setBalance(updatedBalance);
        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);
        transaction.setStan(stanGenerator.generate());
        transactionRepository.save(transaction);
        return transaction;
    }


    public void verify() {

    }
}
