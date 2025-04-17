import React from 'react';
import Movie from './Movie';
import { ImageList, ImageListItem, ImageListItemBar, useMediaQuery } from "@mui/material";

export default function MovieGrid({ movies }) {
    const handleMovieClick = (movie) => {
        alert(`Navigating to movie: ${movie.name}`);
        // You can replace this with navigation logic
    };
    console.log("What")
    console.log(movies)
    return (
        <div
            style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))',
                gap: '16px',
                padding: '20px',
            }}
        >
            {movies.map((item, index) => {
                const movie = item.movie || item; // handle both ProductDto and MovieReference
                console.log(movie)
                if (!movie?.name || !movie?.posterPath) return null;
                return <Movie key={index} movie={movie} onClick={handleMovieClick} />;
            })}
        </div>
    );
}

/*
export default function MovieGrid({ movies }) {
    const handleMovieClick = (movie) => {
        alert(`Navigating to movie: ${movie.name}`);
        // You can replace this with navigation logic
    };
    console.log("What")
    console.log(movies)
    return (
        <div
            style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))',
                gap: '16px',
                padding: '20px',
            }}
        >
            {movies.map((item, index) => {
                const movie = item.movie || item; // handle both ProductDto and MovieReference
                console.log(movie)
                if (!movie?.name || !movie?.posterPath) return null;
                return <Movie key={index} movie={movie} onClick={handleMovieClick} />;
            })}
        </div>
    );
}*/