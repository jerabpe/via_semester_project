package cz.cvut.fel.via.api;

import cz.cvut.fel.via.api.model.MovieDetails;
import cz.cvut.fel.via.api.model.MovieDto;
import cz.cvut.fel.via.api.model.NowPlayingResult;
import cz.cvut.fel.via.db.model.Movie;
import cz.cvut.fel.via.db.repository.MovieRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService implements MovieApiDelegate {

    private final String MOVIE_API_KEY = "api_key=494cce640d8dda9faa40904f3598a19a";
    private final String NOW_PLAYING_URI = "https://api.themoviedb.org/3/movie/now_playing?" + MOVIE_API_KEY + "&language=en-US";
    private final String MOVIE_ID_URI = "https://api.themoviedb.org/3/movie/";
    private final String YT_SEARCH = "https://www.googleapis.com/youtube/v3/search?";
    private final String YT_VIDEO_BY_ID = "https://www.googleapis.com/youtube/v3/videos?";
    private final String YT_API_KEY = "key=AIzaSyBT9EqNGpm1HacozgKNMGjuZEIlUC6AeNU";

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public ResponseEntity<List<MovieDto>> movieNowGet() {
        RestTemplate restTemplate = new RestTemplate();
        //call movieDB api
        NowPlayingResult result = restTemplate.getForObject(NOW_PLAYING_URI, NowPlayingResult.class);
        List<Movie> movies = new ArrayList<>();
        result.getResults().forEach(m -> {
            //call movieDB API for details
            //System.out.println(m.getTitle()+", " + m.getId());
            Movie movie = loadMovieFromAPI(m.getId());
            movies.add(movie);
        });

        loadTrailers(movies);

        List<MovieDto> dtoList = new ArrayList<>();
        movies.forEach(m -> {
            dtoList.add(toDto(m));
        });

        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(dtoList);
    }

    private Movie loadMovieFromAPI(int id){
        RestTemplate restTemplate = new RestTemplate();
        //System.out.println("Loading movie from MovieDB_API id: " + id);
        MovieDetails details = restTemplate.getForObject(MOVIE_ID_URI+id+"?"+MOVIE_API_KEY+"&language=en-US", MovieDetails.class);
        //System.out.println(details.getTitle() + " loaded.");
        Movie movie = new Movie();
        movie.setId(details.getId());
        movie.setDescription(details.getOverview());
        movie.setTitle(details.getTitle());
        movie.setReleaseDate(details.getRelease_date());
        List<String> genres = new ArrayList<>();
        details.getGenres().forEach(g -> genres.add(g.getName()));
        movie.setGenres(genres);
        movie.setPopularity(details.getPopularity());
        return movie;
    }

    private void loadTrailers(List<Movie> movies){
        movies.forEach(m -> {
            Optional<Movie> movieOptional = movieRepository.findById(m.getId());
            if(!movieOptional.isPresent()){
                //if not in db - load from yt
                //call YT API
                //System.out.println("fetching trailer for " + m.getTitle());
                RestTemplate restTemplate = new RestTemplate();
                String ytRes = restTemplate.getForObject(YT_SEARCH+YT_API_KEY+"&q="+m.getTitle()+" trailer", String.class);
                JSONParser parser = new JSONParser();
                String videoId;
                try {
                    JSONArray items = (JSONArray) ((JSONObject) parser.parse(ytRes)).get("items");
                    videoId = ((JSONObject)((JSONObject) items.get(0)).get("id")).get("videoId").toString();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                //System.out.println("videoID: "+videoId);

                ytRes = restTemplate.getForObject(YT_VIDEO_BY_ID+YT_API_KEY+"&id="+videoId+"&part=player", String.class);
                String player;
                try{
                    JSONArray items = (JSONArray) ((JSONObject) parser.parse(ytRes)).get("items");
                    player = ((JSONObject) items.get(0)).get("player").toString();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(player);
                m.setTrailer(player);
                movieRepository.saveAndFlush(m);
            } else {
                //System.out.println("trailer already in db");
                m.setTrailer(movieOptional.get().getTrailer());
            }
        });

    }

    @Override
    public ResponseEntity<MovieDto> movieIdGet(Integer id) {
        Optional<Movie> movieOptional = movieRepository.findById(id);
        if(movieOptional.isPresent()){
            return ResponseEntity.ok(toDto(movieOptional.get()));
        } else {
            //load from API
            Movie movie = loadMovieFromAPI(id);
            loadTrailers(List.of(movie));
            return ResponseEntity.ok(toDto(movie));
        }
    }

    private MovieDto toDto(Movie m){
        MovieDto dto = new MovieDto();
        dto.setId(m.getId());
        dto.setGenres(m.getGenres());
        dto.setTitle(m.getTitle());
        dto.setDescription(m.getDescription());
        dto.setPopularity(m.getPopularity());
        dto.setTrailer(m.getTrailer());
        dto.setReleaseDate(m.getReleaseDate());
        return dto;
    }
}
