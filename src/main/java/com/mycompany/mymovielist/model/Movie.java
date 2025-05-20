package com.mycompany.mymovielist.model;

import java.time.Year; 
import javax.persistence.*;
import com.mycompany.mymovielist.converter.YearAttributeConverter;
import com.mycompany.mymovielist.api.YearDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
/**
 *
 * @author kiran
 */

@Entity
@Table(name = "movie")
public class Movie extends BaseEntity{
    
    private static final long serialVersionUID = 1L;

    @Column(name = "title", nullable = false, length = 75)
    private String title;

    @Column(name = "release_year", nullable = false)
    @Convert(converter = YearAttributeConverter.class)
    @JsonDeserialize(using = YearDeserializer.class)
    private Year releaseYear;

    @Column(name = "genre", nullable = false, length = 75)
    private String genre;

    @Column(name = "rating", nullable = false)
    private double rating;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "image_url")
    private String imageUrl;

    public Movie() {
    }
    
    @JsonCreator
    public Movie(@JsonProperty("title") String title,
        @JsonProperty("releaseYear") Year releaseYear,
        @JsonProperty("genre") String genre,
        @JsonProperty("rating") double rating,
        @JsonProperty("description") String description,
        @JsonProperty("imageUrl") String imageUrl
    ) {
        setMovieTitle(title);
        setReleaseYear(releaseYear);
        setRating(rating);
        this.genre = genre;
        this.description = description;
        this.imageUrl = imageUrl;
    }
    
    public double getRating(){
        return rating;
    }
    
    public void setRating(double rating){
        this.rating = rating;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }

    public void setMovieTitle(String title) {
        this.title = title;
    }

    public Year getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Year releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public void setDescription(String description){
        this.description = description;
    }
    
    public String getDescription(){
        return description;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
}


