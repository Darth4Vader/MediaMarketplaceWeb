package backend.services.ai;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import backend.dtos.ai.RecommendResponse;
import backend.dtos.ai.RecommendResponse.Recommendation;
import backend.dtos.references.MovieReference;
import backend.entities.Movie;
import backend.entities.User;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.GeneralServerException;
import backend.services.MovieService;
import backend.services.TokenService;
import backend.utils.RestClientUtils;

@Service
public class MovieRecommendationService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("logging.level.myapp.logging.fastapi");
	
	@Value("${fastapi.url}")
	private String fastApiUrl;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private MovieService movieService;
	
	public List<MovieReference> getRecommendations(int topN) throws EntityNotFoundException {
		User user = tokenService.getCurretUser(); 
		String url = String.format("/recommend/%s?top_n=%s", user.getId(), topN);
		RecommendResponse response = null;
		try {
			url = fastApiUrl + url;
			// we use custom rest client to force http 1.1
			// becuase fastapi sometimes have issues with http 2.0
			// this is a workaround (maybe temporary) to fix the issue
			RestClient restClient = RestClientUtils.createRestClientVersion1();
			response = restClient.get()
					.uri(url)
					.accept(MediaType.APPLICATION_JSON)
					.retrieve()
					.body(RecommendResponse.class); // Convert the response to List<Map<String, Object>>
		} catch (Exception e) {
			// Handle errors (e.g., logging), return null or default response as fallback
			LOGGER.error("Error fetching recommendations from FastAPI: {}", e.getMessage());
			throw new GeneralServerException("Failed to fetch recommendations", e);
		}
		List<MovieReference> recommendations = new ArrayList<>();
		List<Recommendation> list = response.getRecommendations();
		for(Recommendation rec : list) {
			Long movieId = Long.valueOf(rec.getMovieId());
			Movie movieDto = movieService.getMovieByID(movieId);
			MovieReference reference = movieService.convertMovieToReference(movieDto);
			recommendations.add(reference);
		}
		return recommendations;
	}
}