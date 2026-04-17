package com.lithoapp.patientservice.repository;

import com.lithoapp.patientservice.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {

    boolean existsByDi(String di);
    boolean existsByDmi(String dmi);
    boolean existsByDiAndIdNot(String di, Long id);
    boolean existsByDmiAndIdNot(String dmi, Long id);

    Optional<Patient> findByDi(String di);
    Optional<Patient> findByDmi(String dmi);
}
