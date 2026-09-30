package restaurante.team3.giacobello.auth;

import jakarta.validation.Valid;
import restaurante.team3.giacobello.auth.dto.LoginRequest;
import restaurante.team3.giacobello.auth.dto.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping (path = "${api-endpoint}/auth")
public class AuthController {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public AuthController(TokenService tokenService, AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
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
}
