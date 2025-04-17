import { reject } from "react";


const apiBaseUrl = 'http://localhost:8080';

export async function getData(uri) {
    const settings = {
        method: 'GET',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
        }
    };
    console.log(`${apiBaseUrl}${uri}`);
    try {
        const response = await fetch(`${apiBaseUrl}${uri}`, settings)
            .catch((err) => {
                console.log("Error in fetch:");
                return Promise.reject(err);
                //throw err;
            });

        /*if (!response.ok) {
            throw new Error("Network response was not ok");
        }
        const data = await response.json();
        return data;*/
        //console.log(response.json());
        return response;
    }
    catch (error) {
        console.log("Error fetching data:");
        //window.alert();
        //return Promise.reject(error);
        //throw error;
    }
}


export async function get(uri) {
    const settings = {
        method: 'GET',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json',
        }
    };
    console.log(`${apiBaseUrl}${uri}`);
    try {
        const response = await fetch(`${apiBaseUrl}${uri}`, settings)
            .catch((err) => {
                console.log("Error in fetch:");
                return Promise.reject(err);
                //throw err;
            });

        /*if (!response.ok) {
            throw new Error("Network response was not ok");
        }
        const data = await response.json();
        return data;*/
        //console.log(response.json());
        return response;
    }
    catch (error) {
        console.log("Error fetching data:");
        //return Promise.reject(error);
        //throw error;
    }
}


