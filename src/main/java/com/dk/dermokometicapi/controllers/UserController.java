package com.dk.dermokometicapi.controllers;

import com.dk.dermokometicapi.models.dto.UserRequestDTO;
import com.dk.dermokometicapi.models.dto.UserResponseDTO;
import com.dk.dermokometicapi.models.dto.UserUpdateDTO;
import com.dk.dermokometicapi.models.dto.UserValidationDTO;
import com.dk.dermokometicapi.services.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    @Transactional
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @Transactional
    @GetMapping("/id/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> postUser(@RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.ok(userService.createUser(userRequestDTO));
    }

    @GetMapping("/login")
    public ResponseEntity<Boolean> validateUser(@RequestBody UserValidationDTO userValidationDTO) {
        boolean isValid = userService.validateUser(userValidationDTO);
        return ResponseEntity.ok(isValid);
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteUser(@RequestBody UserValidationDTO userValidationDTO) {
        userService.deleteUser(userValidationDTO);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> putUser(@PathVariable String username, @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.ok(userService.updateUser(username, userRequestDTO));
    }

    @PatchMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> patchUser(@PathVariable String username, @RequestBody UserUpdateDTO userUpdateDTO) {
        return ResponseEntity.ok(userService.patchUser(username, userUpdateDTO));
    }

}
