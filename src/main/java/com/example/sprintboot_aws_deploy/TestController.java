package com.example.sprintboot_aws_deploy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")

public class TestController {
    @GetMapping("/data")
    public String getData() {
        return "First message will be from AWS ECS";

    }
    @GetMapping("/message")
    public String getMessage() {
        return "Second message will be from AWS ECS";

    }

}

