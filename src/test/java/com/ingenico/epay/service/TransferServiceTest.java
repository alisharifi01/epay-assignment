package com.ingenico.epay.service;

import com.ingenico.epay.api.dto.AccountInfoDTO;
import com.ingenico.epay.api.dto.AccountInitResponseDTO;
import com.ingenico.epay.api.dto.TransferDTO;
import com.ingenico.epay.api.dto.TransferResponseDTO;
import com.ingenico.epay.api.exception.DuplicatePartyException;
import com.ingenico.epay.domain.Account;
import com.ingenico.epay.repository.AccountRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {TransferService.class})
public class TransferServiceTest {

    static final String TRANSFER_REFERENCE = "aaaa";

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private TransactionExecutorService transactionExecutorService;

    @Before
    public void mockDependencies() {
        Mockito.when(
                transactionExecutorService.execute(any(),any(),any()))
                .thenReturn(TRANSFER_REFERENCE);

        Mockito.when(
                accountRepository.findByIban(any())).thenReturn(new Account());

    }

    @Autowired
    private TransferService transferService;

    @Test
    public void transfer_successful() {
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setToAccount("NL11-1111-1111-1111");
        transferDTO.setFromAccount("NL11-1111-1111-1112");
        transferDTO.setAmount(new BigDecimal("100"));
        TransferResponseDTO transferResponseDTO = transferService.transfer(transferDTO);
        Assert.assertNotNull(transferResponseDTO);
        Assert.assertNotNull(transferResponseDTO.getTransferReference());
        Assert.assertEquals(transferResponseDTO.getTransferReference() , TRANSFER_REFERENCE);
    }

}
