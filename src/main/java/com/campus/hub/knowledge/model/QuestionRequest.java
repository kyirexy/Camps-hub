package com.campus.hub.knowledge.model;

import lombok.Data;

/**
 * Model class representing a question request to the knowledge base
 */
@Data
public class QuestionRequest {
    private String question;
    private String context; // Optional context for the question
    private String userId; // Optional user identifier
    private String sessionId; // Optional session identifier
} 