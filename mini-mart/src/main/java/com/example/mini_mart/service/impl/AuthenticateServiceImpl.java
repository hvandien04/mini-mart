package com.example.mini_mart.service.impl;


import com.example.mini_mart.dto.request.AuthenticationRequest;
import com.example.mini_mart.dto.request.IntrospectRequest;
import com.example.mini_mart.dto.response.AuthenticationResponse;
import com.example.mini_mart.dto.response.CurrentUserResponse;
import com.example.mini_mart.dto.response.IntrospectResponse;
import com.example.mini_mart.entity.User;
import com.example.mini_mart.exception.AppException;
import com.example.mini_mart.exception.ErrorCode;
import com.example.mini_mart.repository.UserRepository;
import com.example.mini_mart.service.AuthenticateService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticateServiceImpl implements AuthenticateService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;


    @Value("${jwt.valid-duration-access}")
    private Long VALID_DURATION_ACCESS;

    @Value("${jwt.valid-duration-refresh}")
    private Long VALID_DURATION_REFRESH;


    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);
        
        if (user == null) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        
        if (!user.getIsActive()) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(generateToken(user, false))
                .refreshToken(generateToken(user, true))
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) {
        var token = request.getToken();
        boolean isValid = false;
        try {
            verifyToken(token);
            isValid = true;
        } catch (AppException | ParseException | JOSEException ignored) {
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    @Override
    public AuthenticationResponse refreshToken(String token) throws ParseException, JOSEException {

        log.info("Refresh token: {}", token);
        var verifiedToken = verifyToken(token);
        if(!verifiedToken.getJWTClaimsSet().getClaim("type").toString().equals("refresh")) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_ALLOWED);
        }

        String username = verifiedToken.getJWTClaimsSet().getSubject();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return AuthenticationResponse.builder()
                .refreshToken(generateToken(user,true))
                .token(generateToken(user,false))
                .authenticated(true)
                .build();
    }

    private String generateToken(User user, Boolean isRefresh) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);

        long duration = isRefresh
                ? VALID_DURATION_REFRESH
                : VALID_DURATION_ACCESS;
        Date expirationDate = new Date(
                Instant.now().plus(duration, ChronoUnit.SECONDS).toEpochMilli()
        );


        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issueTime(new Date())
                .claim("type", isRefresh ? "refresh" : "access")
                .claim("scope", user.getRole() != null ? user.getRole().trim() : "")
                .claim("userId", user.getId())
                .expirationTime(expirationDate)
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new RSASSASigner(privateKey));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        if (!signedJWT.verify(new RSASSAVerifier(publicKey))
                || expiryTime.before(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }

    @Override
    public CurrentUserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        return CurrentUserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

}
