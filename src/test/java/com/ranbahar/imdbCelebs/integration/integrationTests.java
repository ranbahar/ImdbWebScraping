package com.ranbahar.imdbCelebs.integration;

import com.ranbahar.imdbCelebs.model.Celeb;
import com.ranbahar.imdbCelebs.model.Gender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class integrationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static final int TOP_CELEB = 100;
    private String URL;

    @Before
    public void setURL() {
        URL = "http://localhost:" + port + "/api/v1/imdb/";
    }

    @Test
    public void getAllCelebrities() throws Exception {
        ResponseEntity<List> responseEntity =
                this.testRestTemplate.getForEntity(URL, List.class);

        assertEquals(responseEntity.getStatusCode(), (HttpStatus.OK));
        assertEquals(responseEntity.getBody().size(), TOP_CELEB);
    }

    @Test
    public void initCelebritiesList() throws Exception {
        ResponseEntity<List> responseEntity =
                this.testRestTemplate.getForEntity(URL + "init/", List.class);

        assertEquals(responseEntity.getStatusCode(), (HttpStatus.OK));
        assertEquals(responseEntity.getBody().size(), TOP_CELEB);
    }

    @Test
    public void getCelebrityById() {
        ResponseEntity<Celeb> responseEntity =
                this.testRestTemplate.getForEntity(URL + "1/", Celeb.class);

        Celeb actual = responseEntity.getBody();

        assertEquals(responseEntity.getStatusCode(), (HttpStatus.OK));
        assertEquals(actual.getId(), 1);
        assertEquals(actual.getName(), "Johnny Depp");
        assertEquals(actual.getDesc(), "Johnny Depp is perhaps one of the most versatile actors of his day and age in Hollywood. He was born John Christopher Depp II in Owensboro, Kentucky, on June 9, 1963, to Betty Sue (Wells), who worked as a waitress, and John Christopher Depp, a civil engineer. Depp was raised in Florida. He dropped ...");
        assertEquals(actual.getTitle(), "Actor");
        assertEquals(actual.getGender(), Gender.Male);
    }

    @Test
    public void getCelebrityByIdNotFound() {
        ResponseEntity<Celeb> responseEntity =
                this.testRestTemplate.getForEntity(URL + "-1/", Celeb.class);

        assertEquals(responseEntity.getStatusCode(), (HttpStatus.NOT_FOUND));
    }

    @Test
    public void getCelebrityByIdBadRequest() {
        ResponseEntity<Celeb> responseEntity =
                this.testRestTemplate.getForEntity(URL + "asd/", Celeb.class);

        assertEquals(responseEntity.getStatusCode(), (HttpStatus.BAD_REQUEST));
    }

    @Test
    public void createCelebritySuccess() throws MalformedURLException {
        Celeb ranBahar = getRan();

        int responseEntity =
                this.testRestTemplate.postForObject(URL, ranBahar, Integer.class);

        assertEquals(responseEntity, ranBahar.getId());

        this.testRestTemplate.delete(URL + ranBahar.getId());
    }

    @Test
    public void createCelebrityBadRequest() {
        HttpEntity<Celeb> re = new HttpEntity<>(new Celeb());
        ResponseEntity<Integer> responseEntity =
                this.testRestTemplate.exchange(URL, HttpMethod.POST, re, Integer.class);

        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void deleteCelebritySuccess() throws MalformedURLException {
        Celeb celeb = getRan();
        this.testRestTemplate.postForEntity(URL, celeb, Integer.class);

        ResponseEntity<Void> responseEntity =
                this.testRestTemplate.exchange(URL + celeb.getId(), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertEquals(responseEntity.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void deleteCelebrityNoContent() throws MalformedURLException {
        Celeb celeb = new Celeb();

        ResponseEntity<Void> responseEntity =
                this.testRestTemplate.exchange(URL + celeb.getId(), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertEquals(responseEntity.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void updateCelebritySuccess() throws MalformedURLException {
        Celeb celeb = getRan();
        this.testRestTemplate.postForEntity(URL, celeb, Integer.class);

        celeb.setDesc("Testttttttttttttttttt");
        ResponseEntity<Celeb> responseEntity =
                this.testRestTemplate.exchange(URL + celeb.getId(), HttpMethod.PUT, new HttpEntity<>(celeb), Celeb.class);

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(celeb, responseEntity.getBody());

        this.testRestTemplate.delete(URL + celeb.getId());
    }

    @Test
    public void updateCelebrityNotFound() throws MalformedURLException {
        Celeb celeb = new Celeb();

        celeb.setDesc("Testttttttttttttttttt");
        ResponseEntity<Celeb> responseEntity =
                this.testRestTemplate.exchange(URL + celeb.getId(), HttpMethod.PUT, new HttpEntity<>(celeb), Celeb.class);

        assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(responseEntity.getBody());
    }

    private Celeb getRan() throws MalformedURLException {
        return new Celeb("Ran Bahar", "Programmer", "Ran Bahar - Programmer since 2015", Gender.Male,
                new URL("https://media-exp1.licdn.com/dms/image/C4E03AQFRmK4QwUpbGw/profile-displayphoto-shrink_200_200/0?e=1596067200&v=beta&t=GwVuIYgd112yZYUrl8ZmlNwjdBcdQaxfkIId5pr684c"),
                LocalDate.of(1987, 5, 20));
    }
}
