/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist;
import com.mycompany.mymovielist.model.*;
import com.mycompany.mymovielist.service.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.Year;
import java.util.*;import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import com.mycompany.mymovielist.repository.MovieRepository;

/**
 *
 * @author kiran
 */
public class MovieServiceTest {
    @Test
    public void getAvailableMoviesTest(){
        
        List<Movie> dummyMovies = Arrays.asList(
            new Movie("Inception", Year.of(2010), "Thriller", 9.2, "Corporate espionage through dream hacking."),
            new Movie("Gladiator", Year.of(2000), "History", 9.5, "A gladiator seeks vengeance")
        );
        
        MovieRepository movieRepository = mock(MovieRepository.class);
        
        when(movieRepository.getAll()).thenReturn(dummyMovies);
        
        MovieService movieService = new MovieService(movieRepository);
        List<Movie> result = movieService.getAvailableMovies();
        assertEquals(result, dummyMovies, "The list of available movies should match the dummy list.");
        
    }
    
    @Test
    public void testGetMovieByIdFound(){
        
        Movie dummyMovie = new Movie("Movie One", Year.of(2021), "Drama", 8.5, "A great movie.");
            try {
                Field idField = BaseEntity.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(dummyMovie, 1L);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            
        MovieRepository movieRepository = mock(MovieRepository.class);
    
        when(movieRepository.get(dummyMovie.getId())).thenReturn(Optional.of(dummyMovie));
        
        MovieService movieService = new MovieService(movieRepository);
        
        Optional<Movie> result = movieService.getMovieById(dummyMovie.getId());
        
        assertTrue(result.isPresent());
        assertEquals(dummyMovie, result.get());
            
    }
    
    @Test
    public void testGetMovieByIdNotFound() {
        MovieRepository movieRepository = mock(MovieRepository.class);
        
        when(movieRepository.get(99L)).thenReturn(Optional.empty());
        
        MovieService movieService = new MovieService(movieRepository);
        
        Optional<Movie> result = movieService.getMovieById(99L);
        assertFalse(result.isPresent(), "No movie should be found for id 99.");
    }
}
