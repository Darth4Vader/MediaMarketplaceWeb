package backend.controllers.ai;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.dtos.references.MovieReference;
import backend.exceptions.EntityNotFoundException;
import backend.services.ai.MovieRecommendationService;

@RestController
@RequestMapping("/main/recommendations")
public class RecommendationController {
	
	@Autowired
	private MovieRecommendationService recommendationService;
	
	@GetMapping("/current-user")
	public List<MovieReference> getRecommendations(@RequestParam(name = "topN", defaultValue = "10") Integer topN) throws EntityNotFoundException {
		return recommendationService.getRecommendations(topN);
	}
}