package com.aymane.chatnojutsu.controller;

import com.aymane.chatnojutsu.mapper.UserMapper;
import com.aymane.chatnojutsu.model.User;
import com.aymane.chatnojutsu.dto.UserDTO;
import com.aymane.chatnojutsu.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createNewUser(@Valid @RequestBody UserDTO userDTO) {
        User createdUser = userService.registerNewUser(userDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(){
        List<String> listOfUsers = userService.getAllUsers();
        return ResponseEntity.ok(listOfUsers);
    }
    @GetMapping("/searchUsers")
    public ResponseEntity<?> getUsers(@RequestParam String query){
        List<String> listOfUsers = userService.getUsers(query);
        return ResponseEntity.ok(listOfUsers);
    }
    @PostMapping("/{userName}/friends/{friendName}")
    public ResponseEntity<?> addFriend(@PathVariable String userName, @PathVariable String friendName){
        UserDTO userDTO = UserMapper.toUserDTO(userService.addFriend(userName,friendName));
        return ResponseEntity.ok(userDTO.username() + " is friend with " + friendName);
    }
    @DeleteMapping ("/{userName}/friends/{friendName}")
    public ResponseEntity<?> removeFriend(@PathVariable String userName, @PathVariable String friendName) {
        UserDTO userDTO = UserMapper.toUserDTO(userService.removeFriend(userName, friendName));
        return ResponseEntity.ok(userDTO.username() + " is no longer friends with " + friendName);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(" you have access now  ");
    }
}
