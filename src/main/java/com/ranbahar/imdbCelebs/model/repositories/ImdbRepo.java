package com.ranbahar.imdbCelebs.model.repositories;

import com.ranbahar.imdbCelebs.model.Celeb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ImdbRepo {

    List<Celeb> init();

    void delete(int id);

    Optional<Celeb> get(int id);

    List<Celeb> getAll();

    Celeb update(int id, Celeb celeb);

    int createCeleb(Celeb celeb);

    int addRan();
}
