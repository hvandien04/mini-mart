package com.example.mini_mart.service;
import com.example.mini_mart.dto.request.AuthenticationRequest;
import com.example.mini_mart.dto.request.IntrospectRequest;
import com.example.mini_mart.dto.response.AuthenticationResponse;
import com.example.mini_mart.dto.response.CurrentUserResponse;
import com.example.mini_mart.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticateService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    IntrospectResponse introspect(IntrospectRequest request);
    AuthenticationResponse refreshToken(String token) throws ParseException, JOSEException;
    CurrentUserResponse getCurrentUser(String username);
}

