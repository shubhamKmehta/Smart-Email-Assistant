package com.email.writer.service;

import com.email.writer.entity.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class EmailGeneratorService {

        private final WebClient webClient;
        private final String apiKey;

    public EmailGeneratorService(WebClient.Builder webClientBuilder,
                                 @Value("${gemini.api.url}") String baseUrl,
                                 @Value("${gemini.api.key}") String geminiApiKey
                                 ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = geminiApiKey;
    }

    public String generateEmailReply(EmailRequest emailRequest){
            // build prompt

            String prompt = buildPrompt(emailRequest);
            // prepare raw JSON body
            String requestBody = String.format("""
                    {
                        "contents": [
                          {
                            "parts": [
                              {
                                "text": "%s"
                              }
                            ]
                          }
                        ]
                    }
                    """,prompt);
            // send Request
            String response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/gemini-3-flash-preview:generateContent")
                            .build())
                    .header("x-goog-api-key", apiKey)
                    .header("Content-Type","application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            // Extract response
            return extractResponseContent(response);
        }

        private String buildPrompt(EmailRequest emailRequest){
            StringBuilder prompt = new StringBuilder();
            prompt.append("Generate a professional email reply for the following email :");
            if(emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()){
                prompt.append("Use a ").append(emailRequest.getTone()).append(" tone.");
                // Use a professional tone.

            }
            prompt.append("Original Email : \n").append(emailRequest.getEmailContent());
            return prompt.toString();
        }

        public String extractResponseContent(String response){
            try{
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response);
                String text = root.path("candidates")
                        .get(0)
                        .path("content")
                        .path("parts")
                        .get(0)
                        .path("text")
                        .asText();
                return text;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
}
