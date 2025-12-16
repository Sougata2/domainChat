package com.domain.chat.app.room.controller;

import com.domain.chat.app.message.dto.MessageDto;
import com.domain.chat.app.room.dto.RoomDto;
import com.domain.chat.app.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService service;

    @GetMapping("/all")
    public ResponseEntity<List<RoomDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/reference-number/{number}")
    public ResponseEntity<RoomDto> findByReferenceNumber(@PathVariable(value = "number") String referenceNumber) {
        return ResponseEntity.ok(service.findByReferenceNumber(referenceNumber));
    }

    @GetMapping("/messages/{number}")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable(value = "number") String referenceNumber) {
        return ResponseEntity.ok(service.getMessages(referenceNumber));
    }

    @GetMapping("/subscribed-rooms")
    public ResponseEntity<List<RoomDto>> getSubscribedRooms() {
        return ResponseEntity.ok(service.getSubscribedRooms());
    }

    @Deprecated
    @GetMapping(value = "/messages/{number}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamRoom(@PathVariable(value = "number") String referenceNumber) {
        return service.streamRoom(referenceNumber);
    }

    @PostMapping
    public ResponseEntity<RoomDto> create(@RequestBody RoomDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping
    public ResponseEntity<RoomDto> update(@RequestBody RoomDto dto) {
        return ResponseEntity.ok(service.update(dto));
    }

    @DeleteMapping
    public ResponseEntity<RoomDto> delete(@RequestBody RoomDto dto) {
        return ResponseEntity.ok(service.delete(dto));
    }
}
