package com.tpe.service.user;

import com.tpe.payload.request.LoginRequest;
import com.tpe.payload.response.authentication.AuthResponse;
import com.tpe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;

    public ResponseEntity<AuthResponse> authenticateUser(LoginRequest loginRequest) {
        return null;
    }
}
