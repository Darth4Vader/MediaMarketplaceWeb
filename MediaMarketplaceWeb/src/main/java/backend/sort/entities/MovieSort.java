package backend.sort.entities;

public enum MovieSort {

	/**
	 * Sort by the rating of the movie.
	 */
	RATING("rating");

	private final String value;

	MovieSort(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	public static MovieSort fromValue(String value) {
		for (MovieSort sort : MovieSort.values()) {
			if (sort.getValue().equalsIgnoreCase(value)) {
				return sort;
			}
		}
		return null;
	}
}
