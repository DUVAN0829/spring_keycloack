package com.duvan.keycloack.app.services;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class KeycloackTokenService {

  // Vars
  private final WebClient webClient;

  // Constructor
  public KeycloackTokenService(WebClient webClient) {
    this.webClient = webClient;
  }

  // Methods
  public Mono<String> getAdminToken() {

    return webClient.post()
        .uri("http://localhost:8080/realms/spring-boot/protocol/openid-connect/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData("grant_type", "password")
            .with("client_id", "spring-api")
            .with("username", "duvan")
            .with("password", "123456"))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .map(json -> json.get("accesss_token").asString());

  }

}
