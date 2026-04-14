package com.test.docgen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.docgen.entity.Barangay;

@Repository
public interface BarangayRepository extends JpaRepository<Barangay, Long> {

	List<Barangay> findByMunicipalityAndProvince(String municipality, String province);
	
}
