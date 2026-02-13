package com.laris.dataprocessingpipeline.service;

import com.laris.dataprocessingpipeline.domain.Role;
import com.laris.dataprocessingpipeline.domain.User;
import com.laris.dataprocessingpipeline.dto.request.AuthRequest;
import com.laris.dataprocessingpipeline.dto.response.AuthResponse;
import com.laris.dataprocessingpipeline.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userPrincipal.user();

        String token = jwtService.generateToken(userPrincipal);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .roles(user.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet()))
                        .build())
                .build();
    }
}
