package com.nerd.herd.authentication.service;

import com.nerd.herd.authentication.data.AuthUserRepository;
import com.nerd.herd.authentication.domain.Role;
import com.nerd.herd.authentication.util.AuthenticationRequest;
import com.nerd.herd.authentication.util.AuthenticationResponse;
import com.nerd.herd.authentication.util.RegisterRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.nerd.herd.authentication.domain.AuthUser;

@Service
@AllArgsConstructor
public class AuthService {

	private final JwtService jwtService;
	private final AuthUserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;

	/**
	 *
	 * @return current user
	 */
	public AuthUser getAuthUser(){
		return new AuthUser("id","firstname", "lastname", "email", "password", Role.ADMIN);
	}

	public AuthUser getAdminAuthUser(){
		return new AuthUser("id","firstname", "lastname", "email", "password", Role.USER);
	}


	public AuthenticationResponse register(RegisterRequest request) {
		var user = AuthUser.builder()
				.firstname(request.getFirstName())
				.lastname(request.getLastName())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(Role.USER)
				.build();

		repository.save(user);
		var jwtToken = jwtService.generateToken(user, user.getId());
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		var user = repository.findByEmail(request.getEmail()).orElseThrow();
		var jwtToken = jwtService.generateToken(user, user.getId());
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}
}
