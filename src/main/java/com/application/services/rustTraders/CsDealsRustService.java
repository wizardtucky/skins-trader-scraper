package com.application.services.rustTraders;

import com.application.exceptions.ElementNotFoundException;
import com.application.model.RustItem;
import com.application.repository.RustItemRepository;
import com.application.services.TraderService;
import com.application.utils.MessagesUtils;
import com.application.utils.SeleniumUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.application.constants.CsDealsConst.*;
import static com.application.enums.Game.RUST;
import static com.application.enums.Trader.CSDEALS;

@Component
public class CsDealsRustService extends TraderService<RustItem> {

    private final RustItemRepository itemRepository;

    @Autowired
    public CsDealsRustService(RustItemRepository itemRepository) {
        super(itemRepository, CSDEALS, RUST);
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
    public CompletableFuture<List<RustItem>> getCompletableItemsList() {
        return CompletableFuture.supplyAsync( () -> {
            try {
                setup();
                List<RustItem> items = getItemsList();
                driver.quit();
                return items;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    private void setup() {
        driver = getDriver();
        driver.get(URL_RUST);
//        acceptCookies();
        logger.info(MessagesUtils.READY);
    }

    private List<RustItem> getItemsList() throws ElementNotFoundException, InterruptedException {
        return getElementsWithScroll()
                .stream()
                .map( itemWebElement ->
                    new RustItem(
                        getName(itemWebElement),
                        getPrice(itemWebElement),
                        getCount(itemWebElement),
                        getTrader()
                    )
                )
                .toList();
    }

    private String getName(WebElement webElement) {
        return ((RemoteWebElement) webElement).findElementByClassName(ITEM_NAME_CLASS).getText();
    }
    private Integer getCount(WebElement webElement) {
        String count = ((RemoteWebElement) webElement).findElementByClassName(ITEM_COUNT_CLASS).getText();
        if(count.isBlank()) return 1;
        return Integer.parseInt(((RemoteWebElement) webElement).findElementByClassName(ITEM_COUNT_CLASS).getText());
    }

    private Float getPrice(WebElement webElement) {
        var priceString = ((RemoteWebElement) webElement)
                .findElementByClassName(ITEM_PRICE_CLASS)
                .getText()
                .split(" ")[0]
                .replaceAll(",", "");
        return Float.valueOf(priceString);
    }

    private List<WebElement> getElementsWithScroll() {
        var maxLoops = 83;
        var loopsCounter = 0;
        var maxCount = 10000; // galima bandyt pasikelt max jei bus poreikis
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

        } while (itemCount < maxCount || loopsCounter < maxLoops);

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
            logger.info("Failed to click cookies element for " + CSDEALS + ". Initializing scraper.");
        }
    }
}
