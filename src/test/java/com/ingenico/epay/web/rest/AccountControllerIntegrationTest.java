package com.ingenico.epay.web.rest;

import com.ingenico.epay.api.dto.AccountInfoDTO;
import com.ingenico.epay.api.dto.AccountInitResponseDTO;
import com.ingenico.epay.domain.Party;
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
public class AccountControllerIntegrationTest {
    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    String uri;

    HttpHeaders headers = new HttpHeaders();

    @Autowired
    PartyRepository partyRepository;

    @Before
    public void setup(){
        uri = UriComponentsBuilder.fromHttpUrl(
                        "http://localhost:" + port + "/api/v1/account/").toUriString();
        initPerson();
    }

    private void initPerson() {
        Party party = new Party();
        party.setName("John");
        partyRepository.save(party);
    }

    @Test
    public void open_successful() {
        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setOwnerName("Peter");
        accountInfoDTO.setInitBalance(new BigDecimal("1000"));

        HttpEntity<AccountInfoDTO> entity
                = new HttpEntity<AccountInfoDTO>(accountInfoDTO, headers);

        ResponseEntity<AccountInitResponseDTO> resp
                = restTemplate.exchange(uri, HttpMethod.POST, entity, AccountInitResponseDTO.class);

        assertEquals(resp.getStatusCode(),HttpStatus.CREATED);
        AccountInitResponseDTO accountInitResponseDTO = resp.getBody();
        assertNotNull(accountInitResponseDTO.getAccountIban());
        assertEquals(accountInitResponseDTO.getAccountIban().length() , 19);
    }

    @Test
    public void whenOpenForDuplicateUser_thenBadRequest() {
        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setOwnerName("John");
        accountInfoDTO.setInitBalance(new BigDecimal("1000"));

        HttpEntity<AccountInfoDTO> entity
                = new HttpEntity<AccountInfoDTO>(accountInfoDTO, headers);

        ResponseEntity<String> resp
                = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        assertEquals(resp.getStatusCode(),HttpStatus.BAD_REQUEST);
    }

    @Test
    public void whenNegativeBalance_thenBadRequest(){
        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setOwnerName("John");
        accountInfoDTO.setInitBalance(new BigDecimal("-1"));

        HttpEntity<AccountInfoDTO> entity
                = new HttpEntity<AccountInfoDTO>(accountInfoDTO, headers);

        ResponseEntity<String> resp
                = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        assertEquals(resp.getStatusCode(),HttpStatus.BAD_REQUEST);
    }

    @Test
    public void whenZeroBalance_thenBadRequest() {
        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setOwnerName("John");
        accountInfoDTO.setInitBalance(BigDecimal.ZERO);

        HttpEntity<AccountInfoDTO> entity
                = new HttpEntity<AccountInfoDTO>(accountInfoDTO, headers);

        ResponseEntity<String> resp
                = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        assertEquals(resp.getStatusCode(),HttpStatus.BAD_REQUEST);
    }

    @Test
    public void whenNullBalance_thenBadRequest() {
        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setOwnerName("John");

        HttpEntity<AccountInfoDTO> entity
                = new HttpEntity<AccountInfoDTO>(accountInfoDTO, headers);

        ResponseEntity<String> resp
                = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        assertEquals(resp.getStatusCode(),HttpStatus.BAD_REQUEST);
    }

    @Test
    public void whenNullOwnerName_thenBadRequest() {
        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setInitBalance(BigDecimal.ONE);

        HttpEntity<AccountInfoDTO> entity
                = new HttpEntity<AccountInfoDTO>(accountInfoDTO, headers);

        ResponseEntity<String> resp
                = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        assertEquals(resp.getStatusCode(),HttpStatus.BAD_REQUEST);
    }


}
