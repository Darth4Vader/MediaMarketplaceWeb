import * as requests from './requests';

export async function getAllMovies(){
    const response = await requests.getData('/api/main/movies/');
    console.log("Response");
    console.log(response);
    if (!response.ok) {
        return {
            status: response?.status,
            isError: true,
            error: `Request failed with status ${response.status}: ${response.statusText}`,
        };
    }

    // Parse the response data
    const data = await response.json();

    // Return the successful response
    return data;
}

export async function getMovie(id){
    const response = await requests.getData(`/api/main/movies/${id}`);
    console.log("Response");
    console.log(response);
    if (!response.ok) {
        return {
            status: response?.status,
            isError: true,
            error: `Request failed with status ${response.status}: ${response.statusText}`,
        };
    }

    // Parse the response data
    const data = await response.json();
    // Return the successful response
    return data;
}

export async function getActorsMovie(id){
    const response = await requests.getData(`/api/main/actors?movieId=${id}`);
    console.log("Response");
    console.log(response);
    if (!response.ok) {
        return {
            status: response?.status,
            isError: true,
            error: `Request failed with status ${response.status}: ${response.statusText}`,
        };
    }

    // Parse the response data
    const data = await response.json();
    // Return the successful response
    return data;
}

export async function getDirectorsMovie(id){
    const response = await requests.getData(`/api/main/directors?movieId=${id}`);
    console.log("Response");
    console.log(response);
    if (!response.ok) {
        return {
            status: response?.status,
            isError: true,
            error: `Request failed with status ${response.status}: ${response.statusText}`,
        };
    }

    // Parse the response data
    const data = await response.json();
    // Return the successful response
    return data;
}

export async function getReviewsOfMovie(id, page=0, size=50){
    const response = await requests.getData(`/api/main/movie-reviews/reviews/${id}?number=${page}&size=${size}`);
    console.log("Response");
    console.log(response);
    if (!response.ok) {
        return {
            status: response?.status,
            isError: true,
            error: `Request failed with status ${response.status}: ${response.statusText}`,
        };
    }

    // Parse the response data
    const data = await response.json();
    // Return the successful response
    return data;
}


