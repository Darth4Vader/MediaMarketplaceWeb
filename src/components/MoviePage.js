import React, {useState, useEffect, use, Suspense} from 'react';
import {get} from '../http/requests';
import MovieGrid from "./MovieGrid";
import { useParams } from 'react-router-dom';
import AppBar from "./AppBar";
import {getMovie, getActorsMovie, getDirectorsMovie, getReviewsOfMovie} from "../http/api";
import './MoviePage.css';

export default function LoadMoviePage() {
    const { id } = useParams();
    console.log(id);
    return (
        <Suspense fallback={<div>Loading...</div>}>
            <AppBar />
            <MoviePage moviePromise={getMovie(id)} />
        </Suspense>
    );
}

const MoviePage = ({ moviePromise }) => {
    const movie = use(moviePromise);
    return (
        <div className="movie-page">
            <div className="movie-header">
                <img src={movie?.posterPath} alt={`${movie?.name} Poster`} className="movie-poster" />
                <div className="movie-details">
                    <h1>{movie?.name}</h1>
                    <p><strong>Year:</strong> {movie?.year}</p>
                    <p><strong>Runtime:</strong> {movie?.runtime} minutes</p>
                    <p><strong>Rating:</strong> {movie?.rating}/100</p>
                </div>
            </div>
            <div className="movie-synopsis">
                <h2>Synopsis</h2>
                <p>{movie?.synopsis || 'To Be Determined'}</p>
            </div>
            <div className="movie-cast">
                <h2>Directors</h2>
                <Suspense fallback={<div>Loading Directors...</div>}>
                    <DirectorList directorsPromise={getDirectorsMovie(movie.id)} />
                </Suspense>
                <h2>Actors</h2>
                <Suspense fallback={<div>Loading Actors...</div>}>
                    <ActorList actorsPromise={getActorsMovie(movie.id)} />
                </Suspense>
            </div>
            <div className="movie-reviews">
                <h2>Reviews</h2>
                <Suspense fallback={<div>Loading Reviews...</div>}>
                    <ReviewsList reviewsPromise={getReviewsOfMovie(movie.id)} />
                </Suspense>
            </div>
        </div>
    );
};

function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString(undefined, {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
    });
}

const ReviewsList = ({ reviewsPromise }) => {
    const reviews = use(reviewsPromise);
    console.log(reviews);
    return reviews?.content?.length > 0 ? (
                reviews?.content?.map((review) => (
                    <div key={review.id} className="review">
                        <h3>{review.title}</h3>
                        <p>{review.content}</p>
                        <p><strong>Rating:</strong> {review.rating}/100</p>
                    </div>
                ))
            ) : (
                <p>No reviews yet.</p>
            );
};

const DirectorList = ({ directorsPromise }) => {
    console.log(directorsPromise);
    const directors = use(directorsPromise);
    return (
        <ol>
            {directors?.map((director) => {
                return <DirectorCell castMember={director}/>;
            })}
        </ol>
    );
};

const ActorList = ({ actorsPromise }) => {
    console.log(actorsPromise);
    const actors = use(actorsPromise);
    return (
        <ol>
            {actors?.map((actor) => {
                return <ActorCell castMember={actor}/>;
            })}
        </ol>
    );
};

const ActorCell = ({ castMember }) => {
    return (
        <li key={castMember.id} className="cast-cell">
            <img src={castMember?.person?.imagePath} alt={`${castMember?.name} Poster`} />
            <div>
                <p>{castMember?.person?.name}</p>
                <p>{castMember?.roleName}</p>
            </div>
        </li>
    );
};

const DirectorCell = ({ castMember }) => {
    return (
        <li key={castMember.id} className="cast-cell">
            <img src={castMember?.person?.imagePath} alt={`${castMember?.name} Poster`} />
            <div>
                <p>{castMember?.person?.name}</p>
            </div>
        </li>
    );
};

//export default MoviePage;