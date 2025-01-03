package com.aymane.chatnojutsu.controller;

import com.aymane.chatnojutsu.mapper.UserMapper;
import com.aymane.chatnojutsu.model.User;
import com.aymane.chatnojutsu.dto.UserDTO;
import com.aymane.chatnojutsu.service.RoomService;
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
    private final RoomService roomService;

    @Autowired
    public UserController(UserService userService, RoomService roomService) {
        this.userService = userService;
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<URI> createNewUser(@Valid @RequestBody UserDTO userDTO) {
        User createdUser = userService.registerNewUser(userDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllUsers(){
        List<String> listOfUsers = userService.getAllUsers();
        return ResponseEntity.ok(listOfUsers);
    }
    @GetMapping("/searchUsers")
    public ResponseEntity<List<String>> getUsers(@RequestParam String query){
        List<String> listOfUsers = userService.getUsers(query);
        return ResponseEntity.ok(listOfUsers);
    }
    @PostMapping("/{userName}/friends/{friendName}")
    public ResponseEntity<String> addFriend(@PathVariable String userName, @PathVariable String friendName){
        UserDTO userDTO = UserMapper.toUserDTO(userService.addFriend(userName,friendName));
        return ResponseEntity.ok(userDTO.username() + " is friend with " + friendName);
    }
    @DeleteMapping ("/{userName}/friends/{friendName}")
    public ResponseEntity<String> removeFriend(@PathVariable String userName, @PathVariable String friendName) {
        UserDTO userDTO = UserMapper.toUserDTO(userService.removeFriend(userName, friendName));
        return ResponseEntity.ok(userDTO.username() + " is no longer friends with " + friendName);
    }
    @GetMapping("/{username}/chats")
    public ResponseEntity<List<String>> getUserChats(@PathVariable String username){
        List<String> chatsByUsername = roomService.getChatsByUsername(username);
        return ResponseEntity.ok(chatsByUsername);
    }
}
