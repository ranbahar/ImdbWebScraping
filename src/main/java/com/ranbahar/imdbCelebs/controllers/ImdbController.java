package com.ranbahar.imdbCelebs.controllers;

import com.ranbahar.imdbCelebs.model.Celeb;
import com.ranbahar.imdbCelebs.model.services.ImdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@RestController
@RequestMapping("/api/v1/imdb")
public class ImdbController {

    @Autowired
    ImdbService service;

    @GetMapping
    @CrossOrigin
    public ResponseEntity<List<Celeb>> getAll() {
        try {
            return ResponseEntity
                    .ok()
                    .body(service.getAll());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "{id}")
    @CrossOrigin
    public ResponseEntity<Celeb> get(@PathVariable int id) {
        try {
            System.out.println("Get By Id");
            Optional<Celeb> result = service.get(id);
            if (result.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok().body(result.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping()
    @CrossOrigin
    public ResponseEntity<Integer> createCeleb(@RequestBody Celeb celeb) {
        try {
            int celebId = this.service.createCeleb(celeb);
            if (celebId != -1) {
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(celebId);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping(value = "{id}")
    @CrossOrigin
    public ResponseEntity<Void> delete(@PathVariable int id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/init")
    @CrossOrigin
    public ResponseEntity<List<Celeb>> init() {
        try {
            return ResponseEntity
                    .ok(service.init());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    @CrossOrigin
    public ResponseEntity<Celeb> update(@PathVariable int id, @RequestBody Celeb celeb) {
        try {
            Celeb result = service.update(id, celeb);
            if (result != null) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/addRan")
    @CrossOrigin
    public ResponseEntity<Integer> addRan() {
        try {
            return ResponseEntity
                    .ok(service.addRan());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
