package com.test.docgen.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.test.docgen.entity.Municipality;
import com.test.docgen.service.MunicipalityService;

@RestController
public class MunicipalityController {

	@Autowired
	MunicipalityService municipalityService;
	
	@RequestMapping(method = RequestMethod.GET, value = "/municipality")
	public List<Municipality> getAllMunicipalities(){
		return municipalityService.getAllMunicipalities();
	}
	
}
