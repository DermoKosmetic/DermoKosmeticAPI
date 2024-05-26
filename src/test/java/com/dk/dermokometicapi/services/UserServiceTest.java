package com.dk.dermokometicapi.services;

import com.dk.dermokometicapi.models.dto.UserRequestDTO;
import com.dk.dermokometicapi.models.dto.UserResponseDTO;
import com.dk.dermokometicapi.models.dto.UserUpdateDTO;
import com.dk.dermokometicapi.models.dto.UserValidationDTO;
import com.dk.dermokometicapi.models.entities.User;
import com.dk.dermokometicapi.exceptions.BadRequestException;
import com.dk.dermokometicapi.exceptions.ResourceNotFoundException;
import com.dk.dermokometicapi.mappers.UserMapper;
import com.dk.dermokometicapi.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    public void testGetAllUser(){
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        List<UserResponseDTO> result = userService.getAllUser();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userMapper).convertToDTO(Collections.emptyList());
        verify(userRepository).findAll();
    }

    @Test
    public void testCreateUser(){
        //arrange
        String username = "username";
        String email = "email@example.com";

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername(user.getUsername());
        userRequestDTO.setEmail(user.getEmail());

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUsername(user.getUsername());
        userResponseDTO.setEmail(user.getEmail());

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userMapper.convertToEntity(userRequestDTO)).thenReturn(user);
        when(userMapper.convertToDTO(user)).thenReturn(userResponseDTO);
        //act
        UserResponseDTO result = userService.createUser(userRequestDTO);

        //Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());

        //verify
        verify(userRepository).save(user);
        verify(userMapper).convertToDTO(user);
        verify(userMapper).convertToEntity(userRequestDTO);
        verify(userRepository).existsByUsername(username);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    public void testCreateUser_UsernameAlreadyTaken() {
        //arranges
        String username = "username";
        String email = "email@example.com";

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername(user.getUsername());
        userRequestDTO.setEmail(user.getEmail());

        when(userRepository.existsByUsername(username)).thenReturn(true);
        assertThrows(BadRequestException.class, ()->userService.createUser(userRequestDTO));
    }

    @Test
    public void testCreateUser_EmailAlreadyTaken() {
        //arranges
        String username = "username";
        String email = "email@example.com";

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername(user.getUsername());
        userRequestDTO.setEmail(user.getEmail());

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(true);
        assertThrows(BadRequestException.class, ()->userService.createUser(userRequestDTO));
    }

    @Test
    public void testGetEntityByUsername() {
        //arrange
        String username = "username";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        //act
        User result = userService.getEntityByUsername(username);
        //Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        //verify
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void testGetEntityByUsername_UserNotFound(){
        String username = "username";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->userService.getEntityByUsername(username));
        //verify
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void testGetUserByUsername(){
        String username = "username";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUsername(username);
        when(userMapper.convertToDTO(user)).thenReturn(userResponseDTO);

        //Act
        UserResponseDTO result = userService.getUserByUsername(username);

        //Assert
        assertEquals(username, result.getUsername());

        //Verify
        verify(userRepository).findByUsername(username);
        verify(userMapper).convertToDTO(user);
    }

    @Test
    public void testGetUserByUsername_UserNotFound(){
        String username = "username";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->userService.getUserByUsername(username));
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void testGetUserById(){
        Long id = 1L;
        User user = new User();
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(id);
        when(userMapper.convertToDTO(user)).thenReturn(userResponseDTO);

        //Act
        UserResponseDTO result = userService.getUserById(id);

        //Assert
        assertEquals(id, result.getId());

        //Verify
        verify(userRepository).findById(id);
        verify(userMapper).convertToDTO(user);
    }

    @Test
    public void testGetUserById_UserNotFound(){
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->userService.getUserById(id));
        verify(userRepository).findById(id);
    }

    @Test
    public void testValidateUserByUsername(){
        String username = "username";
        String password = "password";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        assertTrue(userService.validateUserByUsername(username, password));
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void testValidateUserByUsername_UserNotFound(){
        String username = "username";
        String password = "password";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->userService.validateUserByUsername(username, password));
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void testValidateUserByEmail(){
        String email = "email@example.com";
        String password = "password";
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        when(userRepository.findByemail(email)).thenReturn(Optional.of(user));
        assertTrue(userService.validateUserByEmail(email, password));
        verify(userRepository).findByemail(email);
    }

    @Test
    public void testValidateUserByEmail_UserNotFound(){
        String email = "email@example.com";
        String password = "password";
        when(userRepository.findByemail(email)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->userService.validateUserByEmail(email, password));
        verify(userRepository).findByemail(email);
    }

    @Test
    public void testExistsByUsername(){
        String username = "username";
        when(userRepository.existsByUsername(username)).thenReturn(true);
        assertTrue(userService.existsByUsername(username));
        verify(userRepository).existsByUsername(username);
    }

    @Test
    public void testDeleteUserByUsername() {
    String username = "username";
    String password = "password";

    User user = new User();
    user.setUsername(username);
    user.setPassword(password);

    UserValidationDTO userValidationDTO = new UserValidationDTO();
    userValidationDTO.setUsername(username);
    userValidationDTO.setPassword(password);

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    boolean isValid =userService.validateUser(userValidationDTO);

    assertDoesNotThrow(() ->userService.deleteUser(userValidationDTO));
    verify(userRepository).deleteByUsername(username);
    }

    @Test
    public void testDeleteUserByEmail() {
    String username = "username";
    String email = "email@example.com";
    String password = "password";

    User user = new User();
    user.setEmail(email);
    user.setPassword(password);

    UserValidationDTO userValidationDTO = new UserValidationDTO();
    userValidationDTO.setEmail(email);
    userValidationDTO.setPassword(password);

    when(userRepository.findByemail(email)).thenReturn(Optional.of(user));
    boolean isValid =userService.validateUser(userValidationDTO);

    assertDoesNotThrow(() ->userService.deleteUser(userValidationDTO));
    verify(userRepository).deleteByEmail(email);
    }

    @Test
    public void testDeleteUser_BadRequest(){

    User user = new User();
    UserValidationDTO userValidationDTO = new UserValidationDTO();

    assertThrows(BadRequestException.class, ()->userService.deleteUser(userValidationDTO));

    }

    @Test
    public void testUpdateByUsername(){
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setEmail("email@example.com");
        user.setPassword("password");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername("updatedUsername");
        userRequestDTO.setEmail("updatedEmail@example.com");
        userRequestDTO.setPassword("updatedPassword");

        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setUsername(userRequestDTO.getUsername());
        updatedUser.setEmail(userRequestDTO.getEmail());
        updatedUser.setPassword(userRequestDTO.getPassword());
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(userMapper.convertToEntity(userRequestDTO)).thenReturn(updatedUser);

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(updatedUser.getId());
        userResponseDTO.setUsername(updatedUser.getUsername());
        userResponseDTO.setEmail(updatedUser.getEmail());
        when(userMapper.convertToDTO(updatedUser)).thenReturn(userResponseDTO);

        //Act
        UserResponseDTO result = userService.updateUser(user.getUsername(), userRequestDTO);

        //Assert
        assertNotNull(result);
        assertEquals(userResponseDTO.getId(), result.getId());
        assertEquals(userResponseDTO.getUsername(), result.getUsername());
        assertEquals(userResponseDTO.getEmail(), result.getEmail());

        verify(userRepository).findByUsername(user.getUsername());
        verify(userMapper).convertToEntity(userRequestDTO);
        verify(userRepository).save(updatedUser);
        verify(userMapper).convertToDTO(updatedUser);

    }

    @Test
    public void testUpdateByUsername_UserNotFound(){
        String username = "username";
        UserRequestDTO updatedUser = new UserRequestDTO();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->userService.updateUser(username, updatedUser));
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void testPatchByUsername(){
        String username = "username";

        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail("email@example.com");
        user.setPassword("password");
        user.setProfilePic("profilePic.jpg");

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername("updatedUsername");
        userUpdateDTO.setEmail("updatedEmail@example.com");
        userUpdateDTO.setPassword("updatedPassword");
        userUpdateDTO.setProfilePic("updatedProfilePic.jpg");

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setUsername(userUpdateDTO.getUsername());
        userResponseDTO.setEmail(userUpdateDTO.getEmail());
        userResponseDTO.setProfilePic(userUpdateDTO.getProfilePic());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userMapper.convertToDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.patchUser(username, userUpdateDTO);

        assertNotNull(result);
        assertEquals(userResponseDTO.getId(), result.getId());
        assertEquals(userUpdateDTO.getUsername(), result.getUsername());
        assertEquals(userUpdateDTO.getEmail(), result.getEmail());
        assertEquals(userUpdateDTO.getProfilePic(), result.getProfilePic());

        verify(userRepository).findByUsername(username);
        verify(userRepository).save(user);
        verify(userMapper).convertToDTO(user);
    }

     @Test
    public void testPatchUser_UserNotFound() {
        String username = "username";

        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail("email@example.com");
        user.setPassword("password");
        user.setProfilePic("profilePic.jpg");

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername("updatedUsername");
        userUpdateDTO.setEmail("updatedEmail@example.com");
        userUpdateDTO.setPassword("updatedPassword");
        userUpdateDTO.setProfilePic("updatedProfilePic.jpg");

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setUsername(userUpdateDTO.getUsername());
        userResponseDTO.setEmail(userUpdateDTO.getEmail());
        userResponseDTO.setProfilePic(userUpdateDTO.getProfilePic());

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.patchUser(username, userUpdateDTO));

        assertEquals("User not found with username: " + username, exception.getMessage());

        verify(userRepository).findByUsername(username);
    }

    @Test
    public void testValidateUser_Username() {

        String username = "username";
        String email = "email@example.com";
        String password = "password";

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        UserValidationDTO userValidationDTO = new UserValidationDTO();
        userValidationDTO.setUsername(username);
        userValidationDTO.setEmail(email);
        userValidationDTO.setPassword(password);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        boolean isValid=userService.validateUser(userValidationDTO);

        assertEquals(true,isValid);
    }

    @Test
    public void testValidateUser_Email() {

        String username = "username";
        String email = "email@example.com";
        String password = "password";

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        UserValidationDTO userValidationDTO = new UserValidationDTO();
        userValidationDTO.setEmail(email);
        userValidationDTO.setPassword(password);

        when(userRepository.findByemail(user.getEmail())).thenReturn(Optional.of(user));

        boolean isValid=userService.validateUser(userValidationDTO);

        assertEquals(true,isValid);
    }

    @Test
    public void testValidateUser_BadRequest() {

        String username = "username";
        String email = "email@example.com";
        String password = "password";

        User user = new User();

        UserValidationDTO userValidationDTO = new UserValidationDTO();

        assertThrows(BadRequestException.class, ()->userService.validateUser(userValidationDTO));
    }

    @Test
    public void getEntityById(){
        Long id = 1L;
        User user = new User();
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        User result = userService.getEntityById(id);
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(userRepository).findById(id);
    }

    @Test
    public void getEntityById_UserNotFound(){
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->userService.getEntityById(id));
        verify(userRepository).findById(id);
    }


}
