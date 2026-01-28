/*package com.mbc.mid;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import com.mbc.mid.dto.OcrResponse;
import com.mbc.mid.service.OcrService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootTest
public class OcrAccuracyTest {

    @Autowired
    private OcrService ocrService;
    //private OcrService_01 ocrService;
    //private OcrService_02 ocrService;
    //private OcrService_03 ocrService;
    //private OcrService_04 ocrService;
    //private OcrService_05 ocrService;
    //private OcrService_06 ocrService;
    //private OcrService_07 ocrService;
    //private OcrService_08 ocrService;
    //private OcrService_09 ocrService;
    //private OcrService_10 ocrService;
    //private OcrService_11 ocrService;
    
    private final String TEST_DIR_PATH = "test-images-100";
    //private final String TEST_DIR_PATH = "test-images-1000-1";
    //private final String TEST_DIR_PATH = "test-images-1000-2";
    //private final String TEST_DIR_PATH = "test-images-1000-3";

    @Test
    public void calculateDetailedMetrics() throws IOException {
        File folder = new File(TEST_DIR_PATH);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null || listOfFiles.length == 0) {
            System.out.println("! 폴더 경로 확인 필요 : " + TEST_DIR_PATH);
            return;
        }
        
        int totalCount = 0;
        int fullMatchCount = 0;
        int partialMatchCount = 0;
        int failCount = 0;

        System.out.println("====== [ 📸 상세 분석 로그 시작 ] ======\n");

        for (File file : listOfFiles) {
            if (file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".png"))) {
                totalCount++;

                String expected = file.getName().replaceFirst("[.][^.]+$", "");
                
                FileInputStream input = new FileInputStream(file);
                MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/jpeg", input);
                
                OcrResponse response = ocrService.processImage(multipartFile);
                String result = response.getResultText();
                String raw = response.getRawText(); // ★ 원본 결과 가져오기

                boolean isFullMatch = result.equals(expected);
                boolean isPartialMatch = false;

                if (!isFullMatch && result.startsWith("뒷번호")) {
                    String lastFour = expected.substring(expected.length() - 4);
                    if (result.contains(lastFour)) {
                        isPartialMatch = true;
                    }
                }

                if (isFullMatch) {
                    fullMatchCount++;
                    System.out.println("✅ 완벽인식 : " + expected);
                } else if (isPartialMatch) {
                    partialMatchCount++;
                    // ★ 수정됨: 왜 부분인식이 되었는지 원본(raw)을 보여줌
                    System.out.println("⚠️ 부분인식 : " + expected + " -> " + result);
                    System.out.println("   OCR 원본: " + raw);
                } else {
                    failCount++;
                    System.out.println("❌ 인식실패 : " + expected + " -> " + result);
                    System.out.println("   OCR 원본: " + raw);
                }
                System.out.println("--------------------------------> " + totalCount);
            }
        }

        double totalSuccessRate = (double) (fullMatchCount + partialMatchCount) / totalCount * 100;
        double fullMatchRate = (double) fullMatchCount / totalCount * 100;
        double partialMatchRate = (double) partialMatchCount / totalCount * 100;
        double partialToFullRatio = fullMatchCount > 0 ? ((double) partialMatchCount / fullMatchCount * 100) : 0;
        
        System.out.println("\n\n====== [ 📊 최종 결과 리포트 ] ======");
        System.out.println("      전체 이미지 : " + totalCount + "장");
        System.out.printf("완벽인식 + 부분인식 : %6.2f%%  (%d장)\n", totalSuccessRate, (fullMatchCount + partialMatchCount));
        System.out.printf("        완벽인식 : %6.2f%%  (%d장)\n", fullMatchRate, fullMatchCount);
        System.out.printf("        부분인식 : %6.2f%%  (%d장)\n", partialMatchRate, partialMatchCount);
        System.out.printf("부분인식 / 완벽인식 : %6.2f%%\n", partialToFullRatio);
    }
}*/