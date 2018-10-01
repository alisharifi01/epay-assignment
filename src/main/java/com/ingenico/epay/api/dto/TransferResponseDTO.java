package com.ingenico.epay.api.dto;


public class TransferResponseDTO {

    private String transferReference;

    public TransferResponseDTO() {
    }

    public TransferResponseDTO(String transferReference) {
        this.transferReference = transferReference;
    }

    public String getTransferReference() {
        return transferReference;
    }

    public void setTransferReference(String transferReference) {
        this.transferReference = transferReference;
    }
}
