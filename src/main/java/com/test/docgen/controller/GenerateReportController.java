package com.test.docgen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.test.docgen.service.BarangayReportService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/generate")
@CrossOrigin(origins = "*") // for local dev; tighten in prod
public class GenerateReportController {

	@Autowired
    BarangayReportService reportService;

    @GetMapping("/barangay")
    public ResponseEntity<StreamingResponseBody> generate(
            @RequestParam String municipality,
            @RequestParam String province,
            @RequestParam String doctype
    ) {
        String dt = doctype == null ? "" : doctype.trim().toUpperCase();
        String safeMunicipality = municipality == null ? "" : municipality.trim();
        String safeProvince = province == null ? "" : province.trim();

        if (safeMunicipality.isBlank() || safeProvince.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        if ("CSV".equals(dt)) {
            String filename = "barangays_" + safeMunicipality + "_" + safeProvince + "_" + timestamp + ".csv";
            StreamingResponseBody body = os -> reportService.writeCsv(os, safeMunicipality, safeProvince);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(filename))
                    .header(HttpHeaders.CONTENT_ENCODING, StandardCharsets.UTF_8.name())
                    .body(body);
        }

        if ("PDF".equals(dt)) {
            String filename = "barangays_" + safeMunicipality + "_" + safeProvince + "_" + timestamp + ".pdf";
            StreamingResponseBody body = os -> reportService.writePdf(os, safeMunicipality, safeProvince);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(filename))
                    .body(body);
        }

        return ResponseEntity.badRequest().build();
    }

    private String contentDisposition(String filename) {
        // simple attachment header; browsers will download
        return "attachment; filename=\"" + filename + "\"";
    }
}

