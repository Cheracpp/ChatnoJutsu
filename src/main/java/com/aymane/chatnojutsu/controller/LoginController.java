package com.aymane.chatnojutsu.controller;

import com.aymane.chatnojutsu.config.CustomUserDetails;
import com.aymane.chatnojutsu.dto.UserDTO;
import com.aymane.chatnojutsu.mapper.UserMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping()
public class LoginController {

  private final UserMapper userMapper;

  public LoginController(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @GetMapping("/login")
  public String loginUser() {
    return "login";
  }

  @GetMapping("/register")
  public String registerNewUser() {
    return "register";
  }

  @GetMapping("/home")
  public String getHomepage(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
    UserDTO userDTO = userMapper.fromCustomUserDetailsToUserDto(userDetails);
    model.addAttribute("loggedInUser", userDTO);
    return "index";
  }
}

