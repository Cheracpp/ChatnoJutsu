package com.aymane.chatnojutsu.config;

import com.aymane.chatnojutsu.mapper.UserMapper;
import com.aymane.chatnojutsu.model.User;
import com.aymane.chatnojutsu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Autowired
  public UserDetailsServiceImpl(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
                              .orElseThrow(() -> new UsernameNotFoundException(
                                  "User not found with username: " + username));
    return userMapper.toCustomUserDetails(user);
  }

  public UserDetails loadUserById(String Id) throws UsernameNotFoundException {
    Long userId = Long.parseLong(Id);
    User user = userRepository.findById(userId)
                              .orElseThrow(() -> new UsernameNotFoundException(
                                  "User not found with id: " + Id));
    return userMapper.toCustomUserDetails(user);
  }
}
