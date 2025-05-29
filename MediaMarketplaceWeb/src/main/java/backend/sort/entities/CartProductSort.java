package backend.sort.entities;

public enum CartProductSort {
	
	PRICE("price"),
	DISCOUNT("discount");
	
	private final String value;
	
	CartProductSort(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static CartProductSort fromValue(String value) {
		for (CartProductSort sort : CartProductSort.values()) {
			if (sort.getValue().equalsIgnoreCase(value)) {
				return sort;
			}
		}
		return null;
	}
}