package com.ranbahar.imdbCelebs.unitTest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ranbahar.imdbCelebs.model.Celeb;
import com.ranbahar.imdbCelebs.model.Gender;
import com.ranbahar.imdbCelebs.model.services.ImdbService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class ImdbControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImdbService imdbService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Celeb> celebList = Arrays.asList(
            new Celeb("Ran Bahar", "Programmer", "Ran Bahar - Programmer since 2015", Gender.Male,
                    new URL("https://media-exp1.licdn.com/dms/image/C4E03AQFRmK4QwUpbGw/profile-displayphoto-shrink_200_200/0?e=1596067200&v=beta&t=GwVuIYgd112yZYUrl8ZmlNwjdBcdQaxfkIId5pr684c"),
                    LocalDate.of(1987, 5, 20)),
            new Celeb());

    ImdbControllerTests() throws MalformedURLException {
    }


    @Test
    public void getAllCelebritiesList() throws Exception {

        doReturn(celebList).when(imdbService).getAll();

        ResultActions resultActions = mockMvc.perform(get("/api/v1/imdb/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        MvcResult mvcResult = resultActions.andReturn();
        String json = mvcResult.getResponse().getContentAsString();


        //first way to deserialize
//        Celeb[] response = objectMapper.readValue(json, Celeb[].class);
        //second way to deserialize
//        List<Celeb> response2 = objectMapper.readValue(json, new TypeReference<List<Celeb>>() {
//        });

        //third way to deserialize
        List<Celeb> celebs = objectMapper.readValue(json, new TypeReference<List<Celeb>>() {
        });

        verify(imdbService, Mockito.times(1)).getAll();
        Assert.assertNotNull(celebs);
        Assert.assertEquals(celebs.size(), 2);

    }

    @Test
    public void getAllCelebritiesListFail() throws Exception {
        doThrow(new IllegalStateException("Failed of getting celeb list")).when(imdbService).getAll();

        mockMvc.perform(get("/api/v1/imdb/"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteSuccess() throws Exception {

        Celeb celeb = this.celebList.stream().findFirst().get();
        doNothing().when(imdbService).delete(celeb.getId());

        mockMvc.perform(delete("/api/v1/imdb/{id}", celeb.getId()))
                .andExpect(status().isNoContent());

        verify(imdbService, times(1)).delete(celeb.getId());
        verifyNoMoreInteractions(imdbService);
    }

    @Test
    public void deleteNotFound() throws Exception {

        Celeb celeb = this.celebList.stream().findFirst().get();

        doThrow(new IllegalStateException("Error occurred")).when(imdbService).delete(celeb.getId());

        mockMvc.perform(delete("/api/v1/imdb/{id}", celeb.getId()))
                .andExpect(status().isNotFound());

        verify(imdbService, times(1)).delete(celeb.getId());
        verifyNoMoreInteractions(imdbService);

    }

    @Test
    public void createSuccess() throws Exception {
        Celeb celeb = new Celeb("Test Tester", "Test", "Tester - test", Gender.Female, null, null);

        doReturn(celeb.getId()).when(imdbService).createCeleb(celeb);

        ResultActions resultActions = mockMvc.perform(post("/api/v1/imdb/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(celeb)))
                .andExpect(status().isCreated());

        String jsonId = resultActions.andReturn().getResponse().getContentAsString();

        verify(imdbService, times(1)).createCeleb(celeb);
        verifyNoMoreInteractions(imdbService);
        Assert.assertEquals(celeb.getId(), Integer.parseInt(jsonId));
    }

    @Test
    public void createNotFailed() throws Exception {
        Celeb celeb = new Celeb("Test Tester", "Test", "Tester - test", Gender.Female, null, null);

        doReturn(-1).when(imdbService).createCeleb(celeb);

        mockMvc.perform(post("/api/v1/imdb/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(celeb)))
                .andExpect(status().isBadRequest());


        verify(imdbService, times(1)).createCeleb(celeb);
        verifyNoMoreInteractions(imdbService);
    }

    @Test
    public void getByIdSuccess() throws Exception {
        Celeb celeb = new Celeb("Test Tester", "Test", "Tester - test", Gender.Female, null, null);

        doReturn(Optional.of(celeb)).when(imdbService).get(celeb.getId());

        ResultActions resultActions = mockMvc.perform(get("/api/v1/imdb/{id}", celeb.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String json = resultActions.andReturn().getResponse().getContentAsString();

        verify(imdbService, times(1)).get(celeb.getId());
        verifyNoMoreInteractions(imdbService);
        Assert.assertEquals(celeb, objectMapper.readValue(json, Celeb.class));
    }

    @Test
    public void getByIdFound() throws Exception {
        Celeb celeb = new Celeb("Test Tester", "Test", "Tester - test", Gender.Female, null, null);

        doThrow(new IllegalStateException("Error")).when(imdbService).get(celeb.getId());

        mockMvc.perform(get("/api/v1/imdb/{id}", celeb.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());


        verify(imdbService, times(1)).get(celeb.getId());
        verifyNoMoreInteractions(imdbService);
    }

    @Test
    public void updateSuccess() throws Exception {
        Celeb celeb = new Celeb("Test Tester", "Test", "Tester - test", Gender.Female, null, null);

        celeb.setDesc(celeb.getDesc() + " test test!!");
        doReturn(celeb).when(imdbService).update(celeb.getId(), celeb);

        ResultActions resultActions = mockMvc.perform(put("/api/v1/imdb/{id}", celeb.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(celeb)))
                .andExpect(status().isOk());

        String json = resultActions.andReturn().getResponse().getContentAsString();

        verify(imdbService, times(1)).update(celeb.getId(), celeb);
        verifyNoMoreInteractions(imdbService);
        Assert.assertEquals(celeb, objectMapper.readValue(json, Celeb.class));
    }

    @Test
    public void updateFailed() throws Exception {
        Celeb celeb = new Celeb("Test Tester", "Test", "Tester - test", Gender.Female, null, null);

        celeb.setDesc(celeb.getDesc() + " test test!!");
        doThrow(new IllegalStateException("update failed!!")).when(imdbService).update(celeb.getId(), celeb);

        mockMvc.perform(put("/api/v1/imdb/{id}", celeb.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void initSuccess() throws Exception {
        doReturn(this.celebList).when(imdbService).init();

        mockMvc.perform(get("/api/v1/imdb/init/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void initFailed() throws Exception {
        doThrow(new IllegalStateException("Error init failed!!")).when(imdbService).init();

        mockMvc.perform(get("/api/v1/imdb/init/"))
                .andExpect(status().isInternalServerError());
    }

}
