package com.dk.dermokometicapi.model.mapper;

import com.dk.dermokometicapi.model.dto.UserRequestDTO;
import com.dk.dermokometicapi.model.dto.UserResponseDTO;
import com.dk.dermokometicapi.model.entity.User;
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
