package com.duvan.keycloack.app.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

  @Bean
  public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter() {

    return jwt -> {
      Collection<SimpleGrantedAuthority> authorities = new HashSet<>();

      Object realmAccessObj = jwt.getClaim("realm_access");
      if (realmAccessObj instanceof Map<?, ?> realmAccess) {

        Object rolesObj = realmAccess.get("roles");
        if (rolesObj instanceof List<?> rolesList) {

          rolesList.stream()
              .filter(String.class::isInstance)
              .map(String.class::cast)
              .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        }
      }

      Object resourceAccessObj = jwt.getClaim("resource_access");
      if (resourceAccessObj instanceof Map<?, ?> resourceAccess) {

        Object clientObj = resourceAccess.get("spring-api");
        if (clientObj instanceof Map<?, ?> client) {

          Object clientRolesObj = client.get("roles");
          if (clientRolesObj instanceof List<?> rolesList) {

            rolesList.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
          }
        }
      }

      return new JwtAuthenticationToken(jwt, authorities);
    };
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter)
      throws Exception {

    return http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .anyRequest().authenticated())
        .oauth2ResourceServer(oauth -> oauth
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
        .build();
  }

}
