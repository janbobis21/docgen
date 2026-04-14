package com.test.docgen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.docgen.entity.Municipality;
import com.test.docgen.repository.MunicipalityRepository;

@Service
public class MunicipalityService {

	@Autowired
	MunicipalityRepository municipalityRepository;
	
	public List<Municipality> getAllMunicipalities() {
		return municipalityRepository.findAll();
	}
}
