package com.aymane.chatnojutsu.controller;

import com.aymane.chatnojutsu.dto.RegisterRequest;
import com.aymane.chatnojutsu.dto.UserDTO;
import com.aymane.chatnojutsu.model.User;
import com.aymane.chatnojutsu.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<URI> createNewUser(@Valid @RequestBody RegisterRequest registerRequest) {
    User createdUser = userService.registerNewUser(registerRequest);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(createdUser.getId()).toUri();

    return ResponseEntity.created(location).build();
  }

  @GetMapping
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    List<UserDTO> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @GetMapping("/search")
  public ResponseEntity<List<UserDTO>> getUsers(
      @RequestParam("query") @NotBlank(message = "{error.field.required}") @Size(min = 3, message = "{error.query.size}") String query) {
    List<UserDTO> users = userService.getUsersByQuery(query);
    return ResponseEntity.ok(users);
  }

  @PostMapping("/details")
  public ResponseEntity<Map<String, UserDTO>> getUsersDetails(@RequestBody List<String> userIds) {
    Map<String, UserDTO> usersDetails = userService.getUsersByIds(userIds);
    return ResponseEntity.ok(usersDetails);
  }
}
