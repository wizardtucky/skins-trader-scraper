package com.application.services.rustTraders;

import com.application.exceptions.ElementNotFoundException;
import com.application.model.RustItem;
import com.application.repository.RustItemRepository;
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
import static com.application.enums.Game.RUST;
import static com.application.enums.Trader.RAPIDSKINS;

@Component
public class RapidSkinsRustService extends TraderService<RustItem> {

    private final RustItemRepository itemRepository;

    @Autowired
    public RapidSkinsRustService(RustItemRepository itemRepository) {
        super(itemRepository, RAPIDSKINS, RUST);
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
        return CompletableFuture.supplyAsync(() -> {
            try {
                setup();
                List<RustItem> items = getItemsList();
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

    private List<RustItem> getItemsList() throws ElementNotFoundException {
        return getElementsWithScroll()
                .stream()
                .map(itemWebElement -> {
                    String[] dataArray = itemWebElement.getText().split(System.lineSeparator());
                    return new RustItem(
                            dataArray[1],
                            getPriceFromString(dataArray[2]),
                            1,
                            getTrader()

                    );
                })
                .filter( item -> item.priceEu > 0F ) // traders has items which cannot be traded with price $0 so they are filtered out
                .toList();
    }

    private Float getPriceFromString(String priceString) {
        if (priceString.contains("$ ")) {
            priceString = priceString.replaceAll("\\$ ", "");
        }
        priceString = priceString.replaceAll(",", "");
        return TransformUtils.getEuroFromUSD(Float.valueOf(priceString));
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
                SeleniumUtils.waitForElement(driver, RUST_TYPE_BUTTON).orElseThrow(ElementNotFoundException::new).click();
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
