package backend.dtos.movies;

public class RuntimeDto {
	
	private Integer minutes;
	private String formattedText;

	public RuntimeDto() {
		// TODO Auto-generated constructor stub
	}
	
	public RuntimeDto(Integer minutes, String formattedText) {
		this.minutes = minutes;
		this.formattedText = formattedText;
	}
	
	public Integer getMinutes() {
		return minutes;
	}
	
	public void setMinutes(Integer minutes) {
		this.minutes = minutes;
	}
	
	public String getFormattedText() {
		return formattedText;
	}
	
	public void setFormattedText(String formattedText) {
		this.formattedText = formattedText;
	}
}