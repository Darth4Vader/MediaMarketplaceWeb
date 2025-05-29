import org.springframework.context.ConfigurableApplicationContext;

import backend.ActivateSpringApplication;
import backend.services.MovieRatingService;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = ActivateSpringApplication.create(args);
		MovieRatingService service = context.getBean(MovieRatingService.class);
		System.out.println("Add");
		//service.updateAllMoviesRatings();
		System.out.println("End");
	}

}
