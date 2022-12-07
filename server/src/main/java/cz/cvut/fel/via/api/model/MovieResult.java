package cz.cvut.fel.via.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class MovieResult {
    private String poster_path;
    private boolean adult;
    private String overview;
    private String release_date;
    List<Integer> genre_ids;
    private int id;
    private String original_title;
    private String original_language;
    private String title;
    private String backdrop_path;
    private float popularity;
    private int vote_count;
    private boolean video;
    private float vote_average;
}
