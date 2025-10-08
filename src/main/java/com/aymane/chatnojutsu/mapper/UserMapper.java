package com.aymane.chatnojutsu.mapper;

import com.aymane.chatnojutsu.config.CustomUserDetails;
import com.aymane.chatnojutsu.dto.RegisterRequest;
import com.aymane.chatnojutsu.dto.UserDTO;
import com.aymane.chatnojutsu.model.Role;
import com.aymane.chatnojutsu.model.User;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Mapper(
    componentModel = ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

  User fromRegisterRequest(RegisterRequest registerRequest);

  User fromUserDTO(UserDTO userDTO);

  UserDTO toUserDTO(User user);

  @Mapping(target = "id", expression = "java(customUserDetails.getUsername())")
  @Mapping(target = "username", expression = "java(customUserDetails.getActualUsername())")
  UserDTO fromCustomUserDetailsToUserDto(CustomUserDetails customUserDetails);

  @Mapping(target = "authorities", expression = "java(mapRolesToAuthorities(user.getRole()))")
  CustomUserDetails toCustomUserDetails(User user);

  default Collection<? extends GrantedAuthority> mapRolesToAuthorities(Role role) {
    if (role == null) {
      return Collections.emptyList();
    }
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  default String mapLongToString(Long longValue) {
    return String.valueOf(longValue);
  }
}
