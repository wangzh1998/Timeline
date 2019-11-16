package com.ecnu.timeline.controller;

import com.ecnu.timeline.service.TimelineService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class TimelineControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TimelineService service;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void init() throws Exception {
        ResultActions perform = mockMvc.perform(get("/timeline"));
        perform.andDo(print()).andExpect(status().isOk());
        verify(service,times(1)).init();
    }

    @Test
    void insert() throws Exception {
        ResultActions perform = mockMvc.perform(get("/timeline/insert"));
        perform.andDo(print()).andExpect(status().isOk());
        verify(service,times(1)).insert();

    }

    @Test
    void update() throws Exception {
        ResultActions perform = mockMvc.perform(get("/timeline/update"));
        perform.andDo(print()).andExpect(status().isOk());
        verify(service,times(1)).update();

    }

    @Test
    void showmore() throws Exception {
        ResultActions perform = mockMvc.perform(get("/timeline/showmore"));
        perform.andDo(print()).andExpect(status().isOk());
        verify(service,times(1)).showmore();
    }
}