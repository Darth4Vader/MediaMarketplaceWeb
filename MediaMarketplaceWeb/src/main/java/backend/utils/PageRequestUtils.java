package backend.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import backend.dtos.search.PageableDto;
import backend.dtos.search.SortDto;

public class PageRequestUtils {
	
	public static PageRequest getPageRequest(PageableDto pageableDto) {
		int page = pageableDto.getPage();
		int size = pageableDto.getSize();
		SortDto sortDto = pageableDto.getSort();
		if(sortDto != null) {
			String property = sortDto.getProperty();
			String directionValue = sortDto.getDirection();
			Direction direction = Direction.fromString(directionValue);
			Order sortOrder = new Order(direction, property);
			Sort sort = Sort.by(sortOrder);
			return PageRequest.of(page, size, sort);
		}
		return PageRequest.of(page, size);
	}
}
