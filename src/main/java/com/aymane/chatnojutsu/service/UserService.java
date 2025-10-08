package com.aymane.chatnojutsu.service;

import com.aymane.chatnojutsu.dto.UserDTO;
import com.aymane.chatnojutsu.exception.EmailAlreadyExistsException;
import com.aymane.chatnojutsu.exception.PasswordFormatException;
import com.aymane.chatnojutsu.exception.UsernameAlreadyTakenException;
import com.aymane.chatnojutsu.mapper.UserMapper;
import com.aymane.chatnojutsu.model.Role;
import com.aymane.chatnojutsu.model.User;
import com.aymane.chatnojutsu.repository.UserRepository;
import com.aymane.chatnojutsu.utils.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerNewUser(UserDTO userDTO) {
        if (userDTO == null) {
            throw new IllegalArgumentException(ErrorMessages.USER_DATA_NULL);
        }

        if (userRepository.existsByUsername(userDTO.username())) {
            throw new UsernameAlreadyTakenException(String.format(ErrorMessages.USER_ALREADY_TAKEN, userDTO.username()));
        } else if (userRepository.existsByEmail(userDTO.email())) {
            throw new EmailAlreadyExistsException(String.format(ErrorMessages.EMAIL_ALREADY_EXISTS, userDTO.email()));
        }

        validatePassword(userDTO.password());

        User user = UserMapper.toUser(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        return user;
    }

    public User addFriend(String userName, String friendName){
        Optional<User> user = userRepository.findByUsername(userName);
        Optional<User> friend = userRepository.findByUsername(friendName);
        if(user.isPresent() && friend.isPresent()) {
            user.get().addFriend(friend.get());
            userRepository.save(user.get());
        }
        return user.orElseThrow(() -> new UsernameNotFoundException("User or friend not found with usernames: " + userName + ", " + friendName));
    }
    public User removeFriend(String userName, String friendName){
        Optional<User> user = userRepository.findByUsername(userName);
        Optional<User> friend = userRepository.findByUsername(friendName);
        if(user.isPresent() && friend.isPresent()) {
            user.get().removeFriend(friend.get());
            userRepository.save(user.get());
        }
        return user.orElseThrow(() -> new UsernameNotFoundException("User or friend not found with usernames: " + userName + ", " + friendName));
    }

    public List<String> getAllUsers() {
        List<User> listOfUser = userRepository.findAll();
        List<String> listOfUserUsernames = new ArrayList<>();
        for(User user : listOfUser){
            listOfUserUsernames.add(user.getUsername());
        }
        return listOfUserUsernames;
    }

    public List<String> getUsers(String query) {
        List<User> listOfUsers = userRepository.findUsersByUsernameContaining(query);
        List<String> listOfUserUsernames = new ArrayList<>();
        for(User user : listOfUsers){
            listOfUserUsernames.add(user.getUsername());
        }
        return listOfUserUsernames;
  public Map<String, UserDTO> getUsersByIds(List<String> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyMap();
    }

    // helpers
    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new PasswordFormatException(ErrorMessages.PASSWORD_IS_EMPTY);
        }
        if (password.length() < 8 || password.length() > 20) {
            throw new PasswordFormatException(ErrorMessages.PASSWORD_WRONG_SIZE);
        }
    }

    List<User> foundUsers = userRepository.findByIdIn(numericIds);
    return foundUsers.stream().map(userMapper::toUserDTO)
        .collect(Collectors.toMap(UserDTO::id, Function.identity()));
  }
}
