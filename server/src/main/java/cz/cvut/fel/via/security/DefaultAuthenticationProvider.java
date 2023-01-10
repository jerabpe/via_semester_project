package cz.cvut.fel.via.security;

import cz.cvut.fel.via.db.model.User;
import cz.cvut.fel.via.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefaultAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DefaultAuthenticationProvider(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("authentication of user " + authentication.getPrincipal().toString());
        final String username = authentication.getPrincipal().toString();
        Optional<User> userOptional = userRepository.findByUsername(username);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            if(passwordEncoder.matches((String) authentication.getCredentials(), user.getPassword())){
                user.setPassword("");
                return SecurityUtils.setCurrentUser(user);
            } else {
                //todo ex or stg
                System.out.println("wrong password");
                return null;
            }
        }
        //todo exception or stg
        System.out.println("User not found");
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication) ||
                AuthenticationToken.class.isAssignableFrom(authentication);
    }

}
