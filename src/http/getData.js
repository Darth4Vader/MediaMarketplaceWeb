
/*
export async function getData() {
    const cookieStore = cookies();
    const response = await fetch("https://api.example.com/data");
    if (!response.ok) {
        throw new Error("Network response was not ok");
    }



    const data = await response.json();
    return data;
}




import { SuccessResponse } from "./utils-response";
import { cookies } from "next/headers";


// Generic API function using fetch to fetch data and handle errors
export type FetchError = {status:number,isError:boolean,error:string}
export const fetchData = async <T>(url: string): Promise<T | FetchError> => {
    const cookieStore = cookies();
    try {
        const response = await fetch(
            "youapi/api/v3/" + url,
            {
                method: "GET",
                headers: {
                    accept: "application/json",
                    Authorization: `Bearer ${
                        (await cookieStore).get("accessToken")?.value
                    }`,
                },
                next: {
                    revalidate: 0,
                },
            }
        );
        if (!response.ok) {
            return {
                status: response?.status,
                isError: true,
                error: `Request failed with status ${response.status}: ${response.statusText}`,
            };
        }

        // Parse the response data
        const data: SuccessResponse<T> =
            (await response.json()) as SuccessResponse<T>;

        // Return the successful response
        return data?.data
    } catch (error) {
        console.error("Error fetching data:", error);
        return {
            status:500,
            isError:true,
            error:"Something went wrong"
        }
        // console.error('Error fetching data:', error);
        // if (error instanceof Error) {
        //   throw new Error(error?.message);
        // }
        // // Catch any network or parsing errors

        // throw new Error('Something went wrong ho ki nayt');
    }
};
*/