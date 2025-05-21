package com.example.traffic.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/traffic")
public class TrafficTestController {

    @PostMapping("/v1")
    public String v1() {
        return "Response from v1";
    }

}
