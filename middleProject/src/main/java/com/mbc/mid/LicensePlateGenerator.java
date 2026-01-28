// middleProject - com.mbc.mid - LicensePlateGenerator.java
package com.mbc.mid;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class LicensePlateGenerator {

    // 이미지가 저장될 경로 (프로젝트 폴더 안에 'test-images' 폴더가 자동 생성됨)
    private static final String SAVE_PATH = "test-images";
    private static final int COUNT = 1000; // 생성할 이미지 개수

    public static void main(String[] args) {
        File folder = new File(SAVE_PATH);
        if (!folder.exists()) folder.mkdirs();

        System.out.println("🚀 번호판 이미지 " + COUNT + "개 생성을 시작합니다...");

        for (int i = 0; i < COUNT; i++) {
            String plateNumber = generateRandomNumber();
            createImage(plateNumber);
        }

        System.out.println("✅ 생성 완료! 프로젝트 폴더 내 [" + SAVE_PATH + "] 폴더를 확인하세요.");
        System.out.println("새로고침(F5)을 누르면 폴더가 보입니다.");
    }

    // 1. 랜덤 번호판 숫자 생성 (예: 12가3456)
    private static String generateRandomNumber() {
        Random rand = new Random();
        String[] hangul = {"가", "나", "다", "라", "마", "거", "너", "더", "러", "머", "버", "서", "어", "저", "고", "노", "도", "로", "모", "보", "소", "오", "조", "구", "누", "두", "루", "무", "부", "수", "우", "주", "하", "허", "호"};
        
        int front = rand.nextInt(90) + 10; // 10~99
        String mid = hangul[rand.nextInt(hangul.length)];
        int back = rand.nextInt(9000) + 1000; // 1000~9999

        return front + mid + back;
    }

    // 2. 이미지 그리기 및 저장
    private static void createImage(String text) {
        int width = 500;
        int height = 150;

        // 흑백(Binary) 이미지 생성
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = image.createGraphics();

        // 배경 흰색 채우기
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 검은색 테두리 그리기 (두께 10)
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(10));
        g.drawRect(5, 5, width - 10, height - 10);

        // 글자 쓰기 (검은색)
        g.setFont(new Font("Malgun Gothic", Font.BOLD, 100)); // 폰트 크기 조절
        
        // 글자 중앙 정렬 계산
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        int y = (height - fm.getHeight()) / 2 + fm.getAscent();

        g.drawString(text, x, y);
        g.dispose();

        // 파일 저장 (파일명 = 번호판.jpg)
        try {
            File file = new File(SAVE_PATH + "/" + text + ".jpg");
            ImageIO.write(image, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}