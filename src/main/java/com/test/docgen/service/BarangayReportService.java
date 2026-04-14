package com.test.docgen.service;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.docgen.entity.Barangay;
import com.test.docgen.util.PdfBoxBarangayWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class BarangayReportService {

	@Autowired
    BarangayService barangayService;
   

    public void writeCsv(OutputStream os, String municipality, String province) throws IOException {
        List<Barangay> barangayList = barangayService.getAllBarangaysByMunicipalityAndProvince(municipality, province);

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
             CSVPrinter printer = new CSVPrinter(writer,
                     CSVFormat.DEFAULT.builder()
                             .setHeader("Barangay", "Municipality", "Province")
                             .build()
             )) {

            for (Barangay barangay : barangayList) {
                printer.printRecord(barangay.getBarangay(), barangay.getMunicipality(), barangay.getProvince());
            }
            printer.flush();
        }
    }

    public void writePdf(OutputStream os, String municipality, String province) throws IOException {
        // Implemented below
        PdfBoxBarangayWriter.write(os, barangayService.getAllBarangaysByMunicipalityAndProvince(municipality, province), municipality, province);
    }
}