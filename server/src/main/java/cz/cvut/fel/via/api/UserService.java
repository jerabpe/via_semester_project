package cz.cvut.fel.via.api;

import cz.cvut.fel.via.api.model.LoginDto;
import cz.cvut.fel.via.api.model.UserDto;
import cz.cvut.fel.via.db.model.User;
import cz.cvut.fel.via.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    }

    @Override
    //todo admin only
    public ResponseEntity<Void> userIdDelete(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    //todo admin only
    public ResponseEntity<List<UserDto>> userGet() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        users.forEach(u -> {
            UserDto dto = new UserDto();
            dto.setUsername(u.getUsername());
            dto.setId(u.getId());
            userDtos.add(dto);
        });
        return ResponseEntity.ok(userDtos);
    }

    @Override
    public ResponseEntity<UserDto> userPost(LoginDto loginDto) {
        User u = new User();
        u.setUsername(loginDto.getUsername());
        u.setPassword(passwordEncoder.encode(loginDto.getPassword()));
        try {
            userRepository.saveAndFlush(u);
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        UserDto userDto = new UserDto();
        userDto.setUsername(u.getUsername());
        userDto.setId(u.getId());
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }
}
