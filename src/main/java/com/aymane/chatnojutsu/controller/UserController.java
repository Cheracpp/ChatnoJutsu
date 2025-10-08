package com.aymane.chatnojutsu.controller;

import com.aymane.chatnojutsu.config.CustomUserDetails;
import com.aymane.chatnojutsu.dto.ApiResponse;
import com.aymane.chatnojutsu.dto.RegisterRequest;
import com.aymane.chatnojutsu.dto.UserDTO;
import com.aymane.chatnojutsu.model.User;
import com.aymane.chatnojutsu.service.UserService;
import com.aymane.chatnojutsu.validation.ValidId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @PostMapping("/friends/{friendId}")
  public ResponseEntity<ApiResponse> addFriend(@PathVariable @ValidId String friendId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    User updatedUser = userService.addFriend(userDetails.getUsername(), friendId);

    String message = String.format("User '%s' is now friends with '%s'.", updatedUser.getId(),
        friendId);
    ApiResponse response = new ApiResponse(true, message);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/friends/{friendId}")
  public ResponseEntity<ApiResponse> removeFriend(@PathVariable @ValidId String friendId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    User updatedUser = userService.removeFriend(userDetails.getUsername(), friendId);

    String message = String.format("User '%s' is no longer friends with '%s'.", updatedUser.getId(),
        friendId);
    ApiResponse response = new ApiResponse(true, message);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/details")
  public ResponseEntity<Map<String, UserDTO>> getUsersDetails(@RequestBody List<String> userIds) {
    Map<String, UserDTO> usersDetails = userService.getUsersByIds(userIds);
    return ResponseEntity.ok(usersDetails);
  }
}
