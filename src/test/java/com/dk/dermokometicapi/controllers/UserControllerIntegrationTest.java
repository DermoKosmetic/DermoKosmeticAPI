package com.dk.dermokometicapi.controllers;

import com.dk.dermokometicapi.models.dto.*;
import com.dk.dermokometicapi.models.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    private List<Writer>  getWriters() {
        List<Writer> writers = new ArrayList<>(List.of());
        for(int i = 0; i < 5; i++) {
            Writer writer = new Writer();
            writer.setName("Writer " + i);
            writer.setLastName("Surname " + i);
            writer.setDescription("Description " + i);
            writer.setProfilePic("Pic " + i);
            writers.add(writer);
        }
        return writers;
    }

    private User createTestUser() {
        User user = new User();
        user.setUsername("Username X");
        user.setEmail("Email X");
        user.setPassword("Password X");
        user.setProfilePic("Pic X");
        return user;
    }

    private UserRequestDTO createTestUserRequestDTO() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername("Username X");
        userRequestDTO.setEmail("Email X");
        userRequestDTO.setPassword("Password X");
        userRequestDTO.setProfilePic("Pic X");
        return userRequestDTO;
    }

    private UserValidationDTO createTestUserValidationDTO() {
        UserValidationDTO userValidationDTO = new UserValidationDTO();
        userValidationDTO.setUsername("Username X");
        userValidationDTO.setPassword("Password X");
        return userValidationDTO;
    }

    private UserUpdateDTO createTestUserUpdateDTO() {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setEmail("Email X");
        userUpdateDTO.setPassword("Password X");
        return userUpdateDTO;
    }

    @Test
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetUserByUsername() throws Exception {

        User user = createTestUser();
        entityManager.persist(user);

        String username = user.getUsername();

        mockMvc.perform(MockMvcRequestBuilders.get("/users/username/{username}", username))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetUserById() throws Exception {

        User user = createTestUser();
        entityManager.persist(user);

        Long id = user.getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/users/id/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testPostUser() throws Exception {
        UserRequestDTO userRequestDTO = createTestUserRequestDTO();

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void testValidateUser() throws Exception {

        User user = createTestUser();
        entityManager.persist(user);

        UserValidationDTO userValidationDTO = createTestUserValidationDTO();
        userValidationDTO.setUsername(user.getUsername());
        userValidationDTO.setPassword(user.getPassword());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userValidationDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeleteUserById() throws Exception {

        User user = createTestUser();
        entityManager.persist(user);

        UserValidationDTO userValidationDTO = createTestUserValidationDTO();
        userValidationDTO.setUsername(user.getUsername());
        userValidationDTO.setPassword(user.getPassword());

        mockMvc.perform(MockMvcRequestBuilders.delete("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userValidationDTO)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testPutUser() throws Exception {

        User user = createTestUser();
        entityManager.persist(user);

        UserRequestDTO userRequestDTO = createTestUserRequestDTO();
        userRequestDTO.setUsername("New Username");
        userRequestDTO.setEmail("New Email");
        userRequestDTO.setPassword("New Password");

        mockMvc.perform(MockMvcRequestBuilders.put("/users/username/{username}", user.getUsername())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testPatchUser() throws Exception {

        User user = createTestUser();
        entityManager.persist(user);

        UserUpdateDTO userUpdateDTO = createTestUserUpdateDTO();

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/username/{username}", user.getUsername())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userUpdateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
}


    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
