package com.ingenico.epay.web.rest;

import com.ingenico.epay.api.TransferRemote;
import com.ingenico.epay.api.dto.TransferDTO;
import com.ingenico.epay.api.dto.TransferResponseDTO;
import com.ingenico.epay.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/transfer")
public class TransferController implements TransferRemote {

    private TransferService transferService;

    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/")
    public ResponseEntity<TransferResponseDTO> transfer(
            @RequestBody @Valid TransferDTO transferDTO) {
        TransferResponseDTO transferResponseDTO = transferService.transfer(transferDTO);
        return new ResponseEntity<>(transferResponseDTO , HttpStatus.OK);
    }

}
