package com.application.constants;

import org.openqa.selenium.By;

public class BanditCampConst {
    public BanditCampConst() {
    }
    public static final String URL_BANDIT_CAMP_RUST = "https://bandit.camp";
    public static final By ITEM_ELEMENT_XPATH = By.xpath("//*[@id=\"marketplaceInventory\"]/div");
    public static final String ITEM_NAME_CLASS = "name";
    public static final String ITEM_PRICE_CLASS = "price";
    public static final String ITEM_CONDITION_CLASS = "subName";
    public static final By ACCEPT_COOKIES_BUTTON = By.xpath("//*[@id=\"cookie-consent-sm-accept\"]");
    public static final By CLOSE_UPDATE_WINDOW_BUTTON = By.xpath("//*[@id=\"announcementModal2\"]/div[1]/div");
}
