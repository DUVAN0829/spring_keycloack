package com.duvan.keycloack.app.services;

import java.util.Arrays;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import com.duvan.keycloack.app.models.UserRegister;
import reactor.core.publisher.Mono;

public class KeycloackUserService {

  // Vars
  private final WebClient webClient;
  private final KeycloackTokenService tokenService;

  // Constructor
  public KeycloackUserService(WebClient webClient, KeycloackTokenService tokenService) {
    this.webClient = webClient;
    this.tokenService = tokenService;
  }

  // Method
  public Mono<Void> registerUser(UserRegister userRegister) {

    Map<String, Object> payload = Map.of(
        "username", userRegister.getUsername(),
        "email", userRegister.getEmail(),
        "enabled", true,
        "emailVerified", true,
        "credentials", Arrays.asList(
            Map.of(
                "type", "password",
                "value", userRegister.getPassword(),
                "temporary", false)));

    return tokenService.getAdminToken()
        .flatMap(token -> webClient.post()
            .uri("http://localhost:8080/admin/realms/spring-boot/users")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .retrieve()
            .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                .flatMap(erorr -> Mono.error(new RuntimeException(erorr))))
            .bodyToMono(Void.class));
  }

}
