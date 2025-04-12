package com.campus.hub.knowledge.provider;

import com.campus.hub.knowledge.model.QuestionRequest;
import com.campus.hub.knowledge.model.AnswerResponse;

/**
 * Interface for Large Language Model providers
 * This interface allows for easy switching between different LLM implementations
 */
public interface LLMProvider {
    /**
     * Process a question and return an answer
     * @param request The question request
     * @return The answer response
     */
    AnswerResponse processQuestion(QuestionRequest request);
    
    /**
     * Initialize the provider with necessary configurations
     * @param config Configuration object
     */
    void initialize(Object config);
} 