package br.com.maxmilhas.challenge.service;

import br.com.maxmilhas.challenge.domain.dto.CPFsCreateRequest;
import br.com.maxmilhas.challenge.domain.dto.CPFsResponse;
import br.com.maxmilhas.challenge.domain.dto.PaginatedSearchRequest;
import br.com.maxmilhas.challenge.domain.dto.ResponseBase;
import br.com.maxmilhas.challenge.domain.entity.CPFs;
import br.com.maxmilhas.challenge.repository.CPFsRepository;

import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class CPFsService {
    private final CPFsRepository repository;

    @Autowired
    public CPFsService(CPFsRepository repository) {
        this.repository = repository;
    }

    public ResponseBase<Page<CPFsResponse>> search(PaginatedSearchRequest searchRequest) {
        if (searchRequest.getCurrentPage() == null || searchRequest.getCurrentPage() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current page index must start at 0.");
        }

        if (searchRequest.getPageSize() == null || searchRequest.getPageSize() < 1 || searchRequest.getPageSize() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The number of items per page must be between 1 and 50 items.");
        }

        PageRequest pageRequest = PageRequest.of(searchRequest.getCurrentPage(), searchRequest.getPageSize());

        Page<CPFs> listData = repository.findAllWithNativeQuery(pageRequest);

        if (listData.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data found.");
        }

        Page<CPFsResponse> listResponse = listData.map(CPFsResponse::new);

        return new ResponseBase<>(listResponse);
    }

    public ResponseBase<CPFsResponse> searchCPF(String newRequest) {
        if (newRequest == null || newRequest.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The CPF cannot be empty.");
        }

        if (!newRequest.matches("\\d+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The CPF must only contain numbers.");
        }

        Optional<CPFs> modelOptional = repository.findCpfByCpfWithNativeQuery(newRequest);

        CPFs model = modelOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CPF not found."));

        CPFsResponse modelReturn = new CPFsResponse(model);

        return new ResponseBase<>(modelReturn);
    }


    public ResponseBase<CPFsResponse> register(CPFsCreateRequest newRequest) {
        Optional<CPFs> existingCPF = repository.findCpfByCpfWithNativeQuery(newRequest.getCpf());

        if (existingCPF.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF already exists.");
        }

        repository.saveWithNativeQuery(newRequest.getCpf());

        Optional<CPFs> savedCPF = repository.findCpfByCpfWithNativeQuery(newRequest.getCpf());

        CPFs model = savedCPF.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CPF not found."));

        CPFsResponse modelReturn = new CPFsResponse(model);

        return new ResponseBase<>(modelReturn);
    }

    public ResponseBase<CPFsResponse> delete(String newRequest) {
        if (newRequest == null ||newRequest.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF cannot be empty.");
        }

        if (!newRequest.matches("\\d+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF must contain only numbers.");
        }

        Optional<CPFs> cpfToDeleteOptional = repository.findCpfByCpfWithNativeQuery(newRequest);

        if (cpfToDeleteOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CPF not found.");
        }

        repository.deleteByCpfWithNativeQuery(newRequest);

        CPFsResponse modelReturn = new CPFsResponse(cpfToDeleteOptional.get());

        return new ResponseBase<>(modelReturn);
    }
}
