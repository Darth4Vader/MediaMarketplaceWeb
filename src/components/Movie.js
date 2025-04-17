import React, { useState } from "react";
import { useNavigate } from 'react-router-dom';

/**
 * MovieCell displays a movie's poster and title inside a vertically aligned box.
 * It handles click events to navigate to the detailed movie page.
 *
 * Props:
 * - movie: { name: string, posterPath: string }
 * - onClick: function to handle cell click (e.g., navigate to movie page)
 */

function MovieCell({ movie }) {
    const navigate = useNavigate();

    const handleClick = () => {
        if (movie) {
            navigate(`/movie/${movie.id}`);
        }
    };

    return (
        <div
            className="movie-cell"
            style={{
                border: '1px solid black',
                cursor: movie ? 'pointer' : 'default',
                position: 'relative'
            }}
        >
            <img
                src={movie?.posterPath || ''}
                alt={movie?.name || 'Movie Poster'}
                style={{
                    height: '100%', // You could also make this responsive to parent size with CSS
                    width: '100%',
                }}
                onClick={handleClick}
            />
            <div
                 style={{
                     display: 'flex',
                     alignItems: 'center',
                     width: '100%',
                     height: '10%',
                     cursor: 'default',
                     position: 'absolute',
                     bottom: '0px',
                     backgroundColor: 'rgba(255, 255, 255, 0.8)', // Semi-transparent white background
                     containerType: 'inline-size',
                 }}
            >
                <p style={{
                    textAlign: 'center',
                    wordWrap: 'break-word',
                    overflow: 'hidden',
                    whiteSpace: 'nowrap',
                    width: '100%',
                    textOverflow: 'ellipsis',
                    cursor: 'default',
                    fontSize: '10cqw',
                    color: 'black',
                }}>
                    {movie?.name || ''}
                </p>
            </div>
        </div>
    );
}



/*
function MovieCell({ movie, onClick }) {
    const handleClick = () => {
        if (movie && onClick) {
            onClick(movie);
        }
    };

    return (
        <div
            className="movie-cell"
            style={{
                //display: 'grid',
                //alignItems: 'center',
                border: '1px solid black',
                cursor: movie ? 'pointer' : 'default',
                position: 'relative'
            }}
        >
            <img
                src={movie?.posterPath || ''}
                alt={movie?.name || 'Movie Poster'}
                style={{
                    height: '100%', // You could also make this responsive to parent size with CSS
                    width: '100%',
                    alignItems: 'top',
                    //objectFit: 'contain',
                }}
                onClick={handleClick}
            />
            <div className="container"
                style={{
                        //textAlign: 'center',
                        //wordWrap: 'break-word',
                        //overflow: 'hidden',
                        //whiteSpace: 'nowrap',
                        display: 'flex',
                        alignItems: 'center',
                        width: '100%',
                        height: '10%',
                        //height: '50px',
                        //textOverflow: 'ellipsis',
                        cursor: 'default',
                        position: 'absolute',
                        //top: '70%',
                        //top: '10px',
                        //bottom: '10px',
                        //left: '0px',
                        bottom: '0px',
                        //padding: '4px 8px',
                        //paddingLeft: '20px',
                        //paddingRight: '20px',
                        color: 'blue',
                        backgroundColor: 'rgba(255, 255, 255, 0.8)', // Semi-transparent white background

                        containerType: 'inline-size',



                        //fontSize: '4vw'
                        "@container (width > 10px)": {
                            backgroundColor: 'rgba(255, 0, 0)',
                            ".textt p": {
                                fontSize: '12em'
                            }
                        }


                        //zIndex: '1',
                    }}
            >
            <p className="textt" style={{
                textAlign: 'center',
                wordWrap: 'break-word',
                overflow: 'hidden',
                whiteSpace: 'nowrap',
                width: '100%',
                //height: '50px',
                textOverflow: 'ellipsis',
                cursor: 'default',



                //containerName: 'sidebar',


                //fontSize: '1em',

                fontSize: '10cqw'

                //fontSize: '1em'//'calc(2.5cm / 10)'
                //alignSelf: 'center',
                //position: 'relative',
                //verticalAlign: 'middle'





                position: 'absolute',
                top: '78%',
                //bottom: '10px',
                left: '0px',
                //padding: '4px 8px',
                //paddingLeft: '20px',
                //paddingRight: '20px',
                color: 'blue',
                //zIndex: '1',
            }}>
                {movie?.name || ''}
            </p>
            </div>
        </div>
    );
}
*/


export default MovieCell;