package com.ingenico.epay.service;

import com.ingenico.epay.api.dto.AccountInfoDTO;
import com.ingenico.epay.api.dto.AccountInitResponseDTO;
import com.ingenico.epay.api.exception.DuplicatePartyException;
import com.ingenico.epay.domain.Account;
import com.ingenico.epay.domain.AccountStatus;
import com.ingenico.epay.domain.Party;
import com.ingenico.epay.domain.mapper.AccountInfoDTOAccountEntityMapper;
import com.ingenico.epay.domain.mapper.AccountInfoDTOPartyEntityMapper;
import com.ingenico.epay.repository.AccountRepository;
import com.ingenico.epay.repository.PartyRepository;
import com.ingenico.epay.util.IbanGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountOpenerService {

    private AccountRepository accountRepository;
    private IbanGenerator accountNumberGenerator;
    private PartyRepository partyRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(AccountOpenerService.class);

    @Autowired
    public AccountOpenerService(AccountRepository accountRepository, IbanGenerator accountNumberGenerator, PartyRepository partyRepository) {
        this.accountRepository = accountRepository;
        this.accountNumberGenerator = accountNumberGenerator;
        this.partyRepository = partyRepository;
    }

    public AccountInitResponseDTO open(AccountInfoDTO accountInfoDTO) {
        checkDuplicateParty(accountInfoDTO.getOwnerName());
        LOGGER.info("party is valid");

        Party party = AccountInfoDTOPartyEntityMapper.INSTANCE.toParty(accountInfoDTO);
        LOGGER.info("party has been mapped from accountInfoDTO");

        Account account = AccountInfoDTOAccountEntityMapper.INSTANCE.toAccountEntity(accountInfoDTO);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setIban(accountNumberGenerator.generate());
        account.setParty(party);
        LOGGER.info("account entity has been mapped");

        accountRepository.save(account);
        LOGGER.info("account has been saved");

        return new AccountInitResponseDTO(account.getIban());
    }

    private void checkDuplicateParty(String name) {
        if(partyRepository.existsByName(name)) {
            throw new DuplicatePartyException();
        }
    }
}
