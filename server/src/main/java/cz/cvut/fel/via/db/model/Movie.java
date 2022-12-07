package cz.cvut.fel.via.db.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter @Setter
public class Movie {

    @Id
    private Integer id; //theMovieDb

    private String title;

    private String description;

    private String releaseDate;

    private String trailer;

    @ElementCollection
    private List<String> genres;

    private float popularity;
}
