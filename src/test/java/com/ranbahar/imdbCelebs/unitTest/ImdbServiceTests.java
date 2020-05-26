package com.ranbahar.imdbCelebs.unitTest;

import com.ranbahar.imdbCelebs.model.Celeb;
import com.ranbahar.imdbCelebs.model.Gender;
import com.ranbahar.imdbCelebs.model.repositories.ImdbRepo;
import com.ranbahar.imdbCelebs.model.services.ImdbService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
//instead of change setUp to static
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ImdbServiceTests {

    @Autowired
    private ImdbService imdbService;

    @MockBean
    private ImdbRepo repo;

    private List<Celeb> celebList;


    @BeforeAll
    public void setUp() {
        System.out.println("Run Before every Test");
        this.celebList = Arrays.asList(new Celeb("Ran Bahar", "Actor", "bla bla", Gender.Male, null, LocalDate.of(1987, 02, 14))
                , new Celeb("Dan Dan", "Producer", "Daba Daba", Gender.Male, null, LocalDate.now()));
    }

    @Test
    @DisplayName("Test find by id")
    public void getById() {
        int id = 1;
        Mockito.doReturn(Optional.of(celebList.get(0))).when(repo).get(any(Integer.class));
        Celeb result = imdbService.get(id).orElse(null);

        Assert.assertTrue("Celeb Not Found", result != null);
        Assert.assertEquals("the first id should be 1", result != null ? result.getId() : -1, this.celebList.stream().findFirst().get().getId());
    }

    @Test
    @DisplayName("Test find by Id failed")
    public void getByIdFail() {
        int id = 1;

        Mockito.doReturn(Optional.empty()).when(repo).get(any(Integer.class));
        Celeb result = imdbService.get(id).orElse(null);

        Assert.assertFalse("Celeb Not Found", result != null);
        Assert.assertNotEquals("the first id should be 1", result != null ? result.getId() : -1, this.celebList.stream().findFirst().get().getId());
    }

    @Test
    @DisplayName("Test Get all")
    public void getAll() {

        Mockito.doReturn(this.celebList).when(repo).getAll();
        List<Celeb> result = imdbService.getAll();

        Assert.assertTrue("Celebs Not Found", result != null);
        Assert.assertEquals("total celebs should be 2", result != null ? result.size() : -1, this.celebList.size());
    }

    @Test
    @DisplayName("Test Get all - Failure")
    public void getAllFailure() {
        Mockito.doReturn(List.of()).when(repo).getAll();
        List<Celeb> result = imdbService.getAll();

        Assert.assertTrue("Celebs Not Found", result.isEmpty());
        Assert.assertEquals("the list is empty 0", result.size(), 0);
    }

    @Test
    @DisplayName("Test Delete")
    public void delete() {
        int id = 1;
        ArrayList<Celeb> newCelebList = new ArrayList<>(this.celebList);

        Mockito.doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                newCelebList.removeIf(c -> c.getId() == id);
                return null;
            }
        }).when(repo).delete(id);

        imdbService.delete(id);

        Assert.assertFalse("Celebs Not Found", newCelebList.stream().anyMatch(c -> c.getId() == id));
        Assert.assertEquals("Celeb remove from the celeb list", this.celebList.size() - 1, newCelebList.size());
    }

    @Test
    @DisplayName("Test Delete Failure")
    public void deleteFailure() {
        int id = -1;
        imdbService.delete(id);

        Assert.assertFalse("Celebs Not Found", this.celebList.stream().anyMatch(c -> c.getId() == id));
        Assert.assertEquals("Celeb remove from the celeb list", this.celebList.size(), 2);
    }

    @Test
    @DisplayName("Test Create")
    public void create() {
        Celeb celebExpected = new Celeb("Teser Tester", "Tester", "Test Test Test 12", Gender.Male, null, LocalDate.now());
        ArrayList<Celeb> newCelebList = new ArrayList<>(this.celebList);

        Mockito.doAnswer(new Answer() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                newCelebList.add(celebExpected);
                return celebExpected.getId();
            }
        }).when(repo).createCeleb(any(Celeb.class));

        int resultId = imdbService.createCeleb(celebExpected);

        Assert.assertTrue("find Id in the celeb list", newCelebList.get(2).getId() == resultId);
        Assert.assertEquals("Celeb list is increase by 1", this.celebList.size() + 1, newCelebList.size());
    }

    @Test
    @DisplayName("Test Create Failure")
    public void createFailure() {
        Celeb celebExpected = new Celeb("Teser Tester", "Tester", "Test Test Test 12", Gender.Male, null, LocalDate.now());
        Mockito.doReturn(-1).when(repo).createCeleb(any(Celeb.class));

        int resultId = imdbService.createCeleb(celebExpected);

        Assert.assertFalse("find Id in the celeb list", this.celebList.stream().anyMatch(c -> c.getId() == resultId));
        Assert.assertNotEquals("Celeb list is increase by 1", this.celebList.size(), 3);
    }

    @Test
    @DisplayName("Test update success")
    public void updateSuccess() {
        Celeb celebExpected = new Celeb("Teser Tester", "Tester", "Test Test Test 12", Gender.Male, null, LocalDate.now());
        Mockito.doReturn(celebExpected).when(repo).update(any(Integer.class), any(Celeb.class));

        celebExpected.setDesc("new desc");
        Celeb result = imdbService.update(celebExpected.getId(), celebExpected);

        Assert.assertEquals("Celeb list is increase by 1", result, celebExpected);
    }

    @Test
    @DisplayName("Test update Failure")
    public void updateFailure() {
        Celeb celebExpected = new Celeb("Teser Tester", "Tester", "Test Test Test 12", Gender.Male, null, LocalDate.now());
        Mockito.doReturn(null).when(repo).update(any(Integer.class), any(Celeb.class));

        celebExpected.setDesc("asdsd");
        Celeb result = imdbService.update(celebExpected.getId(), celebExpected);

        Assert.assertNull("result is empty", result);
    }

}
