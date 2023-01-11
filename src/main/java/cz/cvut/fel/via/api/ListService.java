package cz.cvut.fel.via.api;

import cz.cvut.fel.via.api.model.MovieDto;
import cz.cvut.fel.via.api.model.WatchListDto;
import cz.cvut.fel.via.db.model.Movie;
import cz.cvut.fel.via.db.model.User;
import cz.cvut.fel.via.db.model.WatchList;
import cz.cvut.fel.via.db.repository.ListRepository;
import cz.cvut.fel.via.db.repository.MovieRepository;
import cz.cvut.fel.via.security.AuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ListService implements ListApiDelegate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListService.class);

    private final MovieRepository movieRepository;
    private final ListRepository listRepository;

    @Autowired
    public ListService(MovieRepository movieRepository, ListRepository listRepository) {
        this.movieRepository = movieRepository;
        this.listRepository = listRepository;
    }

    @Override
    public ResponseEntity<List<WatchListDto>> listGet() {
        Principal p = null;
        if(RequestContextHolder.getRequestAttributes() == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            p = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getUserPrincipal();
        }
        if(p == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        AuthenticationToken token = (AuthenticationToken) p;
        if(token.getPrincipal() == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            User u = (User) token.getPrincipal();
            List<WatchList> list = listRepository.findAllByOwner(u);
            List<WatchListDto> dtoList = new ArrayList<>();
            list.forEach(l -> {
                dtoList.add(toDTO(l));
                //System.out.println(l.getTitle());
            });
            return ResponseEntity.ok(dtoList);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<WatchListDto> listIdAddPut(Long id, Integer movieId) {
        Principal p = null;
        if(RequestContextHolder.getRequestAttributes() == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            p = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getUserPrincipal();
        }
        if(p == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        AuthenticationToken token = (AuthenticationToken) p;
        if(token.getPrincipal() == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            if(id == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            User u = (User) token.getPrincipal();
            Optional<WatchList> listOptional = listRepository.findById(id);
            if(listOptional.isPresent()){
                if(!Objects.equals(listOptional.get().getOwner().getId(), u.getId())){
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                } else {
                    Optional<Movie> optionalMovie = movieRepository.findById(movieId);
                    if(optionalMovie.isPresent()){
                        WatchList list = listOptional.get();
                        if(!list.getMovies().contains(optionalMovie.get())){
                            list.getMovies().add(optionalMovie.get());
                            listRepository.saveAndFlush(list);
                        }
                        return new ResponseEntity<>(toDTO(list), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    }

    @Override
    public ResponseEntity<WatchListDto> listIdDelete(Long id) {
        Principal p = null;
        if(RequestContextHolder.getRequestAttributes() == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            p = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getUserPrincipal();
        }
        if(p == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        AuthenticationToken token = (AuthenticationToken) p;
        if(token.getPrincipal() == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            if (id == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Optional<WatchList> listOptional = listRepository.findById(id);
            if (listOptional.isPresent()) {
                User u = (User) token.getPrincipal();
                if(!Objects.equals(listOptional.get().getOwner().getId(), u.getId())){
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                } else {
                    listRepository.delete(listOptional.get());
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    }

    @Override
    public ResponseEntity<WatchListDto> listIdGet(Long id) {
        Principal p = null;
        if(RequestContextHolder.getRequestAttributes() == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            p = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getUserPrincipal();
        }
        if(p == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        AuthenticationToken token = (AuthenticationToken) p;
        if(token.getPrincipal() == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            if (id == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Optional<WatchList> listOptional = listRepository.findById(id);
            if (listOptional.isPresent()) {
                User u = (User) token.getPrincipal();
                if(!Objects.equals(listOptional.get().getOwner().getId(), u.getId())){
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
                return ResponseEntity.ok(toDTO(listOptional.get()));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    }


    @Override
    @Transactional
    public ResponseEntity<WatchListDto> listIdRemovePut(Long id, Integer movieId) {
        Principal p = null;
        if(RequestContextHolder.getRequestAttributes() == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            p = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getUserPrincipal();
        }
        if(p == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        AuthenticationToken token = (AuthenticationToken) p;
        if(token.getPrincipal() == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            if(id == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            User u = (User) token.getPrincipal();
            Optional<WatchList> listOptional = listRepository.findById(id);
            if(listOptional.isPresent()){
                if(!Objects.equals(listOptional.get().getOwner().getId(), u.getId())){
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                } else {
                    Optional<Movie> optionalMovie = movieRepository.findById(movieId);
                    if(optionalMovie.isPresent()){
                        WatchList list = listOptional.get();
                        list.getMovies().remove(optionalMovie.get());
                        listRepository.saveAndFlush(list);
                        return new ResponseEntity<>(toDTO(list), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    }

    @Override
    public ResponseEntity<WatchListDto> listPost(WatchListDto watchListDto) {
        Principal p = null;
        if(RequestContextHolder.getRequestAttributes() == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            p = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getUserPrincipal();
        }
        System.out.println(p);
        if(p == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        AuthenticationToken token = (AuthenticationToken) p;
        if(token.getPrincipal() == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            User u = (User) token.getPrincipal();
            WatchList watchList = new WatchList();
            watchList.setTitle(watchListDto.getTitle());
            watchList.setOwner(u);
            List<Movie> movies = new ArrayList<>();
            if(watchListDto.getMovies() != null) {
                watchListDto.getMovies().forEach(m -> {
                    Optional<Movie> optionalMovie = movieRepository.findById(m.getId());
                    if(optionalMovie.isPresent()){
                        movies.add(optionalMovie.get());
                    }
                });
            }
            watchList.setMovies(movies);
            listRepository.saveAndFlush(watchList);
            return new ResponseEntity<WatchListDto>(toDTO(watchList), HttpStatus.CREATED);
        }
    }

    private WatchListDto toDTO(WatchList watchList){
        WatchListDto dto = new WatchListDto();
        dto.setId(watchList.getId());
        dto.setTitle(watchList.getTitle());
        List<MovieDto> movies = new ArrayList<>();
        watchList.getMovies().forEach(m -> {
            MovieDto movieDto = new MovieDto();
            movieDto.setId(m.getId());
            movieDto.setTitle(m.getTitle());
            movieDto.setReleaseDate(m.getReleaseDate());
            movieDto.setDescription(m.getDescription());
            movieDto.setTrailer(m.getTrailer());
            movieDto.setGenres(m.getGenres());
            movieDto.setPopularity(m.getPopularity());
            movies.add(movieDto);
        });
        dto.setMovies(movies);
        return dto;
    }
}


