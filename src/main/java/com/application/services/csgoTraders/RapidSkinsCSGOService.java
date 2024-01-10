package com.application.services.csgoTraders;

import com.application.enums.Condition;
import com.application.exceptions.ElementNotFoundException;
import com.application.model.CSGOItem;
import com.application.repository.CSGOItemRepository;
import com.application.services.TraderService;
import com.application.utils.MessagesUtils;
import com.application.utils.SeleniumUtils;
import com.application.utils.TransformUtils;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.application.constants.RapidSkinsConst.*;
import static com.application.enums.Game.CSGO;
import static com.application.enums.Trader.RAPIDSKINS;

@Component
public class RapidSkinsCSGOService extends TraderService<CSGOItem> {

    private final CSGOItemRepository itemRepository;

    @Autowired
    public RapidSkinsCSGOService(CSGOItemRepository itemRepository) {
        super(itemRepository, RAPIDSKINS, CSGO);
        this.itemRepository = itemRepository;
    }

    @Override
    public CompletableFuture<Void> updateItemsInRepository() throws ElementNotFoundException, InterruptedException {
        setup();
        itemRepository.saveAll(getItemsList());
        logger.info(MessagesUtils.getItemsSavedLog(getTrader()));
        driver.quit();
        return CompletableFuture.allOf();
    }

    @Override
    public CompletableFuture<List<CSGOItem>> getCompletableItemsList() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                setup();
                List<CSGOItem> items = getItemsList();
                driver.quit();
                return items;
            } catch (ElementNotFoundException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setup() throws ElementNotFoundException, InterruptedException {
        driver = getDriver();
        driver.get(URL);
        selectGame();
        logger.info(MessagesUtils.READY);
    }

    private List<CSGOItem> getItemsList() throws ElementNotFoundException {
        return getElementsWithScroll()
                .stream()
                .map(itemWebElement -> {
                    String itemData = itemWebElement.getText();
                    String[] dataArray = itemData.split(System.lineSeparator());
                    CSGOItem item;
                    if (dataArray.length >= 5) {
                        try {
                            item = getRegularItem(dataArray);
                        } catch (Exception e) {
                            item = getNonStandardItem(dataArray);
                        }
                    } else {
                        item = getNonStandardItem(dataArray);
                    }
                    return item;
                })
                .filter( item -> item.priceEu > 0F ) // traders has items which cannot be traded with price $0 so they are filtered out
                .toList();
    }

    private CSGOItem getRegularItem(String[] itemData) {
        return new CSGOItem(
                itemData[3],
                getPriceFromString(itemData[4]),
                1,
                getFormattedItemType(itemData[2]),
                abbreviationToCondition(getFormattedItemType(itemData[0])),
                isStatTrack(itemData[0]),
                getTrader()
        );
    }

    private CSGOItem getNonStandardItem(String[] itemData) {
        if (itemData.length == 4) {
            return new CSGOItem(
                    itemData[2],
                    getPriceFromString(itemData[3]),
                    1,
                    itemData[1],
                    Condition.NONE,
                    false,
                    getTrader()
            );
        } else {
            return new CSGOItem(
                    itemData[1],
                    getPriceFromString(itemData[2]),
                    1,
                    "Unknown",
                    Condition.NONE,
                    false,
                    getTrader()
            );
        }
    }

    private Condition abbreviationToCondition(String abbreviation) {
        return switch (abbreviation) {
            case "FN" -> Condition.FACTORYNEW;
            case "MW" -> Condition.MINIMALWEAR;
            case "FT" -> Condition.FIELDTESTED;
            case "WW" -> Condition.WELLWORN;
            case "BS" -> Condition.BATTLESCARED;
            default -> Condition.NONE;
        };
    }

    private Float getPriceFromString(String priceString) {
        if (priceString.contains("$ ")) {
            priceString = priceString.replaceAll("\\$ ", "");
        }
        priceString = priceString.replaceAll(",", "");
        return TransformUtils.getEuroFromUSD(Float.parseFloat(priceString));
    }

    private String getFormattedItemType(String itemType) {
        if (itemType.contains("StatTrak™")) {
            itemType = itemType.replaceAll("StatTrak™ ", "");
        }
        if (itemType.contains("Souvenir")) {
            itemType = itemType.replaceAll("Souvenir ", "");
        }
        if (itemType.contains("★ ")) {
            itemType = itemType.replaceAll("★ ", "");
        }
        return itemType;
    }

    private Boolean isStatTrack(String itemType) {
        return itemType.contains("StatTrack");
    }

    private List<WebElement> getElementsWithScroll() {
        var maxLoops = 40;
        var loopsCounter = 0;
        var maxCount = 1000;
        var itemCount = 0;
        List<WebElement> items = List.of();
        do {
            loopsCounter++;
            try {
                items = getItemWebElements();
                itemCount = items.size();
                logger.info("Scrapping " + getTrader() + ". Loop counter: " + loopsCounter + ". Total amount: " + itemCount);
                // scroll down to last item to trigger the loading spinner
                driver.executeScript("arguments[0].scrollIntoView();", items.get(items.size() - 1));

            } catch (Exception e) {
                e.printStackTrace();
            }

        } while (itemCount <= maxCount && loopsCounter <= maxLoops);

        logger.info(MessagesUtils.getItemsScrapedLog(getTrader(), items.size()));
        return items;
    }

    private List<WebElement> getItemWebElements() throws ElementNotFoundException {
        return SeleniumUtils.waitForElements(driver, ITEM_ELEMENT_XPATH).orElseThrow(ElementNotFoundException::new);
    }

    private void selectGame() throws InterruptedException {
        int triesToRefresh = 0; // security sometimes detects bot, couple tries with refresh and Thread.sleep(800) helps
        while (triesToRefresh <= 3) {
            try {
                SeleniumUtils.waitForElement(driver, UNLOCKED_SKINS_FILTER_BUTTON).orElseThrow(ElementNotFoundException::new).click();
                SeleniumUtils.waitForElement(driver, GAME_TYPE_BUTTON).orElseThrow(ElementNotFoundException::new).click();
                SeleniumUtils.waitForElement(driver, CSGO_TYPE_BUTTON).orElseThrow(ElementNotFoundException::new).click();
                return;
            } catch (Exception e) {
                logger.info("Unlocked csgo items button failed to click for " + RAPIDSKINS + ". Initializing scraper.");
                Thread.sleep(800);
                triesToRefresh++;
                this.driver.navigate().refresh();
                Thread.sleep(800);
            }
        }
    }
}
