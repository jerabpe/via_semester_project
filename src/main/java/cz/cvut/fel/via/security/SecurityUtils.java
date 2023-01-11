package cz.cvut.fel.via.security;

import cz.cvut.fel.via.db.model.User;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

public class SecurityUtils {

    public static AuthenticationToken setCurrentUser(User user){
        final AuthenticationToken token = new AuthenticationToken(user);
        token.setAuthenticated(true);

        final SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(token);
        SecurityContextHolder.setContext(context);
        return token;
    }
}
