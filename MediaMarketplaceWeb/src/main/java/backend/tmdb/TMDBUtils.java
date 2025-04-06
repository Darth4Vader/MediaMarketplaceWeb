package backend.tmdb;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class TMDBUtils {
	
	/**
	 * Load the TMDB API key from the tmdb.key file.
	 * If the file is not found, or if the file is empty, or if there is an error reading the file, 
	 * then a TMDBKeyLoadingException is thrown
	 * @return the TMDB API key
	 */
	public static String loadApiKey() {
		Scanner scanner = null;
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream("tmdb.key");
			if (is == null) {
				throw new TMDBKeyLoadingException("Could not load tmdb.key file");
			}
			scanner = new Scanner(is);
			String apiKey = scanner.nextLine();
			return apiKey;
		}
		catch (TMDBKeyLoadingException e) {
            throw e;
		}
		catch (NoSuchElementException e) {
			throw new TMDBKeyLoadingException("cannot find the key, The file is empty");
		}
		catch (Exception e) {
			throw new TMDBKeyLoadingException("Could not load TMDB API key", e);
		}
		finally {
			if (scanner != null) scanner.close();
		}
	}

}
