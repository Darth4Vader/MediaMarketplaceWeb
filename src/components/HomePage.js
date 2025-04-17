import React, {Suspense, useEffect, useState, use} from 'react';
import MovieGrid from './MovieGrid';
import AppBar from './AppBar';
import { getAllMovies, getAllMovies2 } from '../http/api';

function LoadHomePage({dataPromise}) {
    //const [state, action]

    /*const products = use(getAllMovies);

    console.log("Rettturn");
    console.log(products);

    const [products, setProducts] = useState([]);
    useEffect(() => {
        //var products2 = getAllMovies();
        moviesRequest();
    }, []);

    const moviesRequest = async () => {
        const response = await getAllMovies();
        console.log("Get All");
        setProducts(response);
    }
    */

    const products = use(dataPromise);
    return (
        <div>
            <h1 style={{ textAlign: 'center', fontSize: '48px', margin: '20px 0' }}>
                Welcome
            </h1>
            <MovieGrid movies={products} />
        </div>
    );
}

export default function HomePage() {
    return (
        <Suspense fallback={<div>Loading...</div>}>
            <AppBar />
            <LoadHomePage dataPromise={getAllMovies()} />
        </Suspense>
    );
}

//export default HomePage;

/*
export default function HomePage() {
    //const [state, action]

    const products = use(getAllMovies);

    console.log("Rettturn");
    console.log(products);

    const [products, setProducts] = useState([]);
    useEffect(() => {
        //var products2 = getAllMovies();
        moviesRequest();
    }, []);

    const moviesRequest = async () => {
        const response = await getAllMovies();
        console.log("Get All");
        setProducts(response);
    }

    return (
        <div>
            <h1 style={{ textAlign: 'center', fontSize: '48px', margin: '20px 0' }}>
                Welcome
            </h1>
            <MovieGrid movies={products} />
        </div>
    );
}
*/