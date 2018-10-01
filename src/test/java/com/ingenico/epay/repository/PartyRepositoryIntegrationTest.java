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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase
public class PartyRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PartyRepository partyRepository;

    @Test
    public void whenExistsByName_thenReturnTrue() {
        Party party = new Party();
        party.setName("John");
        entityManager.persist(party);
        entityManager.flush();

        assertTrue(partyRepository.existsByName(party.getName()));
    }


}
