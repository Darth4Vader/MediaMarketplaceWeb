package backend.startup;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import backend.entities.CurrencyExchange;
import backend.services.CurrencyService;

@Component
public class CurrencyStartupChecker {

    @Autowired
	private CurrencyService currencyService;

    @EventListener(ApplicationReadyEvent.class)
    public void checkIfCurrencyDataIsStale() {
    	LocalDateTime now = LocalDateTime.now();

        // Don't run if it's around 2:00 AM (to avoid conflict with scheduled task)
        int hour = now.getHour();
        int minute = now.getMinute();

        boolean isNearScheduledTime = hour == 2 && minute <= 1  // 2:00 or 2:01
                                   || hour == 1 && minute >= 59; // 1:59

        if (isNearScheduledTime) {
            System.out.println("[Startup] Near scheduled time (2AM), skipping startup update.");
            return;
        }

        CurrencyExchange latest = currencyService.getLatestExchange();
        
        System.out.println(latest.getLastUpdated());

        boolean shouldUpdate = latest == null ||
            latest.getLastUpdated().isBefore(now.minusHours(24));

        if (shouldUpdate) {
            System.out.println("[Startup] Currency data is stale. Fetching now...");
            currencyService.updateAllCurrencyExchanges();
            System.out.println("[Startup] Currency data update complete.");
        } else {
            System.out.println("[Startup] Currency data is fresh. No need to update.");
        }
    }
}