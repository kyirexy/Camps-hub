package com.campus.hub.knowledge.service;

import com.campus.hub.knowledge.model.QuestionRequest;
import com.campus.hub.knowledge.model.AnswerResponse;

/**
 * Service interface for knowledge base operations
 */
public interface KnowledgeBaseService {
    /**
     * Process a question and return an answer
     * @param request The question request
     * @return The answer response
     */
    AnswerResponse askQuestion(QuestionRequest request);
    
    /**
     * Initialize the knowledge base with necessary configurations
     */
    void initialize();
} 