package com.campus.hub.knowledge.service.impl;

import com.campus.hub.knowledge.model.QuestionRequest;
import com.campus.hub.knowledge.model.AnswerResponse;
import com.campus.hub.knowledge.provider.LLMProvider;
import com.campus.hub.knowledge.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the KnowledgeBaseService
 */
@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    private final LLMProvider llmProvider;

    @Autowired
    public KnowledgeBaseServiceImpl(LLMProvider llmProvider) {
        this.llmProvider = llmProvider;
    }

    @Override
    public AnswerResponse askQuestion(QuestionRequest request) {
        // Add any preprocessing or validation logic here
        return llmProvider.processQuestion(request);
    }

    @Override
    public void initialize() {
        // Initialize the LLM provider with any necessary configurations
        llmProvider.initialize(null); // Pass appropriate config object
    }
} 