package com.example.gestor_tareas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KeepAliveService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${SELF_URL:http://localhost:8080}")
    private String selfUrl;
    
    // Se ejecuta cada 11 minutos y 40 segundos (700,000 ms)
    @Scheduled(fixedRate = 700000)
    public void keepAlive() {
        try {
            restTemplate.getForObject(selfUrl, String.class);
            System.out.println("✅ Keep-alive ping sent to " + selfUrl);
        } catch (Exception e) {
            System.err.println("❌ Failed to send keep-alive ping: " + e.getMessage());
        }
    }
}
