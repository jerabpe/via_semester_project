package cz.cvut.fel.via.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class MovieDetails {
    private boolean adult;
    private String backdrop_path;
    private Object belongs_to_collection;
    private int budget;
    List<Genre> genres;
    private String homepage;
    private int id;
    private String imdb_id;
    private String original_language;
    private String original_title;
    private String overview;
    private float popularity;
    private String poster_path;
    List<ProdCompany> production_companies;
    List <ProdCountry> production_countries;
    private String release_date;
    private int revenue;
    private int runtime;
    List<SpokenLanguage> spoken_languages;
    private String status;
    private String tagline;
    private String title;
    private boolean video;
    private float vote_average;
    private int vote_count;
}
