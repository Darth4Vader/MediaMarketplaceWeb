package backend.dtos.search;

public class PageableDto {
	
	private int page;
	private int size;
	
	public PageableDto() {
		// TODO Auto-generated constructor stub
	}

	public int getPage() {
		return page;
	}

	public int getSize() {
		return size;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	

}
