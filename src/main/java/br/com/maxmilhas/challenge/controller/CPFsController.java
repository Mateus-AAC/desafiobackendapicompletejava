package br.com.maxmilhas.challenge.controller;

import br.com.maxmilhas.challenge.domain.dto.CPFsCreateRequest;
import br.com.maxmilhas.challenge.domain.dto.CPFsResponse;
import br.com.maxmilhas.challenge.domain.dto.PaginatedSearchRequest;
import br.com.maxmilhas.challenge.domain.dto.ResponseBase;
import br.com.maxmilhas.challenge.service.CPFsService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CPFsController {
    private final CPFsService service;

    @Autowired
    public CPFsController(CPFsService service) {
        this.service = service;
    }

    @GetMapping(value = "api/cpf")
    public ResponseEntity search(PaginatedSearchRequest newRequest) {
        ResponseBase<Page<CPFsResponse>> modelListReturn = service.search(newRequest);
        return ResponseEntity.ok(modelListReturn);
    }

    @GetMapping(value = "api/cpf/{newRequest}")
    public ResponseEntity searchCPF(@PathVariable String newRequest) {
        ResponseBase<CPFsResponse> modelReturn = service.searchCPF(newRequest);
        return ResponseEntity.ok(modelReturn);
    }

    @PostMapping(value = "api/cpf")
    public ResponseEntity register(@Valid @RequestBody CPFsCreateRequest newRequest) {
        ResponseBase<CPFsResponse> modelReturn = service.register(newRequest);
        return ResponseEntity.ok(modelReturn);
    }

    @DeleteMapping(value = "api/cpf/{newRequest}")
    public ResponseEntity delete(@PathVariable String newRequest) {
        ResponseBase<CPFsResponse> modelReturn = service.delete(newRequest);
        return ResponseEntity.ok(modelReturn);
    }
}
