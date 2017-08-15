package com.springboot.brushup.students.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration // not needed as Application already turns auto-configuration on
public class Example {

    @RequestMapping(value="/")
    String home() {
    	
    	String result = "Hello";
    	result += " my third World!";
        return result;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Example.class, args);
    }

}