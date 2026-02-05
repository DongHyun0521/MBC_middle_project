// middleProject - com.mbc.mid - VocScheduler.java
package com.mbc.mid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.mbc.mid.dao.AdminDao;

@Component
public class VocScheduler {
    @Autowired private AdminDao adminDao;
    /*
    초 : 0~59     분 : 0~59     시 : 0~23
    일 : 1~31     월 : 1~12     요일 : 0~7 (MON~SUN)
    * : 매번
                     초 분 시 일 월 요일              */
    @Scheduled(cron = "0 0 0 * * *") 
    public void autoDeleteVoc() {
        System.out.println("admin이 삭제한 voc 영구삭제 중...");
        try {
            int count = adminDao.permanentDeleteVoc();
            if(count > 0) System.out.println("voc " + count + "개 영구 삭제 완료");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}