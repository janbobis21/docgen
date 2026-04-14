package com.test.docgen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.docgen.entity.Barangay;
import com.test.docgen.repository.BarangayRepository;

@Service
public class BarangayService {

	@Autowired
	BarangayRepository barangayRepository;
	
	public List<Barangay> getAllBarangaysByMunicipalityAndProvince(String municipality, String province) {
		return barangayRepository.findByMunicipalityAndProvince(municipality, province);
	}
}
