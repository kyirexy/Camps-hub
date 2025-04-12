package com.campus.hub.knowledge.model;

import lombok.Data;

/**
 * Model class representing an answer response from the knowledge base
 */
@Data
public class AnswerResponse {
    private String answer;
    private String source; // Source of the answer (e.g., which knowledge base or document)
    private double confidence; // Confidence score of the answer
    private String[] references; // Optional references or citations
    private String error; // Error message if any
} 