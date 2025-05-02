package backend.dtos.orders;

import java.util.List;

import backend.dtos.MoviePurchasedDto;

public class UserActiveMoviePurchaseInfo {
	
	private List<MoviePurchasedDto> activePurchases;

	public UserActiveMoviePurchaseInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public UserActiveMoviePurchaseInfo(List<MoviePurchasedDto> activePurchases) {
		super();
		this.activePurchases = activePurchases;
	}
	
	public List<MoviePurchasedDto> getActivePurchases() {
		return activePurchases;
	}
	
	public void setActivePurchases(List<MoviePurchasedDto> activePurchases) {
		this.activePurchases = activePurchases;
	}

}
