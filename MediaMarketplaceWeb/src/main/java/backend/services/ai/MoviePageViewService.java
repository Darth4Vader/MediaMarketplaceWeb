package backend.services.ai;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.entities.Movie;
import backend.entities.User;
import backend.entities.ai.MoviePageView;
import backend.exceptions.UserNotLoggedInException;
import backend.repositories.MovieRepository;
import backend.repositories.ai.MoviePageViewRepository;
import backend.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class MoviePageViewService {
	
	@Autowired
	private MoviePageViewRepository pageViewRepository;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private MovieRepository movieRepository;
	
	public static final Integer PAGE_VIEW_COOLDOWN_MINUTES = 2;
	
	// Weight constants
	private static final double LOGGED_IN_WEIGHT = 1.0;
	private static final double ANONYMOUS_WEIGHT = 0.2; // adjust based on your AI model
	private static final int PAGE_SIZE = 5000; // number of rows per page
	
	@Transactional
	public void addMoviePageView(Movie movie, HttpServletRequest request) {
		User user = null;
		String ip = null;
		String sessionId = request.getSession().getId();
		try {
			user = tokenService.getCurretUser();
		} catch (UserNotLoggedInException ignored) {}
		
		// Check min cooldown (query recent views)
		LocalDateTime cutoff = LocalDateTime.now().minusMinutes(PAGE_VIEW_COOLDOWN_MINUTES);
		boolean recentExists;
		if(user != null) {
			recentExists = pageViewRepository.existsByMovieAndUserAndViewedAtAfter(movie, user, cutoff);
		} else {
			// maybe in the futurre handle ips
			/*ip = RequestUtils.getClientIpForCloudflare(request);
			if(!RequestUtils.isIpReal(ip)) {
				// we ignore localhost or invalid ip views
				return;
			}*/
	        // Check if this anonymous session already viewed this movie within cooldown
	        recentExists = pageViewRepository.existsByMovieAndSessionIdAndViewedAtAfter(movie, sessionId, cutoff);
		}
		
		if (recentExists) return;

		MoviePageView view = new MoviePageView();
		view.setMovie(movie);
		view.setUser(user);
		view.setIp(ip); // handle ip logging in the future (maybe not good because 2 devices behind same ip == same ip but different sessions, so ip is irrelevant for now)
		view.setSessionId(sessionId);
		view.setViewedAt(LocalDateTime.now());
		pageViewRepository.save(view);
	}
	
	
	@Scheduled(fixedRate = 60 * 60 * 1000) // every hour
	@Transactional
	public void aggregatePageViews() {
		LocalDateTime cutoff = LocalDateTime.now().minusMinutes(PAGE_VIEW_COOLDOWN_MINUTES);
		int pageNumber = 0;

		Page<MoviePageView> page = null;
		while (page == null || page.hasNext()) {
			page = pageViewRepository.findByViewedAtBefore(
				cutoff, PageRequest.of(pageNumber, PAGE_SIZE)
			);

			if (!page.hasContent()) break;

			// Aggregate counts per movie in this page
			Map<Long, double[]> movieCounts = new HashMap<>();
			for (MoviePageView view : page.getContent()) {
				Long movieId = view.getMovie().getId();
				double[] counts = movieCounts.computeIfAbsent(movieId, _ -> new double[2]);

				if (view.getUser() != null) counts[0]++; // logged-in
				else counts[1]++;                         // anonymous
			}

			// Batch update total page views per movie
			for (Map.Entry<Long, double[]> entry : movieCounts.entrySet()) {
				Long movieId = entry.getKey();
				double[] counts = entry.getValue();
				double weightedViews = counts[0] * LOGGED_IN_WEIGHT + counts[1] * ANONYMOUS_WEIGHT;

				// Efficient batch update query
				movieRepository.incrementTotalPageViews(movieId, weightedViews);
			}

			// Delete processed page
			pageViewRepository.deleteAll(page.getContent());

			pageNumber++;
		}
	}
}