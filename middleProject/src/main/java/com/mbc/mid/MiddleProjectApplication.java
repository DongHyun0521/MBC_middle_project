// middleProject - com.mbc.mid - MiddleProjectApplication.java
package com.mbc.mid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling	// '30일 이후 삭제'에 필요
public class MiddleProjectApplication {
	public static void main(String[] args) {
		SpringApplication.run(MiddleProjectApplication.class, args);
	}
}