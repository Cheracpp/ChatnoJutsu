package com.aymane.chatnojutsu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping()
public class LoginController {

    @GetMapping("/login")
    public String loginUser(){
        return "login";
    }

    @GetMapping("/register")
    public String registerNewUser(){
        return "register";
    }

    @GetMapping("/home")
    public String getHomepage(){
        return "index";
    }
}

