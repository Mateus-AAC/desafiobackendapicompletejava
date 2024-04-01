package br.com.maxmilhas.challenge.domain.dto;

import br.com.maxmilhas.challenge.domain.entity.CPFs;

import java.util.Date;

public class CPFsResponse {
    private Integer id;
    private String cpf;
    private Date createdAt;

    public CPFsResponse(CPFs entity) {
        this.id = entity.getId();
        this.cpf = entity.getCpf();
        this.createdAt = entity.getCreatedAt();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
