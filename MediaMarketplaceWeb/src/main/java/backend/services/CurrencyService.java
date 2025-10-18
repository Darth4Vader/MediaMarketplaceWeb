package backend.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;

import backend.dtos.CurrencyKindDto;
import backend.dtos.CurrencyKindDto.CountryDto;
import backend.entities.CurrencyExchange;
import backend.entities.CurrencyKind;
import backend.entities.User;
import backend.exceptions.BadRequestException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.UserNotLoggedInException;
import backend.repositories.CurrencyExchangeRepository;
import backend.repositories.CurrencyKindRepository;
import backend.repositories.UserRepository;
import backend.utils.I18nUtils;
import backend.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class CurrencyService {
	
	@Autowired
	private CurrencyKindRepository currencyKindRepository;
	
	@Autowired
	private CurrencyExchangeRepository currencyExchangeRepository;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private GeolocationService geolocationService;
	
	private static final String CURRENCY_API_URL_TEMPLATE =
		    "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/%s.json";
	
	/**
	 * created using chatgpt, if you need to add more currencies, please ask chatgpt to make the new map with: currency, most prominent country code that uses it
	 */
	public static final Map<String, String> CURRENCY_TO_COUNTRY = Map.ofEntries(
			Map.entry("DKK", "DK"), // Denmark
			Map.entry("CAD", "CA"), // Canada
			Map.entry("USD", "US"), // United States
			Map.entry("JPY", "JP"), // Japan
			Map.entry("NOK", "NO"), // Norway
			Map.entry("NZD", "NZ"), // New Zealand
			Map.entry("ILS", "IL"), // Israel
			Map.entry("AUD", "AU"), // Australia
			Map.entry("GBP", "GB"), // United Kingdom
			Map.entry("SEK", "SE"), // Sweden
			Map.entry("EUR", "EU"), // European Union
			Map.entry("CHF", "CH"), // Switzerland
			Map.entry("CNY", "CN")  // China
	);
	
	/**
	 * Supported currencies in the system
	 */
	public static final List<String> supportedCurrencies = List.of("USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "NZD", "CNY", "SEK", "NOK", "DKK", "ILS");
	
	public List<CurrencyKindDto> getAllCurrencyKinds() {
		// Load the currencies
		List<CurrencyKind> currencyKinds = currencyKindRepository.findAll();
		// Convert them to DTOs
		List<CurrencyKindDto> currencyDtos = new ArrayList<>();
		for (CurrencyKind currencyKind : currencyKinds) {
			CurrencyKindDto currencyDto = convertCurrencyKindToDto(currencyKind);
			currencyDtos.add(currencyDto);
		}
		return currencyDtos;
	}
	
	@Transactional
	public CurrencyKind getCurrencyFromSessionOrUser(HttpSession session) throws EntityNotFoundException {
		User user = null;
		try {
			user = tokenService.getCurretUser();
		} catch (UserNotLoggedInException ignored) {}

		String sessionCurrencyCode = (String) session.getAttribute("Currency");

		// 1. User has saved currency → use it
		if (user != null && user.getPreferredCurrency() != null) {
			CurrencyKind currencyKind = user.getPreferredCurrency();
			String code = currencyKind.getCode();
			session.setAttribute("Currency", code); // sync DB -> session
			return currencyKind;
		}

		// 2. User has no saved currency, but session has one → save to DB
		if (user != null && sessionCurrencyCode != null) {
			CurrencyKind currencyKind = currencyKindRepository.findByCode(sessionCurrencyCode)
				.orElse(null);
			if (currencyKind != null) {
				user.setPreferredCurrency(currencyKind);
				userRepository.save(user);
				return currencyKind;
			}
		}

		// 3. Anonymous user with session currency
		if (sessionCurrencyCode != null) {
			return getCurrencyFromCode(sessionCurrencyCode);
		}
    	// get the current request
    	HttpServletRequest request = RequestUtils.getCurrentHttpRequest();
		// we will get the default currency for the session country
		return getCurrencyFromCode(getDefaultCurrencyOfSessionCountry(request));
	}
	
	@Transactional
	public void saveCurrencyToSession(String currencyCode, HttpSession session) throws BadRequestException {
		Optional<CurrencyKind> currencyKindOpt = currencyKindRepository.findByCode(currencyCode);
		if (currencyKindOpt.isEmpty()) {
			throw new BadRequestException("Currency not supported");
		}

		// Always set session
		session.setAttribute("Currency", currencyCode);

		// Try to set for logged-in user
		try {
			User user = tokenService.getCurretUser();
			CurrencyKind currencyKind = currencyKindOpt.get();

			user.setPreferredCurrency(currencyKind);
			userRepository.save(user);
		} catch (UserNotLoggedInException ignored) {
			// User is anonymous — only session updated
		}
	}
	
	public void saveAllCurrencyKinds() {
		Set<Currency> cuurencies = Currency.getAvailableCurrencies();
		for(Currency currency : cuurencies) {
			if(supportedCurrencies.contains(currency.getCurrencyCode())) {
				String code = currency.getCurrencyCode();
				String name = currency.getDisplayName();
				// to get the symbol withou locale issues, we use the country code map
				String countryCode = CURRENCY_TO_COUNTRY.getOrDefault(code, I18nUtils.DEFAULT_COUNTRY);
				Locale country = I18nUtils.getLocaleForCountry(countryCode); //Locale.of("", countryCode);
				String symbol = currency.getSymbol(country);
				updateOrCreateCurrencyKind(code, name, symbol);
			}
		}
	}
	
	@Scheduled(cron = "0 0 2 * * *") // daily at 2AM
	public void updateAllCurrencyExchanges() {
		List<CurrencyKind> currencyKinds = currencyKindRepository.findAll();
		for(CurrencyKind fromCurrency : currencyKinds) {
			String fromCode = fromCurrency.getCode().toLowerCase();
			if(fromCode.equalsIgnoreCase("USD")) {
				String url = String.format(CURRENCY_API_URL_TEMPLATE, fromCode);
				RestClient restClient = RestClient.create();
				ResponseEntity<JsonNode> response = restClient.get()
						.uri(url)
						.retrieve()
						.toEntity(JsonNode.class);
				JsonNode json = response.getBody();
				
				if(json == null || json.isEmpty()) {
					System.err.printf("Empty response for %s%n", fromCode);
					continue;
				}
				
				// Extract Date
				String dateText = json.has("date") ? json.get("date").asText() : null;
				LocalDateTime date = LocalDateTime.parse(dateText + "T00:00:00");
				
				// lets use current date for now (because sometimes the api date does not update every date)
				date = LocalDateTime.now();
				
				// Extract rates
				JsonNode ratesNode = json.get(fromCode);
				if(ratesNode == null || ratesNode.isEmpty()) {
					System.err.printf("No rates found for %s%n", fromCode);
					continue;
				}
				
				for(CurrencyKind toCurrency : currencyKinds) {
					String toCode = toCurrency.getCode().toLowerCase();
					if(ratesNode.has(toCode)) {
						BigDecimal rate = ratesNode.get(toCode).decimalValue();
						updateOrCreateCurrencyExchange(fromCurrency, toCurrency, rate, date);
					}
				}
			}
		}
	}
	
	@Transactional
	private void updateOrCreateCurrencyKind(String code, String name, String symbol) {
		Optional<CurrencyKind> currencyKindOpt = currencyKindRepository.findByCode(code);
		CurrencyKind currencyKind = currencyKindOpt.orElse(null);
		if(currencyKind == null) {
			currencyKind = new CurrencyKind();
			currencyKind.setCode(code);
		}
		currencyKind.setName(name);
		currencyKind.setSymbol(symbol);
		currencyKindRepository.save(currencyKind);
	}
	
	@Transactional
	private void updateOrCreateCurrencyExchange(CurrencyKind fromCurrency, CurrencyKind toCurrency, BigDecimal rate, LocalDateTime date) {
		Optional<CurrencyExchange> currencyExchangeOpt = getCurrencyExchange(fromCurrency, toCurrency);
		CurrencyExchange currencyExchange = currencyExchangeOpt.orElse(null);
		if(currencyExchange == null) {
			currencyExchange = new CurrencyExchange();
			currencyExchange.setFromCurrencyKind(fromCurrency);
			currencyExchange.setToCurrencyKind(toCurrency);
		}
		currencyExchange.setRate(rate);
		currencyExchange.setLastUpdated(date);
		currencyExchangeRepository.save(currencyExchange);
	}
	
	public Money exchangeCurrencyAmount(CurrencyKind fromCurrency, CurrencyKind toCurrency, double amount) throws EntityNotFoundException {
		return exchangeCurrencyAmount(fromCurrency, toCurrency, BigDecimal.valueOf(amount));
	}
	
	public Money exchangeCurrencyAmount(CurrencyKind fromCurrency, CurrencyKind toCurrency, BigDecimal amount) throws EntityNotFoundException {
		// Wrap in Joda-Money just for styling, optional
		Money amountFromCurrency = Money.of(getCurrencyUnit(fromCurrency), amount);
		return exchangeCurrencyAmount(fromCurrency, toCurrency, amountFromCurrency);
	}
	
	public Money exchangeCurrencyAmount(CurrencyKind fromCurrency, CurrencyKind toCurrency, Money amountFromCurrency) throws EntityNotFoundException {
		// If same currency, return unchanged
		if (fromCurrency.equals(toCurrency)) {
			return amountFromCurrency;
		}

		// Look up exchange rate
		CurrencyExchange exchange = getCurrencyExchange(fromCurrency, toCurrency)
			.orElseThrow(() -> new EntityNotFoundException("Exchange rate not found."));
		BigDecimal exchangeRate = exchange.getRate();
		
		// convert the amount from the source currency to the target currency (using bankers rounding)
		Money amountToCurrency = amountFromCurrency.convertedTo(getCurrencyUnit(toCurrency), exchangeRate, RoundingMode.HALF_EVEN);
		return amountToCurrency;
	}
	
    public CurrencyExchange getLatestExchange() {
        return currencyExchangeRepository.findTopByOrderByLastUpdatedDesc().orElse(null);
    }
    
    public CurrencyKind getCurrentUserPreferredCurrency() throws EntityNotFoundException {
    	User user = tokenService.getCurretUser(); // or however you get user
    	return getCurrentUserPreferredCurrency(user);
    }
    
    public CurrencyKind getCurrentUserPreferredCurrency(User user) throws EntityNotFoundException {
    	if (user != null && user.getPreferredCurrency() != null) {
    		return user.getPreferredCurrency();
		}
    	// get the current request
    	HttpServletRequest request = RequestUtils.getCurrentHttpRequest();
		// we will get the default currency for the session country
		return getCurrencyFromCode(getDefaultCurrencyOfSessionCountry(request));
    }
    
    public CurrencyKind getCurrencyFromCode(String code) throws EntityNotFoundException {
		return currencyKindRepository.findByCode(code)
			.orElseThrow(() -> new EntityNotFoundException("Currency not found"));
	}
    
    public Optional<CurrencyExchange> getCurrencyExchange(CurrencyKind fromCurrency, CurrencyKind toCurrency) {
		return currencyExchangeRepository.findByFromCurrencyKindAndToCurrencyKind(fromCurrency, toCurrency);
	}
    
    public CurrencyKindDto convertCurrencyKindToDto(CurrencyKind currencyKind) {
		CurrencyKindDto currencyDto = new CurrencyKindDto();
		currencyDto.setCurrencyCode(currencyKind.getCode());
		currencyDto.setCurrencyName(currencyKind.getName());
		currencyDto.setCurrencySymbol(currencyKind.getSymbol());
		String countryCode = CURRENCY_TO_COUNTRY.getOrDefault(currencyKind.getCode(), I18nUtils.DEFAULT_COUNTRY);
		Locale country = I18nUtils.getLocaleForCountry(countryCode);
		currencyDto.setMainCountry(new CountryDto(countryCode, country.getDisplayCountry()));
		
		CurrencyUnit currencyUnit = getCurrencyUnit(currencyKind);
		Set<String> list = currencyUnit.getCountryCodes();
		List<CountryDto> countries = new ArrayList<>();
		for(String cc : list) {
			Locale locale = I18nUtils.getLocaleForCountry(cc);;
			countries.add(new CountryDto(cc, locale.getDisplayCountry()));
		}
		currencyDto.setCountries(countries);
		return currencyDto;
    }
	
	public static CurrencyUnit getCurrencyUnit(CurrencyKind currencyKind) {
		return CurrencyUnit.of(currencyKind.getCode());
	}
	
	public String getDefaultCurrencyOfSessionCountry(HttpServletRequest request) {
		String countryCode = geolocationService.getCountryOfSession(request);
		return getDefaultCurrencyOfCountry(countryCode);
	}
	
	public static String getDefaultCurrencyOfCountry(String countryCode) {
		if(countryCode == null || countryCode.isEmpty()) return I18nUtils.DEFAULT_CURRENCY;
		Locale locale = I18nUtils.getLocaleForCountry(countryCode);
		try {
			Currency currency = Currency.getInstance(locale);
			if(currency != null && supportedCurrencies.contains(currency.getCurrencyCode()))
				return currency.getCurrencyCode();
		} catch (IllegalArgumentException e) {
			// invalid country code
		}
		return I18nUtils.DEFAULT_CURRENCY;
	}
}
