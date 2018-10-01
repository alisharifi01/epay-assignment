package com.ingenico.epay.api;

import com.ingenico.epay.api.dto.AccountInfoDTO;
import com.ingenico.epay.api.dto.AccountInitResponseDTO;
import org.springframework.http.ResponseEntity;

public interface AccountRemote {

    ResponseEntity<AccountInitResponseDTO> open(AccountInfoDTO accountInfoDTO);
}
