package cz.cvut.fel.via.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.via.api.UserService;
import cz.cvut.fel.via.api.model.UserDto;
import cz.cvut.fel.via.db.model.User;
import cz.cvut.fel.via.db.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class AuthenticationSuccess implements AuthenticationSuccessHandler, LogoutSuccessHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationSuccess.class);
    private final UserRepository userRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public AuthenticationSuccess(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        final String username = ((User) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username).get();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Successfully authenticated user {}", username);
        }
        final UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setIsAdmin(user.isAdmin());
        objectMapper.writeValue(response.getOutputStream(), userDto);
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Successfully logged out user {}", ((User) authentication.getPrincipal()).getUsername());
        }
        final UserDto userDto = new UserDto();
        objectMapper.writeValue(response.getOutputStream(), userDto);

    }
}
