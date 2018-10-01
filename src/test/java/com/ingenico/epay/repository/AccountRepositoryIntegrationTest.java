package com.ingenico.epay.repository;

import com.ingenico.epay.domain.Account;
import com.ingenico.epay.domain.AccountStatus;
import com.ingenico.epay.domain.Party;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase
public class AccountRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void whenFindByIban_thenReturnAccount() {
        Party party = new Party();
        party.setName("John");

        Account account = new Account();
        account.setIban("NL91-1584-4455-5390");
        account.setBalance(BigDecimal.ZERO);
        account.setParty(party);
        account.setAccountStatus(AccountStatus.ACTIVE);

        entityManager.persist(account);
        entityManager.flush();

        Account found = accountRepository.findByIban(account.getIban());

        assertEquals(found.getIban() ,account.getIban());
    }


}
