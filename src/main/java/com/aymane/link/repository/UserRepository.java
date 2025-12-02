package com.aymane.link.repository;

import com.aymane.link.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  List<User> findUsersByUsernameContaining(String query);

  List<User> findByIdIn(List<Long> ids);
}
