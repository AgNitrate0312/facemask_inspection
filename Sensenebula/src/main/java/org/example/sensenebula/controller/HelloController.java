package org.example.sensenebula.controller;

import org.example.sensenebula.utils.SenseApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        SenseApi.login();
        String s = SenseApi.sessionId;

        return "{\"message\":\"" + SenseApi.sessionId + "\"}";
    }
}