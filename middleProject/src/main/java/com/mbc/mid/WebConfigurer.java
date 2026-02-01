// middleProject - com.mbc.mid - WebConfigurer.java
package com.mbc.mid;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {
   
   /*@Override   // 접근 권한 부여
   public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**").allowedOrigins("*");
      //registry.addMapping("/**").allowedOrigins("http://localhost:3000");
   }*/
   
   @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // "*" 대신 프론트 주소를 정확히 명시
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // OPTIONS 꼭 포함
                .allowedHeaders("*")
                .allowCredentials(true) // 세션 받을 준비 됐다 라고 선언
                .maxAge(3600); // 간보기(Preflight) 결과를 1시간 동안 기억하게 함
    }
   
}