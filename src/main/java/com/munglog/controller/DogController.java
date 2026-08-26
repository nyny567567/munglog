package com.munglog.controller;

import com.munglog.dto.DogRequest;
import com.munglog.dto.DogResponse;
import com.munglog.service.DogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dogs")
@RequiredArgsConstructor
public class DogController {

    private final DogService dogService;

    @PostMapping
    public ResponseEntity<DogResponse> registerDog(
            @RequestBody DogRequest dogRequest,
            @AuthenticationPrincipal String email
    ) {
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        DogResponse dogResponse = dogService.registerDog(dogRequest, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(dogResponse);
    }

    //내 강아지 전체 목록 조회 API
    @GetMapping
    public ResponseEntity<List<DogResponse>> getMyDogs(@AuthenticationPrincipal String email) {
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<DogResponse> dogResponse = dogService.getMyDogs(email);
        return ResponseEntity.ok(dogResponse);
    }

    //단건 상세 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<DogResponse> getDogById(@PathVariable Long id) {
        DogResponse dogResponse = dogService.getDogById(id);
        return ResponseEntity.ok(dogResponse);
    }
}
