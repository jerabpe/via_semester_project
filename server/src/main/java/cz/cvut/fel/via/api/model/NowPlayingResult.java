package cz.cvut.fel.via.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class NowPlayingResult {
    private int page;
    private List<MovieResult> results;
    private Dates dates;
    private int total_pages;
    private int total_results;
}
