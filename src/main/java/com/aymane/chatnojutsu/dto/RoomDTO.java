package com.aymane.chatnojutsu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record RoomDTO(String roomId,
                      @NotEmpty(message = "{error.room.participants}") List<String> participants,
                      @NotBlank @Pattern(regexp = "^(direct|group)$", message = "{error.room.type}") String type,
                      String name) {

}