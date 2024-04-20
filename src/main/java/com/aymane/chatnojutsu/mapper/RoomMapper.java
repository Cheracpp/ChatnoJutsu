package com.aymane.chatnojutsu.mapper;

import com.aymane.chatnojutsu.dto.RoomDTO;
import com.aymane.chatnojutsu.model.Room;

public class RoomMapper {
    public static Room RoomDTOtoRoom(RoomDTO roomDTO) {
        return Room.builder()
                .messageFrom(roomDTO.messageFrom())
                .messageTo(roomDTO.messageTo())
                .build();

    }
}
