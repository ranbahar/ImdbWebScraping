package com.ranbahar.imdbCelebs.unitTest;

import com.ranbahar.imdbCelebs.model.Celeb;
import com.ranbahar.imdbCelebs.model.Gender;
import com.ranbahar.imdbCelebs.model.repositories.ImdbRepo;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class ImdbRepositoryTests {

    private static final int TOP_CELEBS = 100;

    @Autowired
    ImdbRepo repo;

    @Test
    @DisplayName("Test init")
    public void init() {
        List<Celeb> celebs = repo.init();

        Assert.assertEquals("top 100 Celeb", TOP_CELEBS, celebs.size());
    }

    @Test
    @DisplayName("Test get all")
    @Order(1)
    public void getAll() {
        List<Celeb> celebs = repo.getAll();

        Assert.assertEquals("top 100 Celeb", TOP_CELEBS, celebs.size());
    }

    @Test
    @DisplayName("Test get by id")
    @Order(2)
    public void get() {
        int id = 1;
        Optional<Celeb> celeb = repo.get(id);

        Assert.assertNotNull(celeb.get());
        Assert.assertEquals("Celeb id the same", id, celeb.get().getId());
    }

    @Test
    @DisplayName("Test get by id failure")
    @Order(3)
    public void getFailure() {
        int id = -1;
        Optional<Celeb> celeb = repo.get(id);

        Assert.assertTrue("id is not exist", celeb.isEmpty());
    }

    @Test
    @DisplayName("Test delete")
    @Order(4)
    public void delete() {
        System.out.println("delete");
        int id = 2;
        Celeb celeb = repo.get(2).get();
        repo.delete(id);

        Assert.assertEquals("list is now 99", TOP_CELEBS - 1, this.repo.getAll().size());
        Assert.assertFalse("id is not exists", repo.getAll().stream().anyMatch(c -> c.getId() == id));

        repo.createCeleb(celeb);
    }

    @Test
    @DisplayName("Test delete failure")
    @Order(5)
    public void deleteFailure() {
        System.out.println("Delete failure");
        int id = -2;
        repo.delete(id);

        Assert.assertEquals("list is still 100", TOP_CELEBS, this.repo.getAll().size());
        Assert.assertFalse("id is not valid", repo.getAll().stream().anyMatch(c -> c.getId() == id));
    }

    @Test
    @DisplayName("Test create")
    @Order(6)
    public void createCeleb() {
        Celeb celebExpected = new Celeb("Teser Tester", "Tester", "Test Test Test 12", Gender.Male, null, LocalDate.now());
        int id = repo.createCeleb(celebExpected);

        Assert.assertEquals(String.format("celeb list is now {0}", TOP_CELEBS), TOP_CELEBS + 1, repo.getAll().size());
        Assert.assertTrue("id is valid", repo.getAll().stream().anyMatch(c -> c.getId() == id));

        repo.delete(id);

    }

    @Test
    @DisplayName("Test create failure")
    @Order(7)
    public void createCelebFailure() {
        Celeb celebExpected = new Celeb();
        int id = repo.createCeleb(celebExpected);

        Assert.assertNotEquals(String.format("celeb list is now {0}", TOP_CELEBS), TOP_CELEBS + 1, repo.getAll().size());
        Assert.assertFalse("id is not valid", repo.getAll().stream().anyMatch(c -> c.getId() == id));
    }

    @Test
    @DisplayName("Test update")
    public void update() {
        int id = 1;
        Celeb celebExpect = repo.get(id).isEmpty() ? null : repo.get(id).get();

        if (celebExpect.getId() > 0) {
            celebExpect.setGender(Gender.Female);
            celebExpect.setDesc("Bla Bla Bla Bla .....");
        }
        Celeb actual = repo.update(id, celebExpect);

        Assert.assertNotNull("Object is not null", actual);
        Assert.assertEquals("two object are the same", celebExpect, actual);
        Assert.assertEquals("description is the same", celebExpect.getDesc(), actual.getDesc());

    }


    @Test
    @DisplayName("Test update failure")
    public void updateFailure() {
        int id = -1;
        Celeb celebExpect = repo.get(id).isEmpty() ? null : repo.get(id).get();

        Celeb actual = repo.update(id, celebExpect);

        Assert.assertNull("Object is null", actual);
    }

}
