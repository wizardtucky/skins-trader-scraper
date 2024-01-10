package com.application;

import com.application.enums.Game;
import com.application.enums.Trader;
import com.application.exceptions.ElementNotFoundException;
import com.application.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(path = "/bot")
public class ScraperController {

    private final ScraperService scraperService;

    public ScraperController(ScraperService scraperService) {
        this.scraperService = scraperService;
    }

    @GetMapping(path = "/save")
    public @ResponseBody
    Void saveAll() {
        return scraperService.updateItemsByGamesAndTraders(Arrays.stream(Game.values()).toList(), Arrays.stream(Trader.values()).toList());
    }

    @CrossOrigin
    @GetMapping(path = "/save/{game}")
    public @ResponseBody
    Void saveByGame(@PathVariable("game") Game game) {
        return scraperService.updateItemsByGamesAndTraders(List.of(game), Arrays.stream(Trader.values()).toList());
    }

    @GetMapping(path = "/save/{game}/{trader}")
    public @ResponseBody
    Void saveByGameAndTrader(@PathVariable("game") Game game, @PathVariable("trader") Trader trader) {
        return scraperService.updateItemsByGamesAndTraders(List.of(game), List.of(trader));
    }

    @ResponseBody
    @GetMapping(path = "fetch/CSGO/{trader}")
    public ResponseEntity<List<CSGOItem>> getCSGOItems(@PathVariable("trader") Trader trader) throws InterruptedException, ElementNotFoundException, ExecutionException {
        var entities = scraperService.getCSGOItemsByTrader(trader);
        return ResponseEntity.ok(entities);
    }

    @ResponseBody
    @GetMapping(path = "fetch/RUST/{trader}")
    public ResponseEntity<List<RustItem>> getRustItems(@PathVariable("trader") Trader trader) throws InterruptedException, ElementNotFoundException, ExecutionException {
        var entities = scraperService.getRustItemsByTrader(trader);
        return ResponseEntity.ok(entities);
    }

    @ResponseBody
    @GetMapping(path = "aggregated/RUST")
    public ResponseEntity<List<AggregatedRustItems>> getAggregatedRustItems() {
        var entities = scraperService.getAggregatedRustItems();
        return ResponseEntity.ok(entities);
    }

    @CrossOrigin
    @ResponseBody
    @GetMapping(path = "aggregated/CSGO")
    public ResponseEntity<List<AggregatedCSGOItems>> getAggregateCSGOItems() {
        var entities = scraperService.getAggregatedCSGOItems(Optional.empty());
        return ResponseEntity.ok(entities);
    }

    @CrossOrigin
    @ResponseBody
    @GetMapping(path = "aggregated/CSGO/{trader}")
    public ResponseEntity<List<AggregatedCSGOItems>> getAggregateCSGOItemsByTrader(@PathVariable("trader") Trader trader) {
        var entities = scraperService.getAggregatedCSGOItems(Optional.of(trader));
        return ResponseEntity.ok(entities);
    }
    // utilities with data
    @ResponseBody
    @GetMapping(path = "getDeals/{referenceTrader}/{excludedTraders}")
    public ResponseEntity<List<RustItemCompare>> getListOfItemsForTraderComparison(@PathVariable("referenceTrader") Trader referenceTrader, @PathVariable("excludedTraders") Trader[] excludedTraders) throws ElementNotFoundException, ExecutionException, InterruptedException {
        Set<Trader> excludedTradersSet = new HashSet<>(Arrays.asList(excludedTraders));
        var entities = scraperService.getListOfItemsForTraderComparison(referenceTrader,excludedTradersSet);
        return ResponseEntity.ok(entities);
    }
    @ResponseBody
    @GetMapping(path = "getlist/RUST/BanditCamp")
    public ResponseEntity<List<RustItem>> getListOfItemsBanditCamp() throws ElementNotFoundException, ExecutionException, InterruptedException {
        var entities = scraperService.getBanditCampList();
        return ResponseEntity.ok(entities);
    }

    @ResponseBody
    @GetMapping(path = "getDeals/RUST/{moneyAmount}/{minProfit}")
    public ResponseEntity<List<RustItemCompareBuy>> getListOfItemsBanditCamp(@PathVariable("moneyAmount") Float moneyAmount, @PathVariable("minProfit") Float minProfit) throws ElementNotFoundException, ExecutionException, InterruptedException {
        var entities = scraperService.getListOfItemsByMoneyAmountAndMinPrice(moneyAmount, minProfit);
        return ResponseEntity.ok(entities);
    }

}