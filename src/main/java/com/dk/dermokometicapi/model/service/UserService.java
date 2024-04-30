package com.dk.dermokometicapi.model.service;

import com.dk.dermokometicapi.model.dto.UserResponseDTO;
import com.dk.dermokometicapi.model.dto.UserUpdateDTO;
import com.dk.dermokometicapi.model.entity.User;
import com.dk.dermokometicapi.model.exception.BadRequestException;
import com.dk.dermokometicapi.model.exception.ResourceNotFoundException;
import com.dk.dermokometicapi.model.mapper.UserMapper;
import com.dk.dermokometicapi.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponseDTO> getAllUser () {
        List<User> users = userRepository.findAll();
        return userMapper.convertToDTO(users);
    }

    public User getEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return userMapper.convertToDTO(user);
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.convertToDTO(user);
    }

    public UserResponseDTO getUserByEmailAndPassword(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this credentials"));
        return userMapper.convertToDTO(user);
    }

    public UserResponseDTO getUserByUsernameAndPassword(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this credentials"));
        return userMapper.convertToDTO(user);
    }

    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void deleteByUsername(String username) {
        userRepository.deleteByUsername(username);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public UserResponseDTO updateByUsername(String username, User updatedUser) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        updatedUser.setId(user.getId());
        userRepository.save(updatedUser);
        return userMapper.convertToDTO(updatedUser);
    }

    public UserResponseDTO patchByUsername(String username, UserUpdateDTO updatedUser) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        if (updatedUser.getUsername() != null) {
            user.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null) {
            user.setPassword(updatedUser.getPassword());
        }
        userRepository.save(user);
        return userMapper.convertToDTO(user);
    }
}
