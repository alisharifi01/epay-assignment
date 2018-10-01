package com.ingenico.epay.web.rest;

import com.ingenico.epay.api.dto.TransferDTO;
import com.ingenico.epay.api.dto.TransferResponseDTO;
import com.ingenico.epay.config.ApplicationConfig;
import com.ingenico.epay.domain.Account;
import com.ingenico.epay.domain.AccountStatus;
import com.ingenico.epay.domain.Party;
import com.ingenico.epay.repository.AccountRepository;
import com.ingenico.epay.repository.PartyRepository;
import org.junit.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@Import(ApplicationConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TransferControllerIntegrationTest {

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    String uri;

    HttpHeaders headers = new HttpHeaders();

    @Autowired
    PartyRepository partyRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    EntityManagerFactory emf;

    private final String FROM_ACCOUNT_IBAN = "NL11-1111-1111-1111";
    private final String TO_ACCOUNT_IBAN = "NL11-1111-1111-1112";

    @Before
    public void setup() {
        uri = UriComponentsBuilder.fromHttpUrl(
                "http://localhost:" + port + "/api/v1/transfer/").toUriString();
        createDefaultAccounts();
    }

    @After
    public void clearData() {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.createNativeQuery("delete from transaction").executeUpdate();
        em.createNativeQuery("delete from account").executeUpdate();
        em.createNativeQuery("delete from party cascade ").executeUpdate();
        transaction.commit();
    }

    private void createDefaultAccounts() {
        //account for John
        Party partyForJohn = new Party();
        partyForJohn.setName("John");
        Account accountForJohn = new Account();
        accountForJohn.setAccountStatus(AccountStatus.ACTIVE);
        accountForJohn.setIban(FROM_ACCOUNT_IBAN);
        accountForJohn.setParty(partyForJohn);
        accountForJohn.setBalance(new BigDecimal("1000"));
        accountRepository.save(accountForJohn);

        //account for Sara
        Party partyForSara = new Party();
        partyForSara.setName("Sara");
        Account accountForSara = new Account();
        accountForSara.setAccountStatus(AccountStatus.ACTIVE);
        accountForSara.setIban(TO_ACCOUNT_IBAN);
        accountForSara.setParty(partyForSara);
        accountForSara.setBalance(new BigDecimal("1000"));
        accountRepository.save(accountForSara);
    }


    @Test
    public void transfer_successful() {
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAmount(new BigDecimal("100"));
        transferDTO.setFromAccount(FROM_ACCOUNT_IBAN);
        transferDTO.setToAccount(TO_ACCOUNT_IBAN);

        HttpEntity<TransferDTO> entity
                = new HttpEntity<TransferDTO>(transferDTO , headers);

        ResponseEntity<TransferResponseDTO> resp
                = restTemplate.exchange(uri, HttpMethod.POST, entity, TransferResponseDTO.class);

        assertEquals(resp.getStatusCode(),HttpStatus.OK);
        TransferResponseDTO transferResponseDTO = resp.getBody();
        assertNotNull(transferResponseDTO.getTransferReference());

        Account fromAccount = accountRepository.findByIban(FROM_ACCOUNT_IBAN);
        Account toAccount = accountRepository.findByIban(TO_ACCOUNT_IBAN);

        Assert.assertEquals(fromAccount.getBalance() , new BigDecimal("900.00"));
        Assert.assertEquals(toAccount.getBalance() , new BigDecimal("1100.00"));
    }

    @Test
    public void whenNegativeAmount_thenBadRequest() {

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAmount(new BigDecimal("-1"));
        transferDTO.setFromAccount(FROM_ACCOUNT_IBAN);
        transferDTO.setToAccount(TO_ACCOUNT_IBAN);

        HttpEntity<TransferDTO> entity
                = new HttpEntity<TransferDTO>(transferDTO , headers);

        ResponseEntity<String> resp
                = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        assertEquals(resp.getStatusCode(),HttpStatus.BAD_REQUEST);
    }

    @Test
    public void whenZeroAmount_thenBadRequest() {

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAmount(BigDecimal.ZERO);
        transferDTO.setFromAccount(FROM_ACCOUNT_IBAN);
        transferDTO.setToAccount(TO_ACCOUNT_IBAN);

        HttpEntity<TransferDTO> entity
                = new HttpEntity<TransferDTO>(transferDTO , headers);

        ResponseEntity<String> resp
                = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        assertEquals(resp.getStatusCode(),HttpStatus.BAD_REQUEST);
    }

    @Test
    public void whenInsufficientBalance_thenBadRequest() {

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAmount(new BigDecimal("1001"));
        transferDTO.setFromAccount(FROM_ACCOUNT_IBAN);
        transferDTO.setToAccount(TO_ACCOUNT_IBAN);

        HttpEntity<TransferDTO> entity
                = new HttpEntity<TransferDTO>(transferDTO , headers);

        ResponseEntity<String> resp
                = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        assertEquals(resp.getStatusCode(),HttpStatus.BAD_REQUEST);
    }

    @Test
    public void whenAccountIsWrong_thenBadRequest() {
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAmount(new BigDecimal("10"));
        transferDTO.setFromAccount("NL22-2222-2222-2222");
        transferDTO.setToAccount(TO_ACCOUNT_IBAN);

        HttpEntity<TransferDTO> entity
                = new HttpEntity<TransferDTO>(transferDTO , headers);

        ResponseEntity<String> resp
                = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        assertEquals(resp.getStatusCode(),HttpStatus.BAD_REQUEST);

    }

    @Test
    public void whenFromAccountAndToAccountAreSame_thenBadRequest() {
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAmount(new BigDecimal("10"));
        transferDTO.setFromAccount(FROM_ACCOUNT_IBAN);
        transferDTO.setToAccount(FROM_ACCOUNT_IBAN);

        HttpEntity<TransferDTO> entity
                = new HttpEntity<TransferDTO>(transferDTO , headers);

        ResponseEntity<String> resp
                = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        assertEquals(resp.getStatusCode(),HttpStatus.BAD_REQUEST);

    }

}
