package com.bookmanagement.bookmanagementbackend.service;

import com.bookmanagement.bookmanagementbackend.dto.request.AuthenticationRequest;
import com.bookmanagement.bookmanagementbackend.dto.request.IntrospectRequest;
import com.bookmanagement.bookmanagementbackend.dto.response.AuthenticationResponse;
import com.bookmanagement.bookmanagementbackend.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;
}
