package com.ingenico.epay;

import com.ingenico.epay.api.dto.AccountInfoDTO;
import com.ingenico.epay.api.dto.AccountInitResponseDTO;
import com.ingenico.epay.api.dto.TransferDTO;
import com.ingenico.epay.api.dto.TransferResponseDTO;
import com.ingenico.epay.domain.Account;
import com.ingenico.epay.domain.Party;
import com.ingenico.epay.repository.AccountRepository;
import com.ingenico.epay.repository.PartyRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;
import java.math.BigDecimal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@AutoConfigurationPackage
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WholeScenarioTest {

        @LocalServerPort
        private int port;

        TestRestTemplate restTemplate = new TestRestTemplate();

        String openAccountUri;
        String transferUri;
        String BASE_URL;

        HttpHeaders headers = new HttpHeaders();

        @Autowired
        AccountRepository accountRepository;

        @Before
        public void setup() {
            BASE_URL = "http://localhost:" + port + "/api/v1/";
            openAccountUri = UriComponentsBuilder.fromHttpUrl(BASE_URL + "account/").toUriString();
            transferUri = UriComponentsBuilder.fromHttpUrl(BASE_URL + "transfer/").toUriString();
        }

        @Test
        public void successfulScenario() {

            //Peter Account
            AccountInfoDTO accountInfoDTOForPeter = new AccountInfoDTO();
            accountInfoDTOForPeter.setOwnerName("Peter");
            accountInfoDTOForPeter.setInitBalance(new BigDecimal("1000"));

            HttpEntity<AccountInfoDTO> entity
                    = new HttpEntity<AccountInfoDTO>(accountInfoDTOForPeter , headers);

            ResponseEntity<AccountInitResponseDTO> fromAccountResponse
                    = restTemplate.exchange(openAccountUri, HttpMethod.POST, entity, AccountInitResponseDTO.class);

            assertEquals(fromAccountResponse.getStatusCode() , HttpStatus.CREATED);
            String fromAccountIban = fromAccountResponse.getBody().getAccountIban();

            //Sara Account
            AccountInfoDTO accountInfoDTOForSara = new AccountInfoDTO();
            accountInfoDTOForSara.setOwnerName("Sara");
            accountInfoDTOForSara.setInitBalance(new BigDecimal("1000"));

            entity = new HttpEntity<AccountInfoDTO>(accountInfoDTOForSara, headers);

            ResponseEntity<AccountInitResponseDTO> toAccountResponse
                    = restTemplate.exchange(openAccountUri, HttpMethod.POST, entity, AccountInitResponseDTO.class);

            assertEquals(toAccountResponse.getStatusCode() , HttpStatus.CREATED);
            String toAccountIban = toAccountResponse.getBody().getAccountIban();

            //transfer
            TransferDTO transferDTO = new TransferDTO();
            transferDTO.setAmount(new BigDecimal("100"));
            transferDTO.setFromAccount(fromAccountIban);
            transferDTO.setToAccount(toAccountIban);

            HttpEntity<TransferDTO> entityForTransfer
                    = new HttpEntity<TransferDTO>(transferDTO, headers);

            ResponseEntity<TransferResponseDTO> transferResponse
                    = restTemplate.exchange(transferUri, HttpMethod.POST, entityForTransfer, TransferResponseDTO.class);

            assertEquals(transferResponse.getStatusCode(),HttpStatus.OK);
            TransferResponseDTO transferResponseDTO = transferResponse.getBody();
            assertNotNull(transferResponseDTO.getTransferReference());

            accountRepository.findByIban(fromAccountIban);
            Account fromAccount = accountRepository.findByIban(fromAccountIban);
            Account toAccount = accountRepository.findByIban(toAccountIban);

            assertEquals(fromAccount.getBalance() , new BigDecimal("900.00"));
            assertEquals(toAccount.getBalance() , new BigDecimal("1100.00"));
        }

}
