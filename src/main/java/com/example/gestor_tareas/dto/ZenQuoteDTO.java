package com.example.gestor_tareas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ZenQuoteDTO {
    @JsonProperty("q")
    private String quote;
    
    @JsonProperty("a")
    private String author;
    
    @JsonProperty("h")
    private String htmlContent;
}
