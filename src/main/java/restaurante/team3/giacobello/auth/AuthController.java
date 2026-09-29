package restaurante.team3.giacobello.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;

import com.stripe.service.TokenService;

@RestController
@RequestMapping (path = "${api-endpoint}/auth")
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping ("/token")
    public String token(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }
}
