package com.duvan.keycloack.app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/example")
public class DemoController {

  @GetMapping
  @PreAuthorize("hasRole('client_user')")
  public String hello() {
    return "Hello from Springboot and Keycloack";
  }

  @GetMapping("/hello-2")
  @PreAuthorize("hasRole('client_admin')")
  public String helloTwo() {
    return "Hello from Springboot and Keycloack - ADMIN";
  }

}
