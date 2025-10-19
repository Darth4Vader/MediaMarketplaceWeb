package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.dtos.movies.KeywordCreateRequest;
import backend.dtos.references.KeywordReference;
import backend.dtos.search.KeywordFilter;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.services.KeywordService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/main/keywords")
public class KeywordController {

	@Autowired
	private KeywordService keywordService;

	@GetMapping("")
	public List<KeywordReference> getKeywords(@RequestParam("ids") List<Long> ids) {
		return keywordService.getKeywords(ids);
	}

	@GetMapping("/search")
	public Page<KeywordReference> searchKeywords(KeywordFilter keywordFilter, Pageable pageable) {
		return keywordService.searchKeywords(keywordFilter, pageable);
	}

	@PostMapping("/")
	public ResponseEntity<String> createKeyword(@Valid @RequestBody KeywordCreateRequest request) throws EntityAlreadyExistsException {
		try {
			keywordService.createKeyword(request.getName(), request.getMediaID());
		} catch (DataAccessException e) {
			throw new EntityAdditionException("Unable to add the keyword with the name: " + request.getName(), e);
		}
		return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
	}
}