package com.test.docgen.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.test.docgen.entity.Barangay;
import com.test.docgen.service.BarangayService;

@RestController
public class BarangayController {

	@Autowired
	BarangayService barangayService;
	
	@RequestMapping(method = RequestMethod.GET, value = "/barangay")
	public List<Barangay> getAllBarangaysByMunicipalityAndProvince(@RequestParam String municipality, @RequestParam String province){
		return barangayService.getAllBarangaysByMunicipalityAndProvince(municipality, province);
	}
	
}
