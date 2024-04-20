package com.aymane.chatnojutsu.repository;

import com.aymane.chatnojutsu.model.Room;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String> {

    @Query(value = "{$or: [{'messageFrom': ?0, 'messageTo': ?1}, {'messageFrom': ?1, 'messageTo': ?0}]}")
    Optional<Room> getRoom(String messageFrom, String messageTo);


    @Query("{$or:  [{'messageFrom': ?0},{'messageTo': ?0}]}")
    List<Room> findRoomsByUsername(String username, Sort sort);
}
