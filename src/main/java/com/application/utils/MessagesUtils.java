package com.application.utils;

import com.application.enums.Trader;

public class MessagesUtils {
    public static final String READY = "Scraper is ready";
    public static final String EXCEPTION_NO_ELEMENT = "Element not found";

    public static String getItemsSavedLog(Trader trader) {
        return "Items updated in repository successfully for " + trader.name();
    }

    public static String getItemsScrapedLog(Trader trader, int amount) {
        return "Items scraped successfully from " + trader.name() + ". Amount = " + amount;
    }

    private MessagesUtils() {
    }
}
