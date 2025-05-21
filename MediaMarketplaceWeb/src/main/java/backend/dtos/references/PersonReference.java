package backend.dtos.references;

public class PersonReference {
	
	private Long id;
	
	private String name;
	
	private String imagePath;

	public PersonReference() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}	
}