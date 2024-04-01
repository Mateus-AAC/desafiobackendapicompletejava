package br.com.maxmilhas.challenge.domain.dto;

import br.com.maxmilhas.challenge.utils.CPFsValidationUtils;

public class CPFsCreateRequest {
    private String cpf;

    public String getCpf() { return cpf; }

    public void setCpf(String cpf) {
        if (CPFsValidationUtils.isValidCPF(cpf)) {
            this.cpf = cpf;
        } else {
            throw new IllegalArgumentException("Enter a valid CPF without using dashes and dots");
        }
    }
}
