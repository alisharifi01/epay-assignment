package com.ingenico.epay.api.dto;

public class AccountInitResponseDTO {

    private String accountIban;

    public AccountInitResponseDTO() {
    }

    public AccountInitResponseDTO(String accountIban) {
        this.accountIban = accountIban;
    }

    public String getAccountIban() {
        return accountIban;
    }

    public void setAccountIban(String accountIban) {
        this.accountIban = accountIban;
    }
}
