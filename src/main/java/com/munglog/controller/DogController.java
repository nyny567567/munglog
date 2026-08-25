package com.munglog.controller;

import com.munglog.dto.DogRequest;
import com.munglog.dto.DogResponse;
import com.munglog.service.DogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
