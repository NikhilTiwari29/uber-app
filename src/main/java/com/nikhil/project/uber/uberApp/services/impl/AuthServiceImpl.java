package com.nikhil.project.uber.uberApp.services.impl;

import com.nikhil.project.uber.uberApp.dto.DriverDto;
import com.nikhil.project.uber.uberApp.dto.SignUpDto;
import com.nikhil.project.uber.uberApp.dto.UserDto;
import com.nikhil.project.uber.uberApp.services.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public String login(String email, String password) {
        return "";
    }

    @Override
    public UserDto signUp(SignUpDto signUpDto) {
        return null;
    }

    @Override
    public DriverDto onBoardNewDriver(Long userId) {
        return null;
    }
}
