package com.ingenico.epay.web.rest;

import com.ingenico.epay.api.AccountRemote;
import com.ingenico.epay.api.dto.AccountInfoDTO;
import com.ingenico.epay.api.dto.AccountInitResponseDTO;
import com.ingenico.epay.service.AccountOpenerService;
import com.ingenico.epay.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController implements AccountRemote {

    private AccountOpenerService accountOpenerService;
    private final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    public AccountController(AccountOpenerService accountOpenerService) {
        this.accountOpenerService = accountOpenerService;
    }

    @PostMapping("/")
    public ResponseEntity<AccountInitResponseDTO> open(
            @RequestBody @Valid AccountInfoDTO accountInfoDTO) {
        AccountInitResponseDTO accountInitResponseDTO = accountOpenerService.open(accountInfoDTO);
        return new ResponseEntity<AccountInitResponseDTO>(
                accountInitResponseDTO , HttpStatus.CREATED
        );
    }
}
