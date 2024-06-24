package com.dk.dermokometicapi.controllers;

import com.dk.dermokometicapi.models.entities.Writer;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class WriterControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    private Writer createTestWriter(){
        Writer writer = new Writer();
        writer.setName("Name X");
        writer.setLastName("Last Name X");
        return writer;
    }

    @Test
    public void testGetAllWriters() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/writers"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetWriterById() throws Exception {
        Writer writer = createTestWriter();
        entityManager.persist(writer);
        entityManager.flush();

        Long writerId = writer.getId();
        mockMvc.perform(MockMvcRequestBuilders.get("/writers/id/{id}", writerId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetWritersByIds() throws Exception {
        Writer writer1 = createTestWriter();
        entityManager.persist(writer1);
        entityManager.flush();

        Writer writer2 = createTestWriter();
        entityManager.persist(writer2);
        entityManager.flush();

        mockMvc.perform(MockMvcRequestBuilders.post("/writers/id")
                .contentType("application/json")
                .content("[1, 2]"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
