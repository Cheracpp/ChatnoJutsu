package com.aymane.chatnojutsu.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username", unique = true),
    @Index(name = "idx_email", columnList = "email", unique = true)})
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String username;
  @Column
  private String password;
  @Column
  private String email;
  @Enumerated(EnumType.STRING)
  private Role role;
}
