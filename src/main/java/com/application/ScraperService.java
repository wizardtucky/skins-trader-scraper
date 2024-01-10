package com.application;

import com.application.enums.Game;
import com.application.enums.Trader;
import com.application.exceptions.ElementNotFoundException;
import com.application.model.*;
import com.application.repository.CSGOItemRepository;
import com.application.repository.RustItemRepository;
import com.application.services.TraderService;
import com.application.services.csgoTraders.CSTraderCSGOService;
import com.application.services.csgoTraders.CsDealsCSGOService;
import com.application.services.csgoTraders.RapidSkinsCSGOService;
import com.application.services.rustTraders.*;
import com.application.services.csgoTraders.SteamMarketCSGOService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.application.utils.TransformUtils.getItemsProfitabilityScore;


@Service
public class ScraperService {

    private final CSGOItemRepository csItemRepository;
    private final RustItemRepository rustItemRepository;

    private final List<TraderService<CSGOItem>> csgoServices;
    private final List<TraderService<RustItem>> rustServices;

    public ScraperService(
            SteamMarketCSGOService steamMarketCSGOService,
            CsDealsCSGOService csDealsCSGOService,
            CSTraderCSGOService csTraderCSGOService,
            RapidSkinsCSGOService rapidSkinsCSGOService,
            CsDealsRustService csDealsRustService,
            CSTraderRustService csTraderRustService,
            BanditCampRustService banditCampRustService,
//            RapidSkinsRustService rapidSkinsRustService,
            RustClashRustService rustClashRustService,
            CSGOItemRepository csItemRepository,
            RustItemRepository rustItemRepository
    ) {
        this.csItemRepository = csItemRepository;
        this.rustItemRepository = rustItemRepository;
        this.csgoServices = List.of(csDealsCSGOService, csTraderCSGOService, steamMarketCSGOService, rapidSkinsCSGOService);
        this.rustServices = List.of(csDealsRustService, rustClashRustService, banditCampRustService, csTraderRustService); // add Trader to scrape
    }

    public Void updateItemsByGamesAndTraders(List<Game> games, List<Trader> traders) {
        rustItemRepository.deleteAll();
        csItemRepository.deleteAll();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        games.forEach( game -> {
            switch (game) {
                case CSGO -> {
                    for (TraderService<CSGOItem> service : csgoServices) {
                        if (traders.contains(service.getTrader())) {
                            try {
                                futures.add(service.updateItemsInRepository());
                            } catch (InterruptedException | IOException | ElementNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                case RUST -> {
                    for (TraderService<RustItem> service : rustServices) {
                        if (traders.contains(service.getTrader())) {
                            try {
                                futures.add(service.updateItemsInRepository());
                            } catch (InterruptedException | IOException | ElementNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public List<CSGOItem> getCSGOItemsByTrader(Trader trader) throws ElementNotFoundException, ExecutionException, InterruptedException {
        for (TraderService<CSGOItem> service : csgoServices) {
            if (service.getTrader() == trader) {
                return service.getCompletableItemsList().get();
            }
        }
        return null;
    }

    public List<RustItem> getRustItemsByTrader(Trader trader) throws ElementNotFoundException, ExecutionException, InterruptedException {
        for (TraderService<RustItem> service : rustServices) {
            if (service.getTrader() == trader) {
                return service.getCompletableItemsList().get();
            }
        }
        return null;
    }

    public List<AggregatedRustItems> getAggregatedRustItems() {
        Map<String, List<RustItem>> duplicates = StreamSupport.stream(rustItemRepository.findAll().spliterator(), false)
                .collect(Collectors.groupingBy(RustItem::getName));

        return duplicates.values().stream().map( rustItems ->
           new AggregatedRustItems(rustItems, getItemsProfitabilityScore(rustItems, Optional.empty()))
        ).sorted(Comparator.comparingDouble(AggregatedRustItems::getProfitability).reversed()).toList();
    }

    public List<AggregatedCSGOItems> getAggregatedCSGOItems(Optional<Trader> trader) {
        // fetch all stored items with price (some items don't have price, and they are saved with -1)
        Map<CSGOItem.CSGOItemGrouping, List<CSGOItem>> duplicates = StreamSupport.stream(csItemRepository.findCSGOItemsByPriceEuGreaterThan(0F).spliterator(), false)
                .collect(Collectors.groupingBy(CSGOItem::getGroupingObject));

        return duplicates.values().stream()
                .filter( duplicateItems -> {

                    if (duplicateItems.size() < 2) {
                        return false;
                    }
                    // a.stream().anyMatch( item -> item.trader == Trader.STEAM )
                    int matchCount = 0;

                    // Check if STEAM element occurs exactly once
                    for (CSGOItem item : duplicateItems) {
                        if (item.trader == Trader.STEAM) {
                            matchCount++;
                            if (matchCount > 1) {
                                return false; // More than one unique occurrence found
                            }
                        }
                    }
                    return matchCount == 1;
                })
                .map( csgoItems ->
                        new AggregatedCSGOItems(
                                csgoItems.stream().sorted(Comparator.comparingDouble(CSGOItem::getPriceEu)).toList(),
                                getItemsProfitabilityScore(csgoItems, trader)
                        )
                )
                .sorted(
                        Comparator.comparingDouble(AggregatedCSGOItems::getProfitability).reversed()
                )
                .toList();
    }

    public List<RustItemCompare> getListOfItemsForTraderComparison(Trader referenceTrader, Set<Trader> excludedTraders)
            throws ElementNotFoundException, ExecutionException, InterruptedException {
        List<RustItem> filteredRustItems = new ArrayList<>();
        rustItemRepository.findAll().forEach(filteredRustItems::add); // getting full list

        List<RustItem> filteredItemsReferenceTrader = filteredRustItems.stream()
                .filter(item -> item.trader == referenceTrader) // getting only reference trader list
                .collect(Collectors.toList());

        List<RustItem> filteredItemsAll = removeDuplicatesAndKeepLowestPrice(filteredRustItems);

        List<RustItemCompare> finalList = new ArrayList<>();
        // get referenced trader coin value
        Float otherTraderCoinValue = 1F;
        Float referencedTraderCoinValue = 1F;
        if(referenceTrader == Trader.RUSTCLASH){
            referencedTraderCoinValue = 0.599F;
        } else if (referenceTrader == Trader.BANDIT_CAMP) {
            referencedTraderCoinValue = 0.549F;
        } else {
            referencedTraderCoinValue = 1F;
        }
        for (RustItem referenceItem : filteredItemsReferenceTrader) { // getting one item from reference trader list and comparing
            if (referenceItem.getCount() >= 20 && referenceTrader == Trader.RUSTCLASH) break; // if RUSTCLASH and more than 20 items, don't check
            Float mainItemPriceEu = Math.round((referenceItem.priceEu * referencedTraderCoinValue) * 100F) / 100F;

            List<RustItem> matchingItems = filteredItemsAll.stream()
                    .filter(matchingItem -> matchingItem.getName().equals(referenceItem.getName().trim())
                            && (excludedTraders == null || !excludedTraders.contains(matchingItem.trader)))
                    .collect(Collectors.toList());

            if (!matchingItems.isEmpty()) {
                for (RustItem compareItem : matchingItems) {
                    if (compareItem.getName().trim().equals("Graffiti Sulfur Storage")) break;
                    if(compareItem.getTrader() == Trader.RUSTCLASH){
                        otherTraderCoinValue = 0.599F;
                    } else if (compareItem.getTrader() == Trader.BANDIT_CAMP) {
                        otherTraderCoinValue = 0.549F;
                    } else if (compareItem.getTrader() == Trader.CSDEALS) {
                        otherTraderCoinValue = 1.02F;
                    } else {
                        otherTraderCoinValue = 1F;
                    }

                    Float comparedItemPriceEu = (float) Math.round((compareItem.priceEu * otherTraderCoinValue) * 100F) / 100F;
                    Float profitabilityPercentage = new BigDecimal(mainItemPriceEu / comparedItemPriceEu)
                            .setScale(4, RoundingMode.HALF_UP)
                            .floatValue();

                    RustItemCompare rustItemCompare = new RustItemCompare();
                    rustItemCompare.setItems(Arrays.asList(referenceItem, compareItem));
                    rustItemCompare.setProfitability(mainItemPriceEu - comparedItemPriceEu);
                    rustItemCompare.setProfitabilityPercentage(profitabilityPercentage * 100F);
                    rustItemCompare.setReferenceTraderEu(mainItemPriceEu);
                    rustItemCompare.setComparedTraderEu(comparedItemPriceEu);
                    finalList.add(rustItemCompare);
                }
            }
        }

        List<RustItemCompare> finalSortedList = finalList.stream()
                .sorted(Comparator.comparing(RustItemCompare::getProfitabilityPercentage).reversed())
                .collect(Collectors.toList());

        return finalSortedList;
    }


    private static List<RustItem> removeDuplicatesAndKeepLowestPrice(List<RustItem> items) {
        return items.stream()
                .collect(Collectors.groupingBy(
                        rustItem -> Arrays.asList(rustItem.getName(), rustItem.getTrader()),
                        Collectors.minBy(Comparator.comparingDouble(RustItem::getPriceEu))))
                .values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
    public List<RustItem> getBanditCampList() throws ElementNotFoundException, ExecutionException, InterruptedException {

        List<RustItem> filteredRustItems2 = new ArrayList<>();
        rustItemRepository.findAll().forEach(filteredRustItems2::add); // getting full list

        List<RustItem> filteredItems = filteredRustItems2.stream()
                .filter(item -> item.trader == Trader.BANDIT_CAMP) // getting only BanditCamp list
                .collect(Collectors.toList());

        return filteredItems;
    }

    public List<RustItemCompareBuy> getListOfItemsByMoneyAmountAndMinPrice(Float moneyAmount, Float minProfit) throws ElementNotFoundException, ExecutionException, InterruptedException {
        List<RustItemCompare> comparedList = getListOfItemsForTraderComparison(Trader.BANDIT_CAMP, null);
        List<RustItemCompareBuy> finalList = new ArrayList<>();
        Float totalScrapsMade = 0F;
        Float totalScrapsToEu = 0F;

        for (RustItemCompare rustItemCompare : comparedList){
            if(moneyAmount > 1F){
                if(rustItemCompare.getComparedTraderEu() <= moneyAmount && rustItemCompare.getProfitability() >= minProfit){
                    RustItemCompareBuy rustItemCompareBuy = new RustItemCompareBuy(rustItemCompare);
                    for (int i = 1; i <= rustItemCompare.getItems().get(1).getCount(); i++){
                        if(moneyAmount >= rustItemCompare.getComparedTraderEu()){
                            moneyAmount -= rustItemCompare.getComparedTraderEu();
                            rustItemCompareBuy.setScrapsMade(rustItemCompareBuy.getScrapsMade() + rustItemCompare.getItems().get(0).getPriceEu());
                            rustItemCompareBuy.setHowManyToBuy(i);
                        } else break;
                    }
                    rustItemCompareBuy.setMoneyLeft(moneyAmount);
                    rustItemCompareBuy.setScrapsToEu(rustItemCompareBuy.getScrapsMade() * 0.549F);
                    totalScrapsMade += rustItemCompareBuy.getScrapsMade();
                    totalScrapsToEu += rustItemCompareBuy.getScrapsToEu();
                    rustItemCompareBuy.setTotalScrapsMade(totalScrapsMade);
                    rustItemCompareBuy.setTotalScrapsToEu(totalScrapsToEu);
                    finalList.add(rustItemCompareBuy);
                }
            } else break;
        }
        return finalList;
    }
}
