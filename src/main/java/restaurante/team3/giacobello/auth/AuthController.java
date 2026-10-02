package restaurante.team3.giacobello.auth;

import jakarta.validation.Valid;
import restaurante.team3.giacobello.auth.dto.LoginRequest;
import restaurante.team3.giacobello.auth.dto.TokenResponse;
import restaurante.team3.giacobello.auth.repository.UserAuthRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import restaurante.team3.giacobello.auth.dto.ChangePasswordRequest;
import restaurante.team3.giacobello.auth.entity.UserAuthEntity;
import restaurante.team3.giacobello.auth.repository.UserAuthRepository;

@RestController
@RequestMapping(path = "${api-endpoint}/auth")
public class AuthController {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            TokenService tokenService,
            AuthenticationManager authenticationManager,
            UserAuthRepository userAuthRepository,
            PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.userAuthRepository = userAuthRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/token")
    public TokenResponse token(@Valid @RequestBody LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        return new TokenResponse(tokenService.generateToken(authentication));
    }

    private Authentication authenticate(String username, String password) {
        try {
            return authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(username, password));
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    private UserAuthEntity findUser(String username) {
        return userAuthRepository.findByUsername(username)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
    }
}
