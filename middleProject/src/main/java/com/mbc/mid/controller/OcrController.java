// middleProject - com.mbc.mid.controller - OcrController.java
package com.mbc.mid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mbc.mid.dto.OcrResponse;
import com.mbc.mid.service.OcrService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173") // Vue 포트 허용
public class OcrController {

    @Autowired
    private OcrService ocrService;

    @PostMapping("/ocr/entry")
    public OcrResponse entryImage(@RequestParam("file") MultipartFile file) {
        return ocrService.processEntryImage(file);
    }
    
    @PostMapping("/ocr/exit")
    public OcrResponse exitImage(@RequestParam("file") MultipartFile file) {
        return ocrService.processExitImage(file);
    }
}