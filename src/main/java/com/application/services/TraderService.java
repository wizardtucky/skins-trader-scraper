package com.application.services;

import com.application.enums.Game;
import com.application.enums.Trader;
import com.application.exceptions.ElementNotFoundException;
import com.application.model.Item;
import com.application.repository.ItemRepository;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class TraderService<E extends Item> {

    protected TraderService(ItemRepository<E> itemRepository, Trader trader, Game game) {
        this.itemRepository = itemRepository;
        this.trader = trader;
        this.game = game;
    }

    protected static final Logger logger = LoggerFactory.getLogger(TraderService.class);
    protected final ItemRepository<E> itemRepository;
    protected ChromeDriver driver;
    protected Trader trader;
    protected Game game;

    public ChromeDriver getDriver() {
        driver = BrowserProvider.createDriver();
        return driver;
    }

    @Async
    public abstract CompletableFuture<Void> updateItemsInRepository() throws InterruptedException, ElementNotFoundException, IOException;

    @Async
    public abstract CompletableFuture<List<E>> getCompletableItemsList() throws InterruptedException, ElementNotFoundException, ExecutionException;

    @Async
    @PreDestroy
    public CompletableFuture<Void> destroy() {
        driver.quit();
        return CompletableFuture.allOf();
    }

    public Trader getTrader() {
        return trader;
    }

    public Game getGame() {
        return game;
    }
}
