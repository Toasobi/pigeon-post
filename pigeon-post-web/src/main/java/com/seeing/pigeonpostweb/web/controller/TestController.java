package com.seeing.pigeonpostweb.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {
    @GetMapping("/test")
    private String test(){
        log.info("日志测试");
        return "日志测试";
    }
}
