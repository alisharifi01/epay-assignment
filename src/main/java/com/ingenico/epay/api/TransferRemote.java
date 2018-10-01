package com.ingenico.epay.api;

import com.ingenico.epay.api.dto.TransferDTO;
import com.ingenico.epay.api.dto.TransferResponseDTO;
import org.springframework.http.ResponseEntity;

public interface TransferRemote {

    ResponseEntity<TransferResponseDTO> transfer(TransferDTO transferDTO);
}
