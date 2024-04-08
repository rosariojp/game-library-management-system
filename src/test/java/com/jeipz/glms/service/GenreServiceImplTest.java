package com.jeipz.glms.service;

import com.jeipz.glms.exception.GenreAlreadyExistsException;
import com.jeipz.glms.exception.GenreNotFoundException;
import com.jeipz.glms.mapper.GenreMapper;
import com.jeipz.glms.model.Game;
import com.jeipz.glms.model.Genre;
import com.jeipz.glms.model.input.GenreInput;
import com.jeipz.glms.repository.GameRepository;
import com.jeipz.glms.repository.GenreRepository;
import com.jeipz.glms.validation.GenreValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {

    public static final String GENRE_NAME = "Arcade";

    public static final String GENRE_NAME_UPDATE = "Fighting";

    public static final String GENRE_NAME_FIELD = "name";

    @InjectMocks
    private GenreServiceImpl genreService;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GenreMapper genreMapper;

    @Mock
    private GenreValidator genreValidator;

    private List<Genre> createGenreList(int elemCount) {
        return IntStream.range(0, elemCount)
                .mapToObj(i -> Genre.builder()
                        .id(UUID.randomUUID())
                        .name("Genre " + i)
                        .build())
                .collect(Collectors.toList());
    }

    @Test
    public void getAllGenres_Successful() {
        int page = 0;
        int size = 5;
        Sort sort = Sort.by(GENRE_NAME_FIELD).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        List<Genre> genres = createGenreList(size);
        Page<Genre> genrePages = new PageImpl<>(genres);

        when(genreRepository.findAll(pageable)).thenReturn(genrePages);

        Page<Genre> fetchedGenrePages = genreService.getAllGenres(page, size);

        assertAll("Should return pages of genre in ascending order",
                () -> assertEquals(genrePages, fetchedGenrePages),
                () -> assertEquals(page, fetchedGenrePages.getNumber()),
                () -> assertEquals(size, fetchedGenrePages.getSize()));

        verify(genreRepository, times(1)).findAll(pageable);
    }

    @Test
    public void getGenreById_Successful() {
        UUID id = UUID.randomUUID();
        Genre genre = Genre.builder()
                .id(id)
                .name(GENRE_NAME)
                .build();

        when(genreRepository.findById(id)).thenReturn(Optional.of(genre));

        Genre fetchGenre = genreService.getGenreById(id);

        assertAll("genre and fetchGenre should be equal",
                () -> assertEquals(genre.getId(), fetchGenre.getId()),
                () -> assertEquals(genre.getName(), fetchGenre.getName()));

        verify(genreRepository, times(1)).findById(id);
    }

    @Test
    public void getGenreById_NotFound() {
        UUID id = UUID.randomUUID();

        when(genreRepository.findById(id)).thenThrow(GenreNotFoundException.class);

        assertAll("Should return GenreNotFoundException",
                () -> assertThrows(GenreNotFoundException.class, () -> genreService.getGenreById(id)));

        verify(genreRepository, times(1)).findById(id);
    }

    @Test
    public void addGenre_Successful() {
        GenreInput genreInput = new GenreInput(GENRE_NAME);
        Genre genre = Genre.builder()
                .name(genreInput.name())
                .build();
        Genre savedGenre = Genre.builder()
                .id(UUID.randomUUID())
                .name(genreInput.name())
                .build();

        doNothing().when(genreValidator).validateName(genreInput.name());
        when(genreMapper.apply(genreInput)).thenReturn(genre);
        when(genreRepository.save(genre)).thenReturn(savedGenre);

        Genre addedGenre = genreService.addGenre(genreInput);

        assertAll("Should return genre",
                () -> assertEquals(addedGenre.getId(), savedGenre.getId()),
                () -> assertEquals(addedGenre.getName(), savedGenre.getName()));

        verify(genreValidator, times(1)).validateName(genreInput.name());
        verify(genreMapper, times(1)).apply(genreInput);
        verify(genreRepository, times(1)).save(genre);
    }

    @Test
    public void addGenre_AlreadyExists() {
        GenreInput genreInput = new GenreInput(GENRE_NAME);

        doThrow(GenreAlreadyExistsException.class).when(genreValidator).validateName(genreInput.name());

        assertAll("Should return GenreAlreadyExistsException",
                () -> assertThrows(GenreAlreadyExistsException.class, () -> genreService.addGenre(genreInput)));

        verify(genreValidator, times(1)).validateName(genreInput.name());
    }

    @Test
    public void updateGenre_nameUpdated_Successful() {
        UUID id = UUID.randomUUID();
        GenreInput genreInput = new GenreInput(GENRE_NAME_UPDATE);
        Genre genre = Genre.builder()
                .id(id)
                .name(GENRE_NAME)
                .build();
        Genre updatedGenre = Genre.builder()
                .id(id)
                .name(genreInput.name())
                .build();

        when(genreRepository.findById(id)).thenReturn(Optional.ofNullable(genre));
        doNothing().when(genreValidator).validateName(genreInput.name());
        when(genreRepository.save(any(Genre.class))).thenReturn(updatedGenre);

        Genre resultGenre = genreService.updateGenre(id, genreInput);

        assertAll("updatedGenre should be equal to resultGenre",
                () -> assertEquals(updatedGenre.getId(), resultGenre.getId()),
                () -> assertEquals(updatedGenre.getName(), resultGenre.getName()));

        verify(genreRepository, times(1)).findById(id);
        verify(genreValidator, times(1)).validateName(genreInput.name());
        verify(genreRepository, times(1)).save(any(Genre.class));
    }

    @Test
    public void updateGenre_noChanges_Successful() {
        UUID id = UUID.randomUUID();
        GenreInput genreInput = new GenreInput("Arcade");
        Genre genre = Genre.builder()
                .id(id)
                .name(genreInput.name())
                .build();

        when(genreRepository.findById(id)).thenReturn(Optional.of(genre));
        when(genreRepository.save(any(Genre.class))).thenReturn(genre);

        Genre resultGenre = genreService.updateGenre(id, genreInput);

        assertAll("updatedGenre should be equal to resultGenre",
                () -> assertEquals(genre.getId(), resultGenre.getId()),
                () -> assertEquals(genre.getName(), resultGenre.getName()));

        verify(genreRepository, times(1)).findById(id);
        verify(genreRepository, times(1)).save(genre);
    }

    @Test
    public void updateGenre_NotFound() {
        UUID id = UUID.randomUUID();
        GenreInput genreInput = new GenreInput(GENRE_NAME_UPDATE);

        when(genreRepository.findById(id)).thenThrow(GenreNotFoundException.class);

        assertAll("Should throw GenreNotFoundException",
                () -> assertThrows(GenreNotFoundException.class, () -> genreService.updateGenre(id, genreInput)));

        verify(genreRepository, Mockito.times(1)).findById(id);
    }

    @Test
    public void updateGenre_AlreadyExists() {
        UUID id = UUID.randomUUID();
        GenreInput genreInput = new GenreInput(GENRE_NAME_UPDATE);
        Genre genre = Genre.builder()
                .id(id)
                .name(GENRE_NAME)
                .build();

        when(genreRepository.findById(id)).thenReturn(Optional.ofNullable(genre));
        doThrow(GenreAlreadyExistsException.class).when(genreValidator).validateName(genreInput.name());

        assertAll("Should throw GenreAlreadyExistsException",
                () -> assertThrows(GenreAlreadyExistsException.class, () -> genreService.updateGenre(id, genreInput)));

        verify(genreRepository, times(1)).findById(id);
        verify(genreValidator, times(1)).validateName(genreInput.name());
    }

    @Test
    public void deleteGenre_Successful() {
        UUID id = UUID.randomUUID();
        int gameCount = 3;
        Genre genre = Genre.builder()
                .id(id)
                .name(GENRE_NAME)
                .games(createGameSet(gameCount))
                .build();

        when(genreRepository.findById(id)).thenReturn(Optional.of(genre));
        doNothing().when(genreRepository).delete(genre);

        String message = genreService.deleteGenre(id);

        assertAll("Should delete genre",
                () -> assertEquals("Genre deleted with id -> " + id, message));

        verify(genreRepository, times(1)).findById(id);
        verify(gameRepository, times(gameCount)).save(any(Game.class));
        verify(genreRepository, times(1)).delete(genre);
    }

    private Set<Game> createGameSet(int elemCount) {
        return IntStream.range(0, elemCount)
                .mapToObj(i -> Game.builder()
                        .id(UUID.randomUUID())
                        .title("Game " + i)
                        .price(new BigDecimal("59.99"))
                        .genres(new HashSet<>())
                        .build())
                .collect(Collectors.toSet());
    }

    @Test
    public void deleteGenre_NotFound() {
        UUID id = UUID.randomUUID();

        when(genreRepository.findById(id)).thenThrow(GenreNotFoundException.class);

        assertAll("Should return GenreNotFoundException",
                () -> assertThrows(GenreNotFoundException.class, () -> genreService.deleteGenre(id)));

        verify(genreRepository, times(1)).findById(id);
    }

}