package com.bsep_sbz.SIEMCenter.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;


@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("https://localhost:4200")
                .allowCredentials(true)
                .allowedHeaders("Origin", "Content-Type", "X-Auth-Token")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD");
    }

    //@CrossOrigin(origins = "http://domain2.com")
    //moguce je i ovu anotaciju koristiti za cors support
    // ova anotacija se stavlja iznad controller-a ili metoda controller-a
}