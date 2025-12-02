package com.aymane.link.mapper;

import com.aymane.link.dto.RoomDTO;
import com.aymane.link.model.Room;
import java.util.Arrays;
import java.util.List;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface RoomMapper {

  RoomDTO toRoomDTO(Room room);

  @Mapping(target = "roomId", ignore = true)
  Room fromRoomDTO(RoomDTO roomDTO);

  default List<String> mapArrayToList(String[] array) {
    return Arrays.asList(array);
  }

  default String[] mapListToArray(List<String> list) {
    return list.toArray(String[]::new);
  }

  default String mapObjectIdToString(ObjectId objectId) {
    return objectId.toString();
  }
}
