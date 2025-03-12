/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mymovielist;
import com.mycompany.mymovielist.model.*;
import com.mycompany.mymovielist.service.*;
import com.mycompany.mymovielist.repository.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.Year;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import com.mycompany.mymovielist.repository.MovieRepository;

/**
 *
 * @author kiran
 */
public class PlaylistServiceTest {
    @Test
    public void testAddMovieToList() {

        User dummyUser = new User("testUser", "test@example.com", "password");
        dummyUser.setId(1L); 

        Movie dummyMovie = new Movie("Dummy Movie", Year.of(2021), "Action", 8.5, "Action packed movie");
        dummyMovie.setId(10L);

        UserMovieRating dummyUserMovieRating = new UserMovieRating();
        dummyUserMovieRating.setId(100L); 
        dummyUserMovieRating.setMovieID(dummyMovie); // Link the movie

        UserPlaylist dummyPlaylist = new UserPlaylist("Favorites", dummyUser);
        
        MovieRepository movieRepository = mock(MovieRepository.class);
        UserPlaylistRepository userPlaylistRepository = mock(UserPlaylistRepository.class);
        UserPlaylistMoviesRepository userPlaylistMoviesRepository = mock(UserPlaylistMoviesRepository.class);
        UserMovieRatingRepository userMovieRepository = mock(UserMovieRatingRepository.class);

        when(movieRepository.get(dummyMovie.getId())).thenReturn(Optional.of(dummyMovie));

        // Simulate that no playlist exists with the given name and user so a new one is created
        when(userPlaylistRepository.findByUserIdAndListName(any(UserPlaylist.class)))
            .thenReturn(Optional.empty());
        
        doAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            UserPlaylist playlist = invocation.getArgument(1);
            playlist.setId(200L); // Assign an ID manually
            return null; // Simulating void return
        }).when(userPlaylistRepository).add(eq(dummyUser.getId()), any(UserPlaylist.class));
        
        PlaylistService playlistService = new PlaylistService(
            userPlaylistRepository,
            userPlaylistMoviesRepository,
            userMovieRepository,
            movieRepository
        );

        boolean result = playlistService.addMovieToList(dummyUser, dummyUserMovieRating, dummyPlaylist);

        
        assertTrue(result, "addMovieToList should return true when the movie is added successfully.");

        
        verify(movieRepository, times(1)).get(dummyMovie.getId());
        
        // Verify that the repository checked for an existing playlist.
        verify(userPlaylistRepository, times(1)).findByUserIdAndListName(any(UserPlaylist.class));
        
        // Verify that a new playlist was added since none was found.
        verify(userPlaylistRepository, times(1)).add(eq(dummyPlaylist.getId()), any(UserPlaylist.class));
        
        // Verify that the user playlist movie and rating were added.
        verify(userPlaylistMoviesRepository, times(1)).add(eq(dummyPlaylist.getId()), any(UserPlaylistMovies.class));
        
        verify(userMovieRepository, times(1)).add(anyLong(), eq(dummyUserMovieRating));
    }
}

