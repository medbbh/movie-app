package com.master.movieapp.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.protocol.HttpCoreContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MovieService {
    // API Key for OMDB (you'll need to get your own free API key)
    private static final String API_KEY = "f537bd65";

    public static class Movie {
        private String title;
        private String year;
        private String rating;
        private String poster;
        private String genre;
        private String plot;

        public Movie(String title, String year, String rating, String poster, String genre, String plot) {
            this.title = title;
            this.year = year;
            this.rating = rating;
            this.poster = poster;
            this.genre = genre;
            this.plot = plot;
        }

        // Getters
        public String getTitle() { return title; }
        public String getYear() { return year; }
        public String getRating() { return rating; }
        public String getPoster() { return poster; }
        public String getGenre() { return genre; }
        public String getPlot() { return plot; }
    }

    public List<Movie> getPopularMovies() {
        // Expanded list of search terms covering various genres and themes
        String[] searchTerms = {
            "marvel", "star wars", "action", "comedy", "drama", 
            "sci-fi", "animation", "thriller", "adventure", "fantasy",
            "horror", "romance", "crime", "documentary", "musical"
        };

        // Use a set to avoid duplicate movies
        Set<Movie> uniqueMovies = new HashSet<>();

        // Create an executor service for concurrent API calls
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        // Use CompletableFuture to make concurrent API calls
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String term : searchTerms) {
            for (int page = 1; page <= 3; page++) { // Fetch multiple pages for each term
                final int currentPage = page;
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                        String url = String.format(
                            "http://www.omdbapi.com/?apikey=%s&s=%s&type=movie&page=%d",
                            API_KEY, term, currentPage
                        );

                        HttpGet request = new HttpGet(url);

                        try (ClassicHttpResponse response = httpClient.execute(request, HttpCoreContext.create())) {
                            BufferedReader reader = new BufferedReader(
                                new InputStreamReader(response.getEntity().getContent())
                            );
                            JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();

                            if (jsonResponse.has("Search")) {
                                JsonArray results = jsonResponse.getAsJsonArray("Search");

                                for (int i = 0; i < results.size(); i++) {
                                    JsonObject movieJson = results.get(i).getAsJsonObject();
                                    String imdbID = movieJson.get("imdbID").getAsString();
                                    
                                    // Fetch detailed movie information
                                    Movie detailedMovie = getMovieDetails(imdbID);
                                    if (detailedMovie != null) {
                                        uniqueMovies.add(detailedMovie);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, executorService);

                futures.add(future);
            }
        }

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Shutdown the executor service
        executorService.shutdown();

        // Convert to list and limit to 50 movies
        return uniqueMovies.stream()
            .limit(50)
            .collect(Collectors.toList());
    }

    private Movie getMovieDetails(String imdbID) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = String.format(
                "http://www.omdbapi.com/?apikey=%s&i=%s&plot=short",
                API_KEY, imdbID
            );

            HttpGet request = new HttpGet(url);

            try (ClassicHttpResponse response = httpClient.execute(request, HttpCoreContext.create())) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent())
                );
                JsonObject movieDetails = JsonParser.parseReader(reader).getAsJsonObject();

                return new Movie(
                    movieDetails.get("Title").getAsString(),
                    movieDetails.get("Year").getAsString(),
                    movieDetails.has("imdbRating") ? movieDetails.get("imdbRating").getAsString() : "N/A",
                    movieDetails.has("Poster") ? movieDetails.get("Poster").getAsString() : "N/A",
                    movieDetails.has("Genre") ? movieDetails.get("Genre").getAsString() : "N/A",
                    movieDetails.has("Plot") ? movieDetails.get("Plot").getAsString() : "N/A"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}