package backend.dtos.search;

/**
 * Data Transfer Object (DTO) used for sorting and filtering movie search results.
 */
public class SortDto {
	
	private String property;
	private String direction;
	
	/**
	 * Default constructor.
	 */
	public SortDto() {
		
	}

	public String getProperty() {
		return property;
	}

	public String getDirection() {
		return direction;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
}