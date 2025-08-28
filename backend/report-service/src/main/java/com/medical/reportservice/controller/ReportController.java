package com.medical.reportservice.controller;

import com.medical.reportservice.dto.ReportRequest;
import com.medical.reportservice.service.ReportService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

//    @PostMapping(
//            value = "/generate/stream",
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_PDF_VALUE
//    )
//    public ResponseEntity<StreamingResponseBody> generateStream(@RequestBody @Valid ReportRequest req) {
//        InputStream in = reportService.generatePdfStream(req); // your service can return a stream
//        StreamingResponseBody body = out -> in.transferTo(out);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"health-summary.pdf\"")
//                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
//                .header("Pragma", "no-cache")
//                .contentType(MediaType.APPLICATION_PDF)
//                .body(body);
//    }

//    @PostMapping(
//            value = "/generate",
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_PDF_VALUE
//    )
//    public ResponseEntity<Resource> generate(@RequestBody @Valid ReportRequest req) {
//        byte[] pdf = reportService.generatePdf(req);
//        if (pdf == null || pdf.length == 0) {
//            return ResponseEntity.noContent().build(); // 204 if nothing generated
//        }
//        var resource = new ByteArrayResource(pdf);
//        String filename = "health-summary.pdf"; // or derive from req (sanitize!)
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
//                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
//                .header("Pragma", "no-cache")
//                .contentLength(pdf.length)
//                .contentType(MediaType.APPLICATION_PDF)
//                .body(resource);
//    }
@PostMapping(value="/generate",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_PDF_VALUE)
public ResponseEntity<Resource> generate(@RequestBody @Valid ReportRequest req) {
    byte[] pdf = reportService.generatePdf(req);
    Resource resource = new ByteArrayResource(pdf); // ← upcast
    String filename = "health-summary.pdf"; // or derive from req (sanitize!)
//    return ResponseEntity.ok()
//            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"health-summary.pdf\"")
//            .contentLength(pdf.length)
//            .contentType(MediaType.APPLICATION_PDF)
//            .body(resource); // T = Resource
    return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .header("Pragma", "no-cache")
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
}

}

