package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dtos.CurrencyKindDto;
import backend.dtos.users.CurrencyUpdateRequest;
import backend.entities.CurrencyKind;
import backend.exceptions.BadRequestException;
import backend.exceptions.EntityNotFoundException;
import backend.services.CurrencyService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/users/currency")
public class CurrencyController {
	
	@Autowired
	private CurrencyService currencyService;

	@GetMapping("")
	public List<CurrencyKindDto> getAllCurrencies() {
		return currencyService.getAllCurrencyKinds();
	}

	@GetMapping("/current")
	public CurrencyKindDto getSessionCurrentCurrency(HttpSession session) throws EntityNotFoundException {
		//also manage to save the country in the session
		CurrencyKind currency = currencyService.getCurrencyFromSessionOrUser(session);
		return currencyService.convertCurrencyKindToDto(currency);
	}
	
	@PostMapping("/current/")
	public void saveCurrencyToSession(@RequestBody @Validated CurrencyUpdateRequest request, HttpSession session) throws BadRequestException {
		currencyService.saveCurrencyToSession(request.getCurrencyCode(), session);
	}
}
