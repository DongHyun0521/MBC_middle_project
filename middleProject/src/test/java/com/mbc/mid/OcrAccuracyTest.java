package com.mbc.mid;

import com.mbc.mid.dao.MemDao;
import com.mbc.mid.dao.ParkingLogDao;
import com.mbc.mid.dto.OcrResponse;
import com.mbc.mid.dto.ParkingLogDto;
import com.mbc.mid.service.OcrService;
import com.mbc.mid.service.ParkingSpotService;
import com.mbc.mid.service.PaymentService;
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

import static org.mockito.ArgumentMatchers.any;
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
    
    @MockitoBean
    private ParkingSpotService parkingSpotService;

    @MockitoBean
    private PaymentService paymentService;
    
    // 테스트할 이미지 폴더 경로 (프로젝트 루트 기준)
    private final String TEST_DIR_PATH = "license_plates_AIhub";
    
    // 완벽 인식된 이미지를 저장할 폴더 이름
    private final String SUCCESS_DIR_PATH = "_license_plates_AIhub";
    
    @BeforeEach
    public void setup() {
        // 1. 최근 입차 기록 조회 (차량번호 String -> null 반환)
        given(parkingLogDao.selectRecentEntryLog(anyString())).willReturn(null);
        
        // 2. 입차 기록 삽입 (DTO 객체 -> 아무것도 안 함)
        doNothing().when(parkingLogDao).insertEntryLog(any(ParkingLogDto.class));
        
        // 3. 회원 확인
        given(memDao.checkMemberVehicle(anyString())).willReturn(0);
        given(memDao.getMemIdByVehicle(anyString())).willReturn(null);
        
        // 4. 주차 공간 배정 (null 반환하여 DB 로직 건너뜀)
        given(parkingSpotService.findSpotIdByParkingLogId(any())).willReturn(null);
        given(parkingSpotService.recommendSpot()).willReturn(null);
    }

    @Test
    public void calculateDetailedMetrics() throws IOException {
        File folder = new File(TEST_DIR_PATH);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null || listOfFiles.length == 0) {
            System.err.println("❌ [오류] 테스트할 이미지가 없습니다. 경로를 확인하세요: " + folder.getAbsolutePath());
            if (!folder.exists()) {
                folder.mkdirs();
                System.out.println("ℹ️ 테스트용 폴더를 생성했습니다: " + folder.getAbsolutePath());
            }
            return;
        }

        // 성공 이미지 저장용 폴더 생성
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

        System.out.println("\n====== [ 📸 OCR 상세 분석 로그 시작 (전처리 6 적용) ] ======\n");

        for (File file : listOfFiles) {
            if (file.isFile() && isImageFile(file.getName())) {
                totalCount++;
                String expected = file.getName().replaceFirst("[.][^.]+$", ""); // 확장자 제거
                
                FileInputStream input = null;
                try {
                    input = new FileInputStream(file);
                    MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/jpeg", input);
                    
                    // 실제 서비스 호출 (1차 -> 2차 로직 포함됨)
                    OcrResponse response = ocrService.processEntryImage(multipartFile);
                    
                    String result = response.getResultText();
                    String raw = response.getRawText();

                    boolean isFullMatch = false;
                    boolean isPartialMatch = false;

                    if (result != null && result.replaceAll("\\s", "").equals(expected.replaceAll("\\s", ""))) {
                        isFullMatch = true;
                    } else if (result != null && result.startsWith("뒷번호")) {
                        if (expected.length() >= 4) {
                            String expectedLastFour = expected.substring(expected.length() - 4);
                            if (result.contains(expectedLastFour)) {
                                isPartialMatch = true;
                            }
                        }
                    }

                    String statusIcon;
                    String statusText;

                    if (isFullMatch) {
                        fullMatchCount++;
                        statusIcon = "✅";
                        statusText = "완벽인식";
                        
                        try {
                            Path sourcePath = file.toPath();
                            Path destPath = new File(successFolder, file.getName()).toPath();
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
                    
                    if (isFullMatch) {
                        System.out.println("      💾 [저장] " + successFolder.getName() + " 폴더로 복사됨");
                    }
                    
                    if (!isFullMatch && raw != null) {
                        System.out.printf("      (원 본 : %s)\n", raw.replace("\n", " ").trim());
                    }

                } catch (Exception e) {
                    System.out.printf("[%03d] 🚨 에러 발생 (%s): %s\n", totalCount, file.getName(), e.getMessage());
                    failCount++;
                } finally {
                    if (input != null) {
                        try { input.close(); } catch (IOException e) { /* 무시 */ }
                    }
                }
            }
        }

        if (totalCount > 0) {
            double totalSuccessRate = (double) (fullMatchCount + partialMatchCount) / totalCount * 100;
            double fullMatchRate = (double) fullMatchCount / totalCount * 100;
            double partialMatchRate = (double) partialMatchCount / totalCount * 100;
            double partialToFullRatio = fullMatchCount > 0 ? ((double) partialMatchCount / fullMatchCount * 100) : 0;

            System.out.println("\n\n====== [ 📊 최종 결과 리포트 (전처리 6) ] ======");
            System.out.println("      전체 이미지 : " + totalCount + "장");
            System.out.println("-------------------------------------");
            System.out.printf("✅ 완벽인식 : %d장 (%5.2f%%)\n", fullMatchCount, fullMatchRate);
            System.out.printf("⚠️ 부분인식 : %d장 (%5.2f%%)\n", partialMatchCount, partialMatchRate);
            System.out.printf("❌ 인식실패 : %d장 (%5.2f%%)\n", failCount, (double)failCount/totalCount*100);
            System.out.println("-------------------------------------");
            System.out.printf("🏆 총 성공률 (완벽+부분) : %5.2f%%\n", totalSuccessRate);
            System.out.printf("ℹ️ 부분/완벽 비율         : %5.2f%%\n", partialToFullRatio);
            System.out.println("📂 완벽 인식된 파일 저장 경로: " + successFolder.getAbsolutePath());
        } else {
            System.out.println("\n⚠️ 테스트할 이미지가 없어 결과를 출력할 수 없습니다.");
        }
    }
    
    private boolean isImageFile(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".png") || lower.endsWith(".jpeg");
    }
}