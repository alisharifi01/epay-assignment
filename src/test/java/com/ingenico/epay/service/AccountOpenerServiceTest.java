package com.ingenico.epay.service;

import com.ingenico.epay.api.dto.AccountInfoDTO;
import com.ingenico.epay.api.dto.AccountInitResponseDTO;
import com.ingenico.epay.api.exception.DuplicatePartyException;
import com.ingenico.epay.domain.Account;
import com.ingenico.epay.domain.mapper.AccountInfoDTOAccountEntityMapper;
import com.ingenico.epay.domain.mapper.AccountInfoDTOPartyEntityMapper;
import com.ingenico.epay.repository.AccountRepository;
import com.ingenico.epay.repository.PartyRepository;
import com.ingenico.epay.service.AccountOpenerService;
import com.ingenico.epay.util.IbanGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {AccountOpenerService.class})
public class AccountOpenerServiceTest {

    @MockBean
    private PartyRepository partyRepository;

    @MockBean
    private AccountInfoDTOAccountEntityMapper accountInfoDTOAccountEntityMapper;

    @MockBean
    private AccountInfoDTOPartyEntityMapper accountInfoDTOPartyEntityMapper;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private IbanGenerator ibanGenerator;

    @Autowired
    private AccountOpenerService accountOpenerService;

    @Before
    public void mockDependencies() {
        Mockito.when(ibanGenerator.generate()).thenReturn("NL11-1111-1111-1111");
        Mockito.when(partyRepository.existsByName("John")).thenReturn(true);
        Mockito.when(accountInfoDTOAccountEntityMapper.toAccountEntity(any())).thenReturn(
                new Account()
        );

    }

    @Test
    public void open_successful() {
        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setOwnerName("Sara");
        accountInfoDTO.setInitBalance(new BigDecimal("1000"));
        AccountInitResponseDTO accountInitResponseDTO = accountOpenerService.open(accountInfoDTO);
        Assert.assertNotNull(accountInitResponseDTO);
        Assert.assertNotNull(accountInitResponseDTO.getAccountIban());
        Assert.assertEquals(accountInitResponseDTO.getAccountIban().length(), 19);
    }

    @Test(expected = DuplicatePartyException.class)
    public void openWithJohn_thenDuplicatePartyException() {
        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setOwnerName("John");
        accountInfoDTO.setInitBalance(new BigDecimal("1000"));
        accountOpenerService.open(accountInfoDTO);
    }

}
