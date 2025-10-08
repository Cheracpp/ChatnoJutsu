package com.aymane.chatnojutsu.service;

import com.aymane.chatnojutsu.dto.RegisterRequest;
import com.aymane.chatnojutsu.dto.UserDTO;
import com.aymane.chatnojutsu.exception.user.FriendServiceException;
import com.aymane.chatnojutsu.exception.user.UserNotFoundException;
import com.aymane.chatnojutsu.mapper.UserMapper;
import com.aymane.chatnojutsu.model.Role;
import com.aymane.chatnojutsu.model.User;
import com.aymane.chatnojutsu.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
      UserMapper userMapper) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.userMapper = userMapper;
  }

  public User registerNewUser(RegisterRequest registerRequest) {
    User user = userMapper.fromRegisterRequest(registerRequest);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setRole(Role.USER);
    userRepository.save(user);
    return user;
  }

  public User addFriend(String loggedInUserId, String friendId) {
    User currentUser = userRepository.findById(Long.parseLong(loggedInUserId)).orElseThrow(
        () -> new UserNotFoundException(
            "Logged-in user with id " + loggedInUserId + " not found."));
    User friendToAdd = userRepository.findById(Long.parseLong(friendId))
        .orElseThrow(() -> new UserNotFoundException("Friend with id " + friendId + " not found."));

    if (currentUser.getId().equals(friendToAdd.getId())) {
      throw new FriendServiceException("User can't add a friend with the same id.");
    }

    if (currentUser.getFriends().contains(friendToAdd)) {
      throw new FriendServiceException(friendToAdd.getUsername() + " is already a friend.");
    }

    currentUser.addFriend(friendToAdd);

    return userRepository.save(currentUser);
  }

  public User removeFriend(String loggedInUserId, String friendId) {
    User currentUser = userRepository.findById(Long.parseLong(loggedInUserId)).orElseThrow(
        () -> new UserNotFoundException(
            "Logged-in user with id " + loggedInUserId + " not found."));
    User friendToAdd = userRepository.findById(Long.parseLong(friendId))
        .orElseThrow(() -> new UserNotFoundException("Friend with id " + friendId + " not found."));

    if (currentUser.getId().equals(friendToAdd.getId())) {
      throw new FriendServiceException("User can't remove a friend with the same id.");
    }

    if (!currentUser.getFriends().contains(friendToAdd)) {
      throw new FriendServiceException(friendToAdd.getUsername() + " is already not a friend.");
    }

    currentUser.removeFriend(friendToAdd);

    return userRepository.save(currentUser);
  }

  public List<UserDTO> getAllUsers() {
    List<User> users = userRepository.findAll();
    return users.stream().map(userMapper::toUserDTO).collect(Collectors.toList());
  }

  public List<UserDTO> getUsersByQuery(String query) {
    List<User> listOfUsers = userRepository.findUsersByUsernameContaining(query);
    return listOfUsers.stream().map(userMapper::toUserDTO).collect(Collectors.toList());
  }

  public Map<String, UserDTO> getUsersByIds(List<String> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyMap();
    }

    List<Long> numericIds = ids.stream().map(Long::parseLong).collect(Collectors.toList());

    List<User> foundUsers = userRepository.findByIdIn(numericIds);
    return foundUsers.stream().map(userMapper::toUserDTO)
        .collect(Collectors.toMap(UserDTO::id, Function.identity()));
  }
}
