package com.munglog.controller;

import com.munglog.dto.LoginRequest;
import com.munglog.dto.SignupRequest;
import com.munglog.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest request) {
        memberService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = memberService.login(request);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/test")
    public ResponseEntity<String> jwtTest(HttpServletRequest request) {
        String email = (String) request.getAttribute("authenticatedEmail");

        if (email != null) {
            return ResponseEntity.ok(email + "님, 환영합니다!");
        } else {
            return ResponseEntity.status(401).body("유효한 토큰이 없습니다.");
        }
    }
}
