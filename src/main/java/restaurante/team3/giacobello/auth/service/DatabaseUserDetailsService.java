package restaurante.team3.giacobello.auth.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import restaurante.team3.giacobello.auth.entity.UserAuthEntity;
import restaurante.team3.giacobello.auth.repository.UserAuthRepository;
import restaurante.team3.giacobello.auth.repository.UserDetailsRepository;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserAuthRepository userAuthRepository;
    private final UserDetailsRepository userDetailsRepository;

    public DatabaseUserDetailsService(
            UserAuthRepository userAuthRepository,
            UserDetailsRepository userDetailsRepository) {
        this.userAuthRepository = userAuthRepository;
        this.userDetailsRepository = userDetailsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuthEntity user = userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<SimpleGrantedAuthority> authorities = userDetailsRepository.findByUserId(user.getId())
                .map(details -> details.getRole())
                .map(role -> List.of(new SimpleGrantedAuthority("ROLE_" + role.getName())))
                .orElseGet(List::of);

        return User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .build();
    }
}