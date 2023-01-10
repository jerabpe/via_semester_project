package cz.cvut.fel.via.api;

import cz.cvut.fel.via.api.model.LoginDto;
import cz.cvut.fel.via.api.model.UserDto;
import cz.cvut.fel.via.db.model.User;
import cz.cvut.fel.via.db.repository.UserRepository;
import cz.cvut.fel.via.security.AuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserApiDelegate {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        User u = new User();
        u.setUsername("admin");
        u.setPassword(passwordEncoder.encode("admin"));
        u.setAdmin(true);
        this.userRepository.saveAndFlush(u);
    }

    @Override
    public ResponseEntity<Void> userIdDelete(Long id) {
        Principal p = null;
        if(RequestContextHolder.getRequestAttributes() == null){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            p = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getUserPrincipal();
        }
        if(p == null){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        AuthenticationToken token = (AuthenticationToken) p;
        if(token.getPrincipal() == null){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        User u = (User) token.getPrincipal();
        if(u.isAdmin()) {
            try {
                userRepository.deleteById(id);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<List<UserDto>> userGet() {
        Principal p = null;
        if(RequestContextHolder.getRequestAttributes() == null){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            p = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getUserPrincipal();
        }
        if(p == null){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        AuthenticationToken token = (AuthenticationToken) p;
        if(token.getPrincipal() == null){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        User u = (User) token.getPrincipal();
        if(u.isAdmin()) {
            List<User> users = userRepository.findAll();
            List<UserDto> userDtos = new ArrayList<>();
            users.forEach(us -> {
                UserDto dto = new UserDto();
                dto.setUsername(us.getUsername());
                dto.setId(us.getId());
                dto.setIsAdmin(us.isAdmin());
                userDtos.add(dto);
            });
            return ResponseEntity.ok(userDtos);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<UserDto> userPost(LoginDto loginDto) {
        User u = new User();
        u.setUsername(loginDto.getUsername());
        u.setPassword(passwordEncoder.encode(loginDto.getPassword()));
        u.setAdmin(false);
        try {
            userRepository.saveAndFlush(u);
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        UserDto userDto = new UserDto();
        userDto.setUsername(u.getUsername());
        userDto.setId(u.getId());
        userDto.setIsAdmin(false);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }
}
