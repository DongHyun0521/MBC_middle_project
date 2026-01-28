// middleProject - com.mbc.mid - WebConfigurer.java
package com.mbc.mid;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {

	@Override	// 접근 권한 부여
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*");
		//registry.addMapping("/**").allowedOrigins("http://localhost:3000");
	}
	
}
