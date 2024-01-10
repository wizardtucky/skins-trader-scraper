package com.application.services.csgoTraders;

import com.application.enums.Condition;
import com.application.model.CSGOItem;
import com.application.model.CSGOItemPriceHistory;
import com.application.repository.CSGOItemRepository;
import com.application.services.TraderService;
import com.application.utils.MessagesUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.application.constants.SteamMarketConst.CSGO_ITEMS_CSGO_BACKPACK;
import static com.application.enums.Game.CSGO;
import static com.application.enums.Trader.STEAM;
import static com.application.utils.TransformUtils.getConditionByLabel;

@Component
public class SteamMarketCSGOService extends TraderService<CSGOItem> {

    private final CSGOItemRepository itemRepository;

    @Autowired
    public SteamMarketCSGOService(CSGOItemRepository itemRepository) {
        super(itemRepository, STEAM, CSGO);
        this.itemRepository = itemRepository;
    }

    @Override
    public CompletableFuture<Void> updateItemsInRepository() throws InterruptedException, IOException {
        itemRepository.saveAll(getSteamMarketCsgoItemList());
        logger.info(MessagesUtils.getItemsSavedLog(getTrader()));
        return CompletableFuture.allOf();
    }

    @Override
    public CompletableFuture<List<CSGOItem>> getCompletableItemsList() {
        return CompletableFuture.supplyAsync( () -> {
            try {
                return getSteamMarketCsgoItemList();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<CSGOItem> getSteamMarketCsgoItemList() throws IOException, InterruptedException {
        List<CSGOItem> list = new ArrayList<>();
        for (Map.Entry<String, JsonElement> element : getDataAsJson().entrySet()) {
            JsonObject object = element.getValue().getAsJsonObject();

            String name = getFormattedName(getStringValue(object, "name"));
            String itemType = getItemType(getStringValue(object, "name"));
            Condition condition;
            if (object.has("exterior")) {
                condition = getConditionByLabel(getStringValue(object, "exterior"));
            } else {
                condition = Condition.NONE;
            }

            boolean isStatTrack = object.has("stattrak");

            if (object.has("price")) {
                Pair<Float, Set<CSGOItemPriceHistory>> price = getPriceHistory(object.getAsJsonObject("price"));
                list.add(new CSGOItem(name, price.getLeft(), 1, itemType, condition, isStatTrack, price.getRight(), STEAM));
            } else {
                list.add(new CSGOItem(name, -1.0F, 1, itemType, condition, isStatTrack, STEAM));
            }
        }
        logger.info(MessagesUtils.getItemsScrapedLog(getTrader(), list.size()));
        return list.stream().toList();
    }

    private Pair<Float, Set<CSGOItemPriceHistory>> getPriceHistory(JsonObject priceObject) {
        Set<CSGOItemPriceHistory> priceHistory = new HashSet<>();
        Optional<Float> todaysLowestPrice = Optional.empty();
        Optional<Float> thisWeekLowestPrice = Optional.empty();
        Optional<Float> thisMonthLowestPrice = Optional.empty();

        for (Map.Entry<String, JsonElement> element : priceObject.entrySet()) {
            JsonObject object = element.getValue().getAsJsonObject();

            String name = element.getKey();
            float averagePrice = getFloatValue(object, "average");

            if (Objects.equals(name, "24_hours")) {
                todaysLowestPrice = Optional.of(averagePrice);
            } else if (Objects.equals(name, "7_days")) {
                thisWeekLowestPrice = Optional.of(averagePrice);
            } else if (Objects.equals(name, "30_days")) {
                thisMonthLowestPrice = Optional.of(averagePrice);
            }

            priceHistory.add(
                    new CSGOItemPriceHistory(
                            name,
                            getFloatValue(object, "average"),
                            averagePrice,
                            getIntValue(object, "sold"),
                            getFloatValue(object, "standard_deviation"),
                            getFloatValue(object, "lowest_price"),
                            getFloatValue(object, "highest_price")
                    ));
        }

        Float lowestPrice = -1F;
        if (todaysLowestPrice.isPresent()) {
            lowestPrice = todaysLowestPrice.get();
        } else if (thisWeekLowestPrice.isPresent()) {
            lowestPrice = thisWeekLowestPrice.get();
        } else if (thisMonthLowestPrice.isPresent()) {
            lowestPrice = thisMonthLowestPrice.get();
        }

        return Pair.of(lowestPrice, new HashSet<>(priceHistory));
    }

    private String getFormattedName(String fullName) {
        if (fullName.contains("Sticker")) {
            return fullName.replaceAll("Sticker \\| ", "");
        } else if (fullName.contains("|") && fullName.contains("(")){
            return fullName.split("\\| ")[1].split(" \\(")[0];
        } else {
            return fullName;
        }
    }

    private String getItemType(String fullName) {
        if (fullName.contains("Sticker")) {
            return "Sticker";
        } else {
            String itemType = fullName.split(" \\|")[0];
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
    }

    private String getStringValue(JsonObject object, String fieldName) {
        try {
            return object.getAsJsonPrimitive(fieldName).getAsString();
        } catch (Exception e) {
            return "";
        }
    }

    private float getFloatValue(JsonObject object, String fieldName) {
        try {
            return object.getAsJsonPrimitive(fieldName).getAsFloat();
        } catch (Exception e) {
            return 0F;
        }
    }

    private int getIntValue(JsonObject object, String fieldName) {
        try {
            return object.getAsJsonPrimitive(fieldName).getAsInt();
        } catch (Exception e) {
            return 0;
        }
    }

    private JsonObject getDataAsJson() throws IOException, InterruptedException {
        HttpResponse<String> response = getExternalApiResponse();
        return JsonParser.parseString(response.body()).getAsJsonObject().getAsJsonObject("items_list");
    }

    private HttpResponse<String> getExternalApiResponse() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(CSGO_ITEMS_CSGO_BACKPACK)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
