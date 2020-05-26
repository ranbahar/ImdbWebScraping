package com.ranbahar.imdbCelebs.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ranbahar.imdbCelebs.model.services.IDGeneratorService;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.net.URL;
import java.time.LocalDate;

@Data
public class Celeb implements Serializable {


    @JsonDeserialize(using = CustomSerializer.class)
    private int id;
    private String name;
    private String title;
    private String desc;
    private Gender gender;
    private URL image;
    private LocalDate birthDay;

    public Celeb(String name, String title, String desc, Gender gender, @Nullable URL image, @Nullable LocalDate birthDay) {
        id = IDGeneratorService.generateID();
        this.name = name;
        this.title = title;
        this.desc = desc;
        this.gender = gender;
        this.image = image;
        this.birthDay = birthDay;
    }

    public Celeb() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Celeb) {
            Celeb celeb = (Celeb) obj;
            return ((Integer) celeb.id).equals(id);
        }
        return false;
    }

    public void setId() {
        this.id = IDGeneratorService.generateID();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + name.hashCode();
        result = 31 * result + id;
        result = 31 * result + title.hashCode();
        result = 31 * result + desc.hashCode();
        result = 31 * result + gender.hashCode();
        result = 31 * result + image.hashCode();
        result = 31 * result + birthDay.hashCode();

        return result;
    }

}

