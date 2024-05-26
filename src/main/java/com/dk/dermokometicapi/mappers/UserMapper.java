package com.dk.dermokometicapi.mappers;

import com.dk.dermokometicapi.models.dto.UserRequestDTO;
import com.dk.dermokometicapi.models.dto.UserResponseDTO;
import com.dk.dermokometicapi.models.entities.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;

    public User convertToEntity(UserRequestDTO userRequestDTO) {
        return modelMapper.map(userRequestDTO, User.class);
    }

    public UserResponseDTO convertToDTO(User user) {
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public List<UserResponseDTO> convertToDTO(List<User> users) {
        return users.stream().map(this::convertToDTO).toList();
    }
}
