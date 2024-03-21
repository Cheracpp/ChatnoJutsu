package com.aymane.chatnojutsu.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    @NotNull(message = "Username is mandatory" )
    @Size(message = "Username character length must be between 3 and 20", min = 3, max = 20)
    private String username;
    @Column
    @NotNull(message = "Password is mandatory")
    @NotEmpty(message = "Password is mandatory")
    private String password;
    @Column
    @NotEmpty(message = "Email is mandatory")
    @NotNull(message = "Email is mandatory")
    @Email(message = "Email must be well-formed")
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;


    @ManyToMany
    @JoinTable(
            name = "friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends = new HashSet<>();

    public void addFriend(User friend){
        this.friends.add(friend);
        friend.getFriends().add(this);
    }
    public void removeFriend(User friend){
        this.friends.remove(friend);
        friend.getFriends().remove(this);
    }

    // UserDetails methods
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

}
