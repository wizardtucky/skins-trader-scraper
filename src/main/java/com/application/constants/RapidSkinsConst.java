package com.application.constants;

import org.openqa.selenium.By;

public class RapidSkinsConst {

    private RapidSkinsConst() {
    }

    public static final String URL = "https://rapidskins.com/market";

    public static final By ITEM_ELEMENT_XPATH = By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div[2]/div[2]/div");
    public static final By UNLOCKED_SKINS_FILTER_BUTTON = By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[1]/div[2]/div[2]/div[3]/div[2]/div[1]/label");
    public static final By GAME_TYPE_BUTTON = By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div[2]/div[1]/div[2]/div[1]");
    public static final By CSGO_TYPE_BUTTON = By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div[2]/div[1]/div[2]/div[2]/div[3]");
    public static final By RUST_TYPE_BUTTON = By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div[2]/div[1]/div[2]/div[2]/div[2]");
}
