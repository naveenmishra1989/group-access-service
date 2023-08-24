package com.naveen.groupaccessservice.controller;

import com.naveen.groupaccessservice.UserMapper;
import com.naveen.groupaccessservice.common.UserConstant;
import com.naveen.groupaccessservice.dto.UserDTO;
import com.naveen.groupaccessservice.entity.User;
import com.naveen.groupaccessservice.entity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.naveen.groupaccessservice.common.UserConstant.ADMIN_ACCESS;
import static com.naveen.groupaccessservice.common.UserConstant.DEFAULT_ROLE;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @PostMapping("/join")
    public String joinGroup(@RequestBody UserDTO userDTO) {
        userDTO.setRoles(DEFAULT_ROLE);
        userDTO.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        User save = userRepository.save(userMapper.userDtoToUser(userDTO));
        log.info("User joined successfully : " + save);
        return "User joined successfully: " + save.getUserName();
    }

    @GetMapping("/access/{userId}/{userRole}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String giveAccessToUser(@PathVariable int userId, @PathVariable String userRole, Principal principal) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("user not found:" + userId));
        List<String> activeRoles = getRolesByLoggedInUser(principal);
        String newRole;
        if (activeRoles.contains(userRole)) {
            newRole = user.getRoles() + "," + userRole;
            user.setRoles(newRole.toUpperCase());
        }
        userRepository.save(user);
        return "Hi " + user.getUserName() + " New Role assign to you by " + principal.getName();
    }

    @GetMapping
   // @Secured("ROLE_ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<User> loadUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/test")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String testUserAccess() {
        return "user can only access this !";
    }

    private List<String> getRolesByLoggedInUser(Principal principal) {
        String roles = getLoggedInUser(principal).getRoles();
        List<String> assignRoles = Arrays.stream(roles.split(",")).collect(Collectors.toList());
        if (assignRoles.contains("ROLE_ADMIN")) {
            return Arrays.stream(ADMIN_ACCESS).collect(Collectors.toList());
        }
        if (assignRoles.contains("ROLE_MODERATOR")) {
            return Arrays.stream(UserConstant.MODERATOR_ACCESS).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private User getLoggedInUser(Principal principal) {
        return userRepository.findByUserNameIgnoreCase(principal.getName())
                .orElseThrow(() ->
                        new UsernameNotFoundException("user not found:" + principal.getName()));
    }
}



