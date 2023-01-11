package cz.cvut.fel.via.db.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter @Setter
public class WatchList {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    @ManyToMany
    private List<Movie> movies;

    @OneToOne
    private User owner;
}
