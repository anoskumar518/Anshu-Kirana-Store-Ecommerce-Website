package com.kirana.service;

import com.kirana.dto.AuthResponse;
import com.kirana.dto.LoginRequest;
import com.kirana.dto.RegisterRequest;
import com.kirana.dto.UserDto;
import com.kirana.entity.Role;
import com.kirana.entity.RoleName;
import com.kirana.entity.User;
import com.kirana.exception.BadRequestException;
import com.kirana.exception.ResourceNotFoundException;
import com.kirana.exception.UnauthorizedException;
import com.kirana.mapper.EntityDtoMapper;
import com.kirana.repository.RoleRepository;
import com.kirana.repository.UserRepository;
import com.kirana.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final EntityDtoMapper mapper;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       EntityDtoMapper mapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.mapper = mapper;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("A customer with mobile number " + request.getPhone() + " already exists");
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("A customer with email " + request.getEmail() + " already exists");
            }
        }

        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_CUSTOMER, "Customer Role")));

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setPhone(request.getPhone().trim());
        user.setEmail(request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPreferredLanguage(request.getPreferredLanguage() != null ? request.getPreferredLanguage() : "en");
        user.setIsActive(true);
        user.setRoles(Collections.singleton(customerRole));

        User savedUser = userRepository.save(user);

        // Authenticate new user and return JWT
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword())
        );
        String token = tokenProvider.generateToken(authentication);

        return new AuthResponse(token, mapper.toUserDto(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        String identifier = request.getIdentifier().trim();

        // Check if user exists
        Optional<User> userOpt = userRepository.findByPhone(identifier)
                .or(() -> userRepository.findByEmail(identifier));

        if (userOpt.isEmpty()) {
            throw new UnauthorizedException("Invalid mobile number/email or password");
        }

        User user = userOpt.get();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getPhone(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        return new AuthResponse(token, mapper.toUserDto(user));
    }

    public User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated. Please log in.");
        }

        String phone = authentication.getName();
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));
    }

    public UserDto getCurrentUserProfile() {
        return mapper.toUserDto(getCurrentAuthenticatedUser());
    }
}
