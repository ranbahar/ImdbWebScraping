package com.ranbahar.imdbCelebs.model.services;

import com.ranbahar.imdbCelebs.model.Celeb;

import java.util.List;
import java.util.Optional;

public interface ImdbService {

    List<Celeb> init();

    Optional<Celeb> get(int id);

    void delete(int id);

    List<Celeb> getAll();

    Celeb update(int id, Celeb celeb);

    int createCeleb(Celeb celeb);

    int addRan();
}
