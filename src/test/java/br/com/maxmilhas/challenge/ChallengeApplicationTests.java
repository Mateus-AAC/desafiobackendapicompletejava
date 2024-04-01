package br.com.maxmilhas.challenge;

import br.com.maxmilhas.challenge.domain.dto.CPFsCreateRequest;
import br.com.maxmilhas.challenge.domain.dto.CPFsResponse;
import br.com.maxmilhas.challenge.domain.dto.PaginatedSearchRequest;
import br.com.maxmilhas.challenge.domain.dto.ResponseBase;
import br.com.maxmilhas.challenge.domain.entity.CPFs;
import br.com.maxmilhas.challenge.repository.CPFsRepository;
import br.com.maxmilhas.challenge.service.CPFsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
class ChallengeApplicationTests {

	private CPFsRepository repository;
	private CPFsService service;

	@BeforeEach
	void setUp() {
		repository = mock(CPFsRepository.class);
		service = new CPFsService(repository);
	}


	@Test
	public void testSearch_CurrentPageNegative_ThrowBadRequestException() {
		PaginatedSearchRequest searchRequest = new PaginatedSearchRequest();
		searchRequest.setPageSize(10);
		searchRequest.setCurrentPage(-1);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> service.search(searchRequest));
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("Current page index must start at 0.", exception.getReason());
	}

	@Test
	public void testSearch_CurrentPageNull_ThrowBadRequestException() {
		PaginatedSearchRequest searchRequest = new PaginatedSearchRequest();
		searchRequest.setPageSize(10);
		searchRequest.setCurrentPage(null);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> service.search(searchRequest));
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("Current page index must start at 0.", exception.getReason());
	}

	@Test
	public void testSearch_PageSizeNull_ThrowBadRequestException() {
		PaginatedSearchRequest searchRequest = new PaginatedSearchRequest();
		searchRequest.setPageSize(null);
		searchRequest.setCurrentPage(10);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> service.search(searchRequest));
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("The number of items per page must be between 1 and 50 items.", exception.getReason());
	}

	@Test
	public void testSearch_PageSizeLessThanOne_ThrowBadRequestException() {
		PaginatedSearchRequest searchRequest = new PaginatedSearchRequest();
		searchRequest.setPageSize(0);
		searchRequest.setCurrentPage(0);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> service.search(searchRequest));
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("The number of items per page must be between 1 and 50 items.", exception.getReason());
	}

	@Test
	public void testSearch_PageSizeMoreThanFifty_ThrowBadRequestException() {
		PaginatedSearchRequest searchRequest = new PaginatedSearchRequest();
		searchRequest.setPageSize(0);
		searchRequest.setCurrentPage(51);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> service.search(searchRequest));
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("The number of items per page must be between 1 and 50 items.", exception.getReason());
	}

	@Test
	public void testSearch_EmptyList_ThrowNotFoundException() {
		PaginatedSearchRequest searchRequest = new PaginatedSearchRequest();
		searchRequest.setPageSize(1);
		searchRequest.setCurrentPage(7);

		when(repository.findAllWithNativeQuery(any())).thenReturn(Page.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> service.search(searchRequest));
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertEquals("No data found.", exception.getReason());
	}

	@Test
	public void testSearch_DataFound_ReturnPageOfCPFsResponse() {
		PaginatedSearchRequest searchRequest = new PaginatedSearchRequest();
		searchRequest.setPageSize(1);
		searchRequest.setCurrentPage(0);

		CPFs CPF = new CPFs();
		CPF.setCpf("33271469008");

		List<CPFs> cpfList = new ArrayList<>();
		cpfList.add(CPF);

		Page<CPFs> page = new PageImpl<>(cpfList);
		when(repository.findAllWithNativeQuery(any())).thenReturn(page);

		ResponseBase<Page<CPFsResponse>> response = service.search(searchRequest);

		assertNotNull(response);
		assertNotNull(response.getReturnedObject());
		assertFalse(response.getReturnedObject().isEmpty());
		assertEquals(1, response.getReturnedObject().getNumberOfElements());
		assertEquals("33271469008", response.getReturnedObject().getContent().get(0).getCpf());
	}

	@Test
	public void testSearchCPF_ValidCPFFound() {
		CPFs CPF = new CPFs();
		CPF.setCpf("33271469008");

		when(repository.findCpfByCpfWithNativeQuery("33271469008")).thenReturn(Optional.of(CPF));

		ResponseBase<CPFsResponse> response = service.searchCPF("33271469008");

		assertNotNull(response);
		assertNotNull(response.getReturnedObject());
		assertEquals("33271469008", response.getReturnedObject().getCpf());
	}

	@Test
	public void testSearchCPF_ValidCPFNotFound() {
		when(repository.findCpfByCpfWithNativeQuery("12345678900")).thenReturn(Optional.empty());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.searchCPF("12345678900"));
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertEquals("CPF not found.", exception.getReason());
	}

	@Test
	public void testSearchCPF_InvalidCPF() {
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.searchCPF("123abc"));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("The CPF must only contain numbers.", exception.getReason());
	}

	@Test
	public void testSearchCPF_EmptyCPF() {
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.searchCPF(""));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("The CPF cannot be empty.", exception.getReason());
	}

	@Test
	public void testRegister_CpfAlreadyExists() {
		CPFs existingCpf = new CPFs();
		existingCpf.setCpf("33271469008");

		CPFsCreateRequest request = new CPFsCreateRequest();
		request.setCpf("33271469008");

		when(repository.findCpfByCpfWithNativeQuery("33271469008")).thenReturn(Optional.of(existingCpf));

		ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> service.register(request));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("CPF already exists.", response.getReason());
	}

	@Test
	public void testRegister_CpfNotFound() {
		when(repository.findCpfByCpfWithNativeQuery("33271469008")).thenReturn(Optional.empty());

		CPFsCreateRequest request = new CPFsCreateRequest();
		request.setCpf("33271469008");

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.register(request));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertEquals("CPF not found.", exception.getReason());
	}

	@Test
	public void testRegister_SaveCPF() {
		CPFs existingCpf = new CPFs();
		existingCpf.setCpf("33271469008");

		when(repository.findCpfByCpfWithNativeQuery("33271469008")).thenReturn(Optional.empty()).thenReturn(Optional.of(existingCpf));
		doNothing().when(repository).saveWithNativeQuery(any(String.class));

		CPFsCreateRequest request = new CPFsCreateRequest();
		request.setCpf("33271469008");

		ResponseBase<CPFsResponse> response = service.register(request);

		assertNotNull(response);
		assertNotNull(response.getReturnedObject());
		assertEquals("33271469008", response.getReturnedObject().getCpf());
	}

	@Test
	public void testDelete_SuccessfulDeletion() {
		CPFs existingCPF = new CPFs();
		existingCPF.setCpf("12345678900");

		when(repository.findCpfByCpfWithNativeQuery("12345678900")).thenReturn(Optional.of(existingCPF));

		CPFsService service = new CPFsService(repository);

		ResponseBase<CPFsResponse> response = service.delete("12345678900");

		assertNotNull(response);
		assertNotNull(response.getReturnedObject());
		assertEquals("12345678900", response.getReturnedObject().getCpf());

		verify(repository, times(1)).deleteByCpfWithNativeQuery("12345678900");
	}

	@Test
	public void testDelete_EmptyCPF() {
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.delete(""));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("CPF cannot be empty.", exception.getReason());
	}

	@Test
	public void testDelete_InvalidCPF() {
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.delete("invalidCPF"));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("CPF must contain only numbers.", exception.getReason());
	}

	@Test
	public void testDelete_NonExistingCPF() {
		when(repository.findCpfByCpfWithNativeQuery(anyString())).thenReturn(Optional.empty());

		CPFsService service = new CPFsService(repository);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.delete("12345678900"));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
		assertEquals("CPF not found.", exception.getReason());

		verify(repository, never()).deleteByCpfWithNativeQuery(anyString());
	}
}
