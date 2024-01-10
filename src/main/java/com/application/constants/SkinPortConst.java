package com.application.constants;

import org.openqa.selenium.By;

import java.util.List;

public class SkinPortConst {

    private SkinPortConst() {
    }

    public static final String URL_CSGO = "https://skinport.com/csgo/market";
    public static final String URL_RUST = "https://skinport.com/rust/market";

    public static final List<String> URLS_BY_PRICE = List.of(
            "https://skinport.com/rust/market?sort=sale&order=desc&pricelt=430",
        "https://skinport.com/rust/market?sort=sale&order=desc&pricegt=401&pricelt=1400",
        "https://skinport.com/rust/market?sort=sale&order=desc&pricegt=1400&pricelt=3092",
        "https://skinport.com/rust/market?sort=sale&order=desc&pricegt=3092&pricelt=14500"
    );

    public static final By ITEM_ELEMENT_XPATH = By.xpath("//*[@id=\"content\"]/div/div[2]/div[2]/div");
    public static final String ITEM_NAME_CLASS = "ItemPreview-itemName";
    public static final String ITEM_PRICE_CLASS = "Tooltip-link";
    public static final String ITEM_CONDITION_CLASS = "subName";
    public static final By ACCEPT_COOKIES_BUTTON = By.xpath("//*[@id=\"root\"]/div[3]/div/div[2]/button[1]/div");
    public static final By CLOSE_UPDATE_WINDOW_BUTTON = By.xpath("//*[@id=\"announcementModal2\"]/div[1]/div");
    public static final By LOADING_ITEM_CSS_SELECTOR = By.cssSelector("div.loader");
}
