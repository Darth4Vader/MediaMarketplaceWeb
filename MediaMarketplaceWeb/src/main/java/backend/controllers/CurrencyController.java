package backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dtos.users.CurrencyUpdateRequest;
import backend.exceptions.BadRequestException;
import backend.services.CurrencyService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/user/currency")
public class CurrencyController {
	
	@Autowired
	private CurrencyService currencyService;
	
	@PostMapping("/")
	public void saveCurrencyToSession(@RequestBody @Validated CurrencyUpdateRequest request, HttpSession session) throws BadRequestException {
		currencyService.saveCurrencyToSession(request.getCurrencyCode(), session);
	}
}
