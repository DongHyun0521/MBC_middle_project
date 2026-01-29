package com.mbc.mid.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mbc.mid.dao.MemDao;
import com.mbc.mid.dao.ParkingLogDao;
import com.mbc.mid.dto.OcrResponse;
import com.mbc.mid.dto.ParkingLogDto;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class OcrService {
    
    @Autowired
    private ParkingLogDao parkingLogDao;
    
    @Autowired
    private MemDao memDao;
    
    // 입차 시
    public OcrResponse processEntryImage(MultipartFile file) {
    	System.out.println("=> OcrService: processEntryImage | "+ new Date());
        return processImageCommon(file, "ENTRY");
    }

    // 출차 시
    public OcrResponse processExitImage(MultipartFile file) {
    	System.out.println("=> OcrService: processExitImage | "+ new Date());
        return processImageCommon(file, "EXIT");
    }

    // 번호판에 OCR 적용
    private OcrResponse processImageCommon(MultipartFile file, String type) {
    	System.out.println("=> OcrService: processImageCommon | "+ new Date());
        List<String> debugImages = new ArrayList<>();
        File tempFile = null;

        try {
            // 이미지 읽기
        	BufferedImage original = ImageIO.read(file.getInputStream());
            debugImages.add(imageToBase64(original));

            // 전처리
            BufferedImage processedImage = preprocessBoldBlur(original);
            debugImages.add(imageToBase64(processedImage));

            // 테서랙트 OCR용 임시 파일 생성
            tempFile = File.createTempFile("ocr_target_", ".png");
            ImageIO.write(processedImage, "png", tempFile);

            // 테서랙트 OCR 설정
            ITesseract instance = new Tesseract();
            instance.setDatapath("tessdata");
            instance.setLanguage("kor+eng");
            instance.setPageSegMode(7);
            instance.setOcrEngineMode(1);
            // 인식률 높이기 위한 허용 숫자+한글 목록
            instance.setTessVariable("tessedit_char_whitelist",
            		"0123456789"
            		+ "가나다라마바사아자차카타파하거너더러머버서어저처커터퍼허고노도로모보소오조초코토포호구누두루무부수우주추쿠투푸후그느드르므브스으즈츠크트프흐육해공국합");
            // OCR 실행
            String rawResult = instance.doOCR(tempFile).replace("\n", "").trim();
            // OCR 실행해서 나온 번호판 결과
            String finalResult = parseLicensePlate(rawResult);
            
            String entryTimeStr = "";
            String exitTimeStr = "";
            Integer parkingFee = -1;
            Boolean isMember = false;

            // OCR 성공 시 DB 로직 수행
            if (isValidResult(finalResult)) {
                
                if (type.equals("ENTRY")) {
                    // [입차 로직]
                    parkingLogDao.insertEntryLog(finalResult);
                    entryTimeStr = formatDateTime(LocalDateTime.now());
                    parkingFee = 0;
                    
                } else if (type.equals("EXIT")) {
                    ParkingLogDto log = parkingLogDao.selectRecentEntryLog(finalResult);
                    Long memId = null; // ★ 회원 ID 담을 변수

                    if (log != null) {
                        // 회원 여부 및 ID 확인
                        // (DAO에 메서드 하나 추가 필요: vehicleNum으로 MemDto나 memId 조회)
                        // 간단하게 하기 위해 checkVehicleMembership을 수정하거나, 여기서 memId를 구하는 로직 추가
                        // 여기서는 기존 로직 활용:
                        int count = memDao.checkMemberVehicle(finalResult);
                        isMember = (count > 0);
                        
                        if (isMember) {
                            // 회원이면 memId 가져오기 (MemDao에 메서드 추가 필요, 아래 3번 참고)
                            memId = memDao.getMemIdByVehicle(finalResult);
                        }

                        // DB 업데이트 (출차시간, 회원여부)
                        log.setIsMember(isMember);
                        parkingLogDao.updateExitLog(log);

                        // 요금 계산
                        LocalDateTime inTime = log.getEntryTime();
                        LocalDateTime outTime = LocalDateTime.now();
                        parkingFee = calculateFee(inTime, outTime, isMember);

                        entryTimeStr = formatDateTime(inTime);
                        exitTimeStr = formatDateTime(outTime);
                        
                        // ★ Log ID 저장
                        // log객체에 ID가 들어있음
                        return new OcrResponse(finalResult, rawResult, debugImages, entryTimeStr, exitTimeStr, isMember, parkingFee, log.getParkingLogId(), memId);
                    }
                }
            }
            return new OcrResponse(finalResult, rawResult, debugImages, entryTimeStr, exitTimeStr, isMember, parkingFee, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new OcrResponse("에러", "에러", debugImages, "에러", "에러", false, -1, null, null);
        } finally {
            if (tempFile != null) tempFile.delete();
        }
    }

    // 요금 계산 함수
    private Integer calculateFee(LocalDateTime in, LocalDateTime out, boolean isMember) {
    	System.out.println("=> OcrService: calculateFee | "+ new Date());
        long minutes = Duration.between(in, out).toMinutes();

        // 최초 30분 무료
        if (minutes <= 30)
        	return 0;

        // 이후 30분당 요금
        long chargeMinutes = minutes - 30;
        int unit = (int) Math.ceil(chargeMinutes / 30.0); // 올림 처리

        int rate = isMember ? 1000 : 2000;
        return unit * rate;
    }

    private boolean isValidResult(String text) {
    	System.out.println("=> OcrService: isValidResult | "+ new Date());
        return text != null && !text.equals("인식 실패") && !text.contains("에러") && !text.trim().isEmpty();
    }

    private String formatDateTime(LocalDateTime time) {
    	System.out.println("=> OcrService: formatDateTime | "+ new Date());
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    private BufferedImage preprocessBoldBlur(BufferedImage source) {
    	System.out.println("=> OcrService: preprocessBoldBlur | "+ new Date());
        BufferedImage resized = resizeImage(source, 2);		// 확대 2x
        BufferedImage bold = applyDilation(resized);		// 글자 굵게
        BufferedImage smoothBold = applyGaussianBlur(bold);	// 가우시안 블러
        return addPadding(smoothBold, 50);					// 패딩해서 리턴
    }

    // 확대 2x
    private BufferedImage resizeImage(BufferedImage original, int scale) {
    	System.out.println("=> OcrService: resizeImage | "+ new Date());
        int w = original.getWidth() * scale;
        int h = original.getHeight() * scale;
        BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(original, 0, 0, w, h, null);
        g.dispose();
        return resized;
    }
    
    // 글자 굵게
    private BufferedImage applyDilation(BufferedImage source) {
    	System.out.println("=> OcrService: applyDilation | "+ new Date());
        int w = source.getWidth();
        int h = source.getHeight();
        BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                int minVal = 255;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int val = source.getRGB(x + dx, y + dy) & 0xFF;
                        if (val < minVal) minVal = val;
                    }
                }
                int newPixel = (255 << 24) | (minVal << 16) | (minVal << 8) | minVal;
                dest.setRGB(x, y, newPixel);
            }
        }
        return dest;
    }

    // 가우시안 블러
    private BufferedImage applyGaussianBlur(BufferedImage source) {
    	System.out.println("=> OcrService: applyGaussianBlur | "+ new Date());
        float[] matrix = {
            1/16f, 1/8f, 1/16f,
            1/8f,  1/4f, 1/8f,
            1/16f, 1/8f, 1/16f,
        };
        Kernel kernel = new Kernel(3, 3, matrix);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(source, null);
    }

    // 패딩
    private BufferedImage addPadding(BufferedImage original, int padding) {
    	System.out.println("=> OcrService: addPadding | "+ new Date());
        BufferedImage padded = new BufferedImage(original.getWidth() + padding * 2, original.getHeight() + padding * 2, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = padded.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, padded.getWidth(), padded.getHeight());
        g.drawImage(original, padding, padding, null);
        g.dispose();
        return padded;
    }

    // 번호판 문자열 추출
    private String parseLicensePlate(String text) {
    	System.out.println("=> OcrService: parseLicensePlate | "+ new Date());
        String cleanText = text.replaceAll("[^0-9가-힣]", "");
        Pattern fullPattern = Pattern.compile("([0-9]{2,3})([가-힣])([0-9]{4})$");
        Matcher fullMatcher = fullPattern.matcher(cleanText);
        
        if (fullMatcher.find())
            return fullMatcher.group(0);
        
        Pattern lastFourPattern = Pattern.compile("([0-9]{4})$");
        Matcher lastFourMatcher = lastFourPattern.matcher(cleanText);
        
        if (lastFourMatcher.find()) 
            return "뒷번호: " + lastFourMatcher.group(1);
        return "인식 실패";
    }

    // BufferedImage -> Base64 String 변환
    private String imageToBase64(BufferedImage image) throws IOException {
    	System.out.println("=> OcrService: imageToBase64 | "+ new Date());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bos);
        return Base64.getEncoder().encodeToString(bos.toByteArray());
    }
}