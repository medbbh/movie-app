package com.master.movieapp.controller;

import com.master.movieapp.service.MovieService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/movies")
public class MovieListServlet extends HttpServlet {
    private MovieService movieService;

    @Override
    public void init() throws ServletException {
        // Initialize movie service
        movieService = new MovieService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Fetch movies and set as request attribute
        request.setAttribute("movies", movieService.getPopularMovies());
        request.getRequestDispatcher("/movies.jsp").forward(request, response);
    }
}