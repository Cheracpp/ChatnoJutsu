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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

        // Check for existing users before validating the password.
        if (userRepository.existsByUsername(userDTO.username())) {
            throw new UsernameAlreadyTakenException(String.format(ErrorMessages.USER_ALREADY_TAKEN, userDTO.username()));
        } else if (userRepository.existsByEmail(userDTO.email())) {
            throw new EmailAlreadyExistsException(String.format(ErrorMessages.EMAIL_ALREADY_EXISTS, userDTO.email()));
        }

        // Enhanced password validation.
        validatePassword(userDTO.password());

        User user = UserMapper.toUser(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        return user;
    }


    public List<String> getAllUsers() {
        List<User> ListOfUser = userRepository.findAll();
        List<String> ListOfUserUsernames = new ArrayList<>();
        for(User user : ListOfUser){
            ListOfUserUsernames.add(user.getUsername());
        }
        return ListOfUserUsernames;
    }


    // helpers
    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new PasswordFormatException(ErrorMessages.PASSWORD_IS_EMPTY);
        }
        if (password.length() < 8 || password.length() > 20) {
            throw new PasswordFormatException(ErrorMessages.PASSWORD_WRONG_SIZE);
        }
        // Add more complex password validation rules here if necessary.
    }

}
