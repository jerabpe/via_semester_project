package cz.cvut.fel.via.security;

import cz.cvut.fel.via.db.model.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AuthenticationToken extends AbstractAuthenticationToken implements Principal {

    private User user;

    public AuthenticationToken(User user) {
        super(List.of(new SimpleGrantedAuthority("user")));
        this.user = user;
        super.setAuthenticated(true);
        super.setDetails(user);
    }

    @Override
    public Object getCredentials() {
        return user.getPassword();
    }

    @Override
    public Object getPrincipal() {
        return user;
    }
}
