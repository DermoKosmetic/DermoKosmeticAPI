package com.dk.dermokometicapi.services;

import com.dk.dermokometicapi.exceptions.BadRequestException;
import com.dk.dermokometicapi.exceptions.ResourceNotFoundException;
import com.dk.dermokometicapi.models.dto.UserRequestDTO;
import com.dk.dermokometicapi.models.dto.UserResponseDTO;
import com.dk.dermokometicapi.models.dto.UserUpdateDTO;
import com.dk.dermokometicapi.models.dto.UserValidationDTO;
import com.dk.dermokometicapi.models.entities.User;
import com.dk.dermokometicapi.mappers.UserMapper;
import com.dk.dermokometicapi.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponseDTO> getAllUser () {
        List<User> users = userRepository.findAll();
        return userMapper.convertToDTO(users);
    }

    public UserResponseDTO createUser(UserRequestDTO user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Email is already taken!");
        }

        User newUser = userMapper.convertToEntity(user);

        userRepository.save(newUser);
        return userMapper.convertToDTO(newUser);
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

    public boolean validateUserByUsername(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return user.getPassword().equals(password);
    }

    public boolean validateUserByEmail(String email, String password) {
        User user = userRepository.findByemail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return user.getPassword().equals(password);
    }

    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void deleteUser(UserValidationDTO userValidationDTO) {
        if(!validateUser(userValidationDTO)) {
            throw new BadRequestException("Invalid user credentials");
        }
        if(userValidationDTO.getUsername() != null) {
            userRepository.deleteByUsername(userValidationDTO.getUsername());
        }
        else{
            userRepository.deleteByEmail(userValidationDTO.getEmail());
        }
    }

    public UserResponseDTO updateUser(String username, UserRequestDTO userRequestDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        User updatedUser = userMapper.convertToEntity(userRequestDTO);
        updatedUser.setId(user.getId());
        userRepository.save(updatedUser);
        return userMapper.convertToDTO(updatedUser);
    }

    public UserResponseDTO patchUser(String username, UserUpdateDTO updatedUser) {
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
        if (updatedUser.getProfilePic() != null) {
            user.setProfilePic(updatedUser.getProfilePic());
        }
        userRepository.save(user);
        return userMapper.convertToDTO(user);
    }

    public boolean validateUser(UserValidationDTO userValidationDTO) {
        boolean isValid;
        if(userValidationDTO.getUsername() != null) {
            isValid = validateUserByUsername(userValidationDTO.getUsername(), userValidationDTO.getPassword());
        }
        else if(userValidationDTO.getEmail() != null) {
            isValid = validateUserByEmail(userValidationDTO.getEmail(), userValidationDTO.getPassword());
        }
        else {
            throw new BadRequestException("Username or Email must be provided");
        }
        return isValid;
    }

    public User getEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}
