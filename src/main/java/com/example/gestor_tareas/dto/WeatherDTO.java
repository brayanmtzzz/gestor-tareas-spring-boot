package com.example.gestor_tareas.dto;

import lombok.Data;

@Data
public class WeatherDTO {
    private Main main;
    private String name;
    private Weather[] weather;
    
    @Data
    public static class Main {
        private double temp;
        private double feels_like;
        private int humidity;
    }
    
    @Data
    public static class Weather {
        private String main;
        private String description;
        private String icon;
    }
}
