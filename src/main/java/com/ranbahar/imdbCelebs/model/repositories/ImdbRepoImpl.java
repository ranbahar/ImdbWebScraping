package com.ranbahar.imdbCelebs.model.repositories;

import com.ranbahar.imdbCelebs.model.Celeb;
import com.ranbahar.imdbCelebs.model.Gender;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;


@Repository
public class ImdbRepoImpl implements ImdbRepo {

    private List<Celeb> celebList = new ArrayList<>();
    private static final String[] months = new DateFormatSymbols().getMonths();
    public static final String name = ".lister-item-header > a";
    public static final String title = "p.text-muted";
    public static final String dsecription = ".lister-item-content > p:nth-of-type(2)";

    @PostConstruct
    public List<Celeb> init() {
        try {
            Document doc = Jsoup.connect("https://www.imdb.com/list/ls052283250/").get();
            System.out.println(doc.title());

            Elements elements = doc.getElementsByClass("lister-item mode-detail");
            //doc.select("div.mode-detail.lister-item:nth-of-type(1)");

//            doc.select("div.mode-detail.lister-item:nth-of-type(1)").select("img").first().absUrl("src")
            celebList = elements.stream().map(e -> {
                        try {
                            return new Celeb(
                                    e.select(name).text(),
                                    getFirstWord(e.select(title).text()),
                                    e.select(dsecription).text(),
                                    getGender(getFirstWord(e.select(title).text()), e.select(dsecription).text()),
                                    new URL(e.select("img").first().absUrl("src")),
                                    getBDay(e.select(dsecription).text()));
                        } catch (MalformedURLException malformedURLException) {
                            malformedURLException.printStackTrace();
                        }
                        return null;
                    }
            ).collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return this.celebList;
    }

    @Override
    public void delete(int id) {
        this.celebList.removeIf(c -> c.getId() == id);
    }

    @Override
    public List<Celeb> getAll() {
        return this.celebList;
    }

    @Override
    public Celeb update(int id, Celeb celeb) {
        int index = this.celebList.indexOf(celeb);
        System.out.println(index);
        if (index != -1) {
            this.celebList.set(index, celeb);
        } else {
            celeb = null;
        }

        return celeb;
    }

    @Override
    public int createCeleb(Celeb celeb) {
        int celebId = -1;
        if (celeb != null
                && !isNullOrEmpty(celeb.getDesc())
                && !isNullOrEmpty(celeb.getName())
                && !isNullOrEmpty(celeb.getTitle())) {
            if (celeb.getId() == 0 || this.celebList.stream().anyMatch(celeb1 -> celeb1.getId() == celeb.getId())) {
                celeb.setId();
            }
            this.celebList.add(celeb);
            celebId = celeb.getId();
        }

        return celebId;
    }

    @Override
    public int addRan() {
        Celeb ran = this.celebList.stream().filter(c -> c.getName() == "Ran Bahar").findAny().orElse(null);
        if (ran == null) {
            try {
                ran = new Celeb("Ran Bahar", "Programmer", "Ran Bahar - Programmer since 2015", Gender.Male,
                        new URL("https://media-exp1.licdn.com/dms/image/C4E03AQFRmK4QwUpbGw/profile-displayphoto-shrink_200_200/0?e=1596067200&v=beta&t=GwVuIYgd112yZYUrl8ZmlNwjdBcdQaxfkIId5pr684c"),
                        LocalDate.of(1987, 5, 20));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return -1;
            }
            this.celebList.add(ran);
        }
        return ran != null ? ran.getId() : -1;
    }

    @Override
    public Optional<Celeb> get(int id) {
        return this.celebList.stream().filter(c -> c.getId() == id).findAny();
    }

    private String getFirstWord(String str) {
        int i = str.indexOf(" ");
        return str.substring(0, i);
    }

    private Gender getGender(String title, String description) {

        Gender result = Gender.Female;

        switch (title.toLowerCase()) {
            case "producer": {

                Pattern pattern = Pattern.compile("\\bher\\b", Pattern.CASE_INSENSITIVE);
                result = pattern.matcher(description).find() ? Gender.Female : Gender.Male;

                break;
            }
            default: {
                result = title.contains("ss") ? Gender.Female : Gender.Male;
            }
        }
        return result;
    }

    private LocalDate getBDay(String desc) {
        int day = -1;
        String month = "March";
        int year = -1;

        String[] words = desc.split("\\s+");

        for (int i = 0; i < words.length; i++) {

            for (int j = 0; j < months.length - 1; j++) {

                if (words[i].contains(months[j])) {
                    if (words[i].matches("^[a-zA-Z]*$")) {
                        month = words[i];
                    } else {
                        //we don't have to use is we can also set month to month[j] without any change
                        month = getOnlyAlphabet(words[i]);
                    }
                    if (words.length >= i + 2) {
                        day = this.getNumberOnly(words[i + 1]);
                        year = this.getNumberOnly(words[i + 2]);

                        if (this.isValidDate(day, month, year)) {
                            return LocalDate.of(year, Month.valueOf(month.toUpperCase()), day);
                        } else {
                            if (i > 0 && words.length > i + 1) {
                                day = this.getNumberOnly(words[i - 1]);
                                year = this.getNumberOnly(words[i + 1]);

                                if (this.isValidDate(day, month, year)) {
                                    return LocalDate.of(year, Month.valueOf(month.toUpperCase()), day);
                                }
                            }
                        }
                    }

                    break;
                }
            }
        }

        return null;

    }

    private String getOnlyAlphabet(String word) {
        return word.replaceAll("[^a-zA-Z]", "");
    }

    private boolean isValidDate(int day, String month, int year) {
        boolean result = false;
        if (day > 0 && year > 0) {
            result = true;
        }

        return result;
    }

    private int getNumberOnly(String str) {

        int result = -1;

        while (str.length() > 0 && !StringUtil.isNumeric(str.substring(str.length() - 1, str.length()))) {
            str = str.substring(0, str.length() - 1);
        }
        try {
            if (str.length() >= 1)
                result = parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            result = -1;
        }


        return result;
    }

    private boolean isNullOrEmpty(String str) {
        if (str != null && !str.trim().isEmpty())
            return false;
        return true;
    }
}



/*
            desc = newsHeadlines.select(".lister-item-content > p:nth-of-type(2)").text();
            name = newsHeadlines.select(".lister-item-header > a").text();
            title = newsHeadlines.select("p.text-muted").text();

            for (Element headline : newsHeadlines) {
                System.out.println("%s\n\t%s", headline.attr("title"), headline.absUrl("href"));

                headline.outerHtml();
            }*/


