package com.mbc.mid;

import com.mbc.mid.dao.MemDao;
import com.mbc.mid.dao.ParkingLogDao;
import com.mbc.mid.dto.OcrResponse;
import com.mbc.mid.service.OcrService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
public class OcrAccuracyTest {

    @Autowired
    private OcrService ocrService;

    @MockitoBean
    private ParkingLogDao parkingLogDao;

    @MockitoBean
    private MemDao memDao;
    
    // 테스트할 이미지 폴더 경로
    private final String TEST_DIR_PATH = "test-images-1000-3";
    
    // [추가] 완벽 인식된 이미지를 저장할 폴더 이름
    private final String SUCCESS_DIR_PATH = "success-images";

    @BeforeEach
    public void setup() {
        given(parkingLogDao.selectRecentEntryLog(anyString())).willReturn(null);
        doNothing().when(parkingLogDao).insertEntryLog(anyString());
        given(memDao.checkMemberVehicle(anyString())).willReturn(0);
        given(memDao.getMemIdByVehicle(anyString())).willReturn(null);
    }

    @Test
    public void calculateDetailedMetrics() throws IOException {
        File folder = new File(TEST_DIR_PATH);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null || listOfFiles.length == 0) {
            System.out.println("! 폴더 경로 확인 필요 : " + folder.getAbsolutePath());
            return;
        }

        // [추가] 성공 이미지 저장용 폴더 생성 (없으면 생성)
        File successFolder = new File(SUCCESS_DIR_PATH);
        if (!successFolder.exists()) {
            successFolder.mkdirs();
            System.out.println("📂 저장 폴더 생성됨: " + successFolder.getAbsolutePath());
        }

        Arrays.sort(listOfFiles, Comparator.comparing(File::getName));

        int totalCount = 0;
        int fullMatchCount = 0;
        int partialMatchCount = 0;
        int failCount = 0;

        System.out.println("====== [ 📸 상세 분석 로그 시작 ] ======\n");

        for (File file : listOfFiles) {
            if (file.isFile() && (file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".png"))) {
                totalCount++;

                String expected = file.getName().replaceFirst("[.][^.]+$", "");
                
                FileInputStream input = new FileInputStream(file);
                MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/jpeg", input);
                
                OcrResponse response = ocrService.processEntryImage(multipartFile);
                
                String result = response.getResultText();
                String raw = response.getRawText();

                // --- 판정 로직 ---
                boolean isFullMatch = false;
                boolean isPartialMatch = false;

                if (result.replaceAll("\\s", "").equals(expected.replaceAll("\\s", ""))) {
                    isFullMatch = true;
                } else if (result.startsWith("뒷번호")) {
                    if (expected.length() >= 4) {
                        String expectedLastFour = expected.substring(expected.length() - 4);
                        if (result.contains(expectedLastFour)) {
                            isPartialMatch = true;
                        }
                    }
                }

                // --- 결과 출력 및 파일 복사 ---
                String statusIcon;
                String statusText;

                if (isFullMatch) {
                    fullMatchCount++;
                    statusIcon = "✅";
                    statusText = "완벽인식";
                    
                    // [추가] 완벽 인식 시 파일 복사 로직
                    try {
                        Path sourcePath = file.toPath();
                        Path destPath = new File(successFolder, file.getName()).toPath();
                        // 파일 복사 (이미 있으면 덮어쓰기)
                        Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        System.out.println("      ❗ 파일 복사 실패: " + e.getMessage());
                    }
                    
                } else if (isPartialMatch) {
                    partialMatchCount++;
                    statusIcon = "⚠️";
                    statusText = "부분인식";
                } else {
                    failCount++;
                    statusIcon = "❌";
                    statusText = "인식실패";
                }

                System.out.println("--------------------------------------------------");
                System.out.printf("[%03d] 파일명 : %s\n", totalCount, file.getName());
                System.out.printf("      판  정 : %s %s\n", statusIcon, statusText);
                System.out.printf("      정  답 : %s\n", expected);
                System.out.printf("      추  출 : %s\n", result);
                
                // 완벽 인식일 때 복사 메시지 출력
                if (isFullMatch) {
                    System.out.println("      💾 [저장] success-images 폴더로 복사됨");
                }
                
                if (!isFullMatch) {
                    System.out.printf("      (원 본 : %s)\n", raw.replace("\n", " ").trim());
                }
                
                input.close();
            }
        }

        // 최종 통계 출력
        if (totalCount > 0) {
            double totalSuccessRate = (double) (fullMatchCount + partialMatchCount) / totalCount * 100;
            double fullMatchRate = (double) fullMatchCount / totalCount * 100;
            double partialMatchRate = (double) partialMatchCount / totalCount * 100;
            double partialToFullRatio = fullMatchCount > 0 ? ((double) partialMatchCount / fullMatchCount * 100) : 0;

            System.out.println("\n\n====== [ 📊 최종 결과 리포트 ] ======");
            System.out.println("      전체 이미지 : " + totalCount + "장");
            System.out.println("-------------------------------------");
            System.out.printf("✅ 완벽인식 : %d장 (%5.2f%%)\n", fullMatchCount, fullMatchRate);
            System.out.printf("⚠️ 부분인식 : %d장 (%5.2f%%)\n", partialMatchCount, partialMatchRate);
            System.out.printf("❌ 인식실패 : %d장 (%5.2f%%)\n", failCount, (double)failCount/totalCount*100);
            System.out.println("-------------------------------------");
            System.out.printf("🏆 총 성공률 (완벽+부분) : %5.2f%%\n", totalSuccessRate);
            System.out.printf("ℹ️ 부분/완벽 비율        : %5.2f%%\n", partialToFullRatio);
            System.out.println("📂 완벽 인식된 파일 저장 경로: " + successFolder.getAbsolutePath());
        }
    }
}