package backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.auth.AuthenticateAdmin;
import backend.dtos.references.KeywordReference;
import backend.dtos.search.KeywordFilter;
import backend.entities.Keyword;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.repositories.KeywordRepository;
import backend.utils.SpecificationUtils;
import jakarta.persistence.criteria.Predicate;

/**
 * Service class for managing keywords.
 * <p>
 * This class provides methods for retrieving, creating, and removing keywords
 * in the context of a movie database. Access to certain methods is restricted
 * to admin users.
 * </p>
 * <p>
 * It handles the business logic related to keywords and acts as an intermediary 
 * between the data access layer (repositories) and the presentation layer 
 * (controllers).
 * </p>
 */
@Service
public class KeywordService {

	@Autowired
	private KeywordRepository keywordRepository;

	public List<KeywordReference> getKeywords(List<Long> ids) {
		List<Keyword> keywordList = keywordRepository.findAllById(ids);

		return keywordList.stream()
			.map(this::convertKeywordToReference)
			.toList();
	}

	public Page<KeywordReference> searchKeywords(KeywordFilter keywordFilter, Pageable pageable) {
		Specification<Keyword> specification = createKeywordSearchSpecification(keywordFilter);
		Page<Keyword> keywordPage = keywordRepository.findAll(specification, pageable);
		return keywordPage.map(this::convertKeywordToReference);
	}

	public Specification<Keyword> createKeywordSearchSpecification(KeywordFilter params) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			Predicate filterByName = SpecificationUtils.filterByName(cb, query, params.getName(), root.get("name"));
			if (filterByName != null)
				predicates.add(filterByName);
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	/**
	 * Creates a new keyword in the database.
	 * This method is restricted to admin users.
	 *
	 * @param keywordName The name of the keyword to create.
	 * @param mediaID The TMDB media ID of the keyword.
	 * @throws EntityAlreadyExistsException if a keyword with the same name or media ID exists.
	 */
	@AuthenticateAdmin
	@Transactional
	public void createKeyword(String keywordName, String mediaID) throws EntityAlreadyExistsException {
		if (keywordRepository.findByName(keywordName).isPresent()) {
			throw new EntityAlreadyExistsException("Keyword with name \"" + keywordName + "\" already exists");
		}
		if (keywordRepository.findByMediaID(mediaID).isPresent()) {
			throw new EntityAlreadyExistsException("Keyword with media ID \"" + mediaID + "\" already exists");
		}

		Keyword keyword = new Keyword(keywordName, mediaID);
		keywordRepository.save(keyword);
	}

	/**
	 * Removes a keyword from the database.
	 * This method is restricted to admin users.
	 *
	 * @param keywordName The name of the keyword to remove.
	 * @throws EntityNotFoundException if the keyword doesn't exist.
	 * @throws EntityRemovalException if the keyword is associated with movies.
	 */
	@AuthenticateAdmin
	@Transactional
	public void removeKeyword(String keywordName) throws EntityNotFoundException, EntityRemovalException {
		Keyword keyword = getKeywordByName(keywordName);
		if (keyword.getMovies() == null || keyword.getMovies().isEmpty()) {
			try {
				keywordRepository.delete(keyword);
				return;
			} catch (Throwable e) {
				// Log exception if needed
			}
		}
		throw new EntityRemovalException("Cannot remove keyword \"" + keywordName + "\" because it is associated with movies.");
	}

	public Keyword getKeywordByName(String keywordName) throws EntityNotFoundException {
		return keywordRepository.findByName(keywordName)
			.orElseThrow(() -> new EntityNotFoundException("Keyword with name \"" + keywordName + "\" does not exist"));
	}
	
	public Keyword getKeywordByMediaID(String mediaID) throws EntityNotFoundException {
		return keywordRepository.findByMediaID(mediaID)
			.orElseThrow(() -> new EntityNotFoundException("Keyword with media ID \"" + mediaID + "\" does not exist"));
	}

	public static List<String> convertKeywordsToDto(List<Keyword> keywords) {
		List<String> keywordNames = new ArrayList<>();
		if (keywords != null) {
			for (Keyword keyword : keywords) {
				keywordNames.add(keyword.getName());
			}
		}
		return keywordNames;
	}

	public KeywordReference convertKeywordToReference(Keyword keyword) {
		KeywordReference reference = new KeywordReference();
		reference.setId(keyword.getId());
		reference.setName(keyword.getName());
		return reference;
	}
}