package com.application.services.csgoTraders;

import com.application.enums.Condition;
import com.application.model.CSGOItem;
import com.application.repository.CSGOItemRepository;
import com.application.services.TraderService;
import com.application.utils.MessagesUtils;
import com.application.exceptions.ElementNotFoundException;
import com.application.utils.SeleniumUtils;
import com.application.utils.TransformUtils;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.application.constants.CsDealsConst.*;
import static com.application.enums.Game.CSGO;
import static com.application.enums.Trader.CSDEALS;

@Component
public class CsDealsCSGOService extends TraderService<CSGOItem> {

    private CSGOItemRepository itemRepository;

    @Autowired
    public CsDealsCSGOService(CSGOItemRepository itemRepository) {
        super(itemRepository, CSDEALS, CSGO);
        this.itemRepository = itemRepository;
    }

    @Override
    public CompletableFuture<Void> updateItemsInRepository() throws ElementNotFoundException {
        setup();
        itemRepository.saveAll(getItemsList());
        logger.info(MessagesUtils.getItemsSavedLog(getTrader()));
        driver.quit();
        return CompletableFuture.allOf();
    }

    @Override
    public CompletableFuture<List<CSGOItem>> getCompletableItemsList() {
        return CompletableFuture.supplyAsync( () -> {
            try {
                setup();
                List<CSGOItem> items = getItemsList();
                driver.quit();
                return items;
            } catch (ElementNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setup() throws ElementNotFoundException {
        driver = getDriver();
        driver.get(URL_CSGO);
        acceptCookies();
        logger.info(MessagesUtils.READY);
    }

    private List<CSGOItem> getItemsList() throws ElementNotFoundException {
        return getElementsWithScroll()
                .stream()
                .map( itemWebElement -> {
                    String itemData = itemWebElement.getText();
                    String[] dataArray = itemData.split(System.lineSeparator());
                    CSGOItem item;
                    if (dataArray.length >= 4) {
                        try {
                            item = getRegularItem(dataArray);
                        } catch (Exception e) {
                            item = getNonStandardItem(dataArray, itemData.contains("Sticker"));
                        }
                    } else {
                        item = getNonStandardItem(dataArray, itemData.contains("Sticker"));
                    }
                    return item;
                })
                .toList();
    }

    private CSGOItem getRegularItem(String[] itemData) {
        return new CSGOItem(
                itemData[0],
                getPriceFromString(itemData[3]),
                1,
                getFormattedItemType(itemData[2]),
                TransformUtils.getConditionByLabel(itemData[1]),
                isStatTrack(itemData[2]),
                getTrader()
        );
    }

    private CSGOItem getNonStandardItem(String[] itemData, boolean isSticker) {
        float price;
        if (itemData.length >= 2) {
            if (itemData[1].contains("€") && itemData[1].length() > 2) {
                price = getPriceFromString(itemData[1]);
            } else if (itemData[2].contains("€") && itemData[2].length() > 2) {
                price = getPriceFromString(itemData[2]);
            } else if (itemData[3].contains("€") && itemData[3].length() > 2) {
                price = getPriceFromString(itemData[3]);
            } else {
                price = -1F;
            }
            String type;
            if (isSticker) {
                type = "Sticker";
            } else {
                type = "Unknown";
            }
            return new CSGOItem(
                    itemData[0],
                    price,
                    1,
                    type,
                    Condition.NONE,
                    false,
                    getTrader()
            );
        } else {
            return new CSGOItem(
                    itemData[0],
                    -1F,
                    1,
                    "Unknown",
                    Condition.NONE,
                    false,
                    getTrader()
            );
        }
    }

    private Float getPriceFromString(String priceString) {
        priceString = priceString.split(" ")[0].replaceAll(",", "");
        return Float.valueOf(priceString);
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
        var maxLoops = 20;
        var loopsCounter = 0;
        var maxCount = 4000;
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

                // wait for loading spinner to appear and then disappear
                try {
                    SeleniumUtils.waitForElement(driver, LOADING_ITEM_CSS_SELECTOR).orElseThrow(ElementNotFoundException::new);
                    SeleniumUtils.waitUntilIsHidden(driver, LOADING_ITEM_CSS_SELECTOR).orElseThrow(ElementNotFoundException::new);
                } catch (ElementNotFoundException e) {
                    logger.info("Failed to load new batch of items for " + CSDEALS + ". Trying again");
                }
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

    private void acceptCookies() {
        try {
            SeleniumUtils.waitForElement(driver, ACCEPT_COOKIES_BUTTON).orElseThrow(ElementNotFoundException::new).click();
            SeleniumUtils.waitForElement(driver, CLOSE_UPDATE_WINDOW_BUTTON).orElseThrow(ElementNotFoundException::new).click();
        } catch (Exception e) {
            logger.info("Cookies element not found for " + CSDEALS + ". Initializing scraper.");
        }
    }
}
