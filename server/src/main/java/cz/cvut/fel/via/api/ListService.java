package cz.cvut.fel.via.api;

import cz.cvut.fel.via.api.model.WatchListDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListService implements ListApiDelegate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListService.class);

    @Override
    public ResponseEntity<List<WatchListDto>> listGet() {
        return ListApiDelegate.super.listGet();
    }

    @Override
    public ResponseEntity<WatchListDto> listIdAddPut(Long id, String movieId) {
        return ListApiDelegate.super.listIdAddPut(id, movieId);
    }

    @Override
    public ResponseEntity<WatchListDto> listIdDelete(Long id) {
        return ListApiDelegate.super.listIdDelete(id);
    }

    @Override
    public ResponseEntity<WatchListDto> listIdGet(Long id) {
        return ListApiDelegate.super.listIdGet(id);
    }

    @Override
    public ResponseEntity<WatchListDto> listIdRemovePut(Long id, String movieId) {
        return ListApiDelegate.super.listIdRemovePut(id, movieId);
    }

    @Override
    public ResponseEntity<WatchListDto> listPost(WatchListDto watchListDto) {
        return ListApiDelegate.super.listPost(watchListDto);
    }
}
