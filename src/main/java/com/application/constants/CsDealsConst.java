package com.application.constants;

import org.openqa.selenium.By;

public class CsDealsConst {

    private CsDealsConst() {
    }

    public static final String URL_CSGO = "https://cs.deals/market/csgo/?min_price=2&sort=discount&sort_desc=1";
    public static final String URL_RUST = "https://cs.deals/market/rust/?sort=discount&sort_desc=1";

    public static final By ITEM_ELEMENT_XPATH = By.xpath("//*[@id=\"marketplaceInventory\"]/div");
    public static final String ITEM_COUNT_CLASS = "val";
    public static final String ITEM_NAME_CLASS = "name";
    public static final String ITEM_PRICE_CLASS = "price";
    public static final String ITEM_CONDITION_CLASS = "subName";
    public static final By ACCEPT_COOKIES_BUTTON = By.xpath("//*[@id=\"cookie-consent-sm-accept\"]");
    public static final By CLOSE_UPDATE_WINDOW_BUTTON = By.xpath("//*[@id=\"announcementModal2\"]/div[1]/div");
    public static final By LOADING_ITEM_CSS_SELECTOR = By.cssSelector("div.loader");
}
