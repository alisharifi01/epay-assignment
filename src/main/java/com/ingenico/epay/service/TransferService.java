package com.ingenico.epay.service;

import com.ingenico.epay.api.dto.TransferDTO;
import com.ingenico.epay.api.dto.TransferResponseDTO;
import com.ingenico.epay.api.exception.AccountNotFoundException;
import com.ingenico.epay.api.exception.FromAndToAccountAreSameException;
import com.ingenico.epay.domain.Account;
import com.ingenico.epay.domain.TransactionType;
import com.ingenico.epay.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class TransferService {

    private TransactionExecutorService transactionExecutorService;
    private AccountRepository accountRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    @Autowired
    public TransferService(
            TransactionExecutorService transactionExecutorService,
            AccountRepository accountRepository) {
        this.transactionExecutorService = transactionExecutorService;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public TransferResponseDTO transfer(TransferDTO transferDTO) {
        verify(transferDTO);
        Account fromAccount = accountRepository.findByIban(transferDTO.getFromAccount());
        LOGGER.info("fromAccount has been fetched");
        Optional.ofNullable(fromAccount).orElseThrow(AccountNotFoundException::new);

        Account toAccount = accountRepository.findByIban(transferDTO.getToAccount());
        LOGGER.info("toAccount has been fetched");
        Optional.ofNullable(toAccount).orElseThrow(AccountNotFoundException::new);

        String transactionReference
                = transactionExecutorService.execute(TransactionType.WITHDRAW , fromAccount , transferDTO.getAmount());
        LOGGER.info("withdraw transaction has been done(not committed yet)");
        transactionExecutorService.execute(TransactionType.DEPOSIT , toAccount , transferDTO.getAmount());
        LOGGER.info("deposit transaction has been done(not committed yet)");

        return new TransferResponseDTO(transactionReference);
    }

    private void verify(TransferDTO transferDTO) {
        if(transferDTO.getFromAccount().equals(transferDTO.getToAccount())) {
            throw new FromAndToAccountAreSameException();
        }
    }
}
