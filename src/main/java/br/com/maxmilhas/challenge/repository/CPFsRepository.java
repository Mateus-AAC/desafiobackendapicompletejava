package br.com.maxmilhas.challenge.repository;

import br.com.maxmilhas.challenge.domain.entity.CPFs;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CPFsRepository extends JpaRepository<CPFs, Integer> {
    @Query(value = "SELECT * FROM CPFs", nativeQuery = true)
    Page<CPFs> findAllWithNativeQuery(Pageable pageable);

    @Query(value = "SELECT * FROM CPFs WHERE CPF = :cpf", nativeQuery = true)
    Optional<CPFs> findCpfByCpfWithNativeQuery(String cpf);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO CPFs (CPF, createdAt) VALUES (:cpf, GETDATE())", nativeQuery = true)
    void saveWithNativeQuery(String cpf);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM CPFs WHERE cpf = :cpf", nativeQuery = true)
    void deleteByCpfWithNativeQuery(String cpf);
}
