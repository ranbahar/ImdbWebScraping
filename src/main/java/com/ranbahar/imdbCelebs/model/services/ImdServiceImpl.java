package com.ranbahar.imdbCelebs.model.services;

import com.ranbahar.imdbCelebs.model.Celeb;
import com.ranbahar.imdbCelebs.model.repositories.ImdbRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImdServiceImpl implements ImdbService {

    @Autowired
    private ImdbRepo repo;

    public List<Celeb> init() {
        return repo.init();
    }

    @Override
    public void delete(int id) {
        repo.delete(id);
    }

    @Override
    public Optional<Celeb> get(int id) {
        return repo.get(id);
    }

    @Override
    public List<Celeb> getAll() {
        return repo.getAll();
    }

    @Override
    public Celeb update(int id, Celeb celeb) {
        return repo.update(id, celeb);
    }

    @Override
    public int createCeleb(Celeb celeb) {
        return this.repo.createCeleb(celeb);
    }

    @Override
    public int addRan() {
        return this.repo.addRan();
    }

}
