package com.aymane.chatnojutsu.mapper;

import com.aymane.chatnojutsu.dto.MessageDTO;
import com.aymane.chatnojutsu.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = ComponentModel.SPRING,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MessageMapper {

  @Mapping(source = "authenticatedSenderId", target = "senderId")
  Message fromMessageDTO(MessageDTO messageDTO, String authenticatedSenderId);
}
