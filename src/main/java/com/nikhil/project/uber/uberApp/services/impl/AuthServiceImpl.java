package com.nikhil.project.uber.uberApp.services.impl;

import com.nikhil.project.uber.uberApp.dto.DriverDto;
import com.nikhil.project.uber.uberApp.dto.SignUpDto;
import com.nikhil.project.uber.uberApp.dto.UserDto;
import com.nikhil.project.uber.uberApp.entities.User;
import com.nikhil.project.uber.uberApp.enums.Role;
import com.nikhil.project.uber.uberApp.exceptions.UserAlreadyExistsException;
import com.nikhil.project.uber.uberApp.repositories.UserRepository;
import com.nikhil.project.uber.uberApp.services.AuthService;
import com.nikhil.project.uber.uberApp.services.RiderService;
import com.nikhil.project.uber.uberApp.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RiderService riderService;
    private final WalletService walletService;

    @Override
    public String login(String email, String password) {
        return "";
    }

    @Override
    @Transactional
    public UserDto signUp(SignUpDto signUpDto) {

        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists with this email " + signUpDto.getEmail());
        }

        User user = modelMapper.map(signUpDto, User.class);
        user.setRoles(Set.of(Role.RIDER));

        User savedUser = userRepository.save(user);

        riderService.createNewRider(savedUser);
        walletService.createNewWallet(savedUser);

        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public DriverDto onBoardNewDriver(Long userId) {
        return null;
    }
}
