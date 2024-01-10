package com.application.services.csgoTraders;

import com.application.enums.Condition;
import com.application.model.CSGOItem;
import com.application.repository.CSGOItemRepository;
import com.application.services.TraderService;
import com.application.utils.MessagesUtils;
import com.application.utils.TransformUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.application.constants.CsTradeConst.CSGO_URL;
import static com.application.enums.Game.CSGO;
import static com.application.enums.Trader.CSTRADE;

@Component
public class CSTraderCSGOService extends TraderService<CSGOItem> {

    private final CSGOItemRepository itemRepository;

    @Autowired
    public CSTraderCSGOService(CSGOItemRepository itemRepository) {
        super(itemRepository, CSTRADE, CSGO);
        this.itemRepository = itemRepository;
    }

    public CompletableFuture<Void> destroy() {
        driver.quit();
        return CompletableFuture.allOf();
    }

    @Override
    public CompletableFuture<Void> updateItemsInRepository() throws InterruptedException, IOException {
        itemRepository.saveAll(getItemsList());
        logger.info(MessagesUtils.getItemsSavedLog(getTrader()));
        return CompletableFuture.allOf();
    }

    @Override
    public CompletableFuture<List<CSGOItem>> getCompletableItemsList() {
        return CompletableFuture.supplyAsync( () -> {
            try {
                return getItemsList();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<CSGOItem> getItemsList() throws IOException, InterruptedException {
        List<CSGOItem> list = new ArrayList<>();
        for(Map.Entry<String, JsonElement> element :  getDataAsJson().entrySet()){
            JsonObject object = element.getValue().getAsJsonObject();
            int count = object.getAsJsonPrimitive("tradable").getAsInt();
            if (count > 0) {
                String fullName = element.getKey();
                try {
                    list.add(
                        new CSGOItem(
                            getFormattedName(fullName),
                            object.getAsJsonPrimitive("price").getAsFloat(),
                            count,
                            getItemType(fullName),
                            getCondition(fullName),
                            isStatTrack(fullName),
                            getTrader()
                        )
                    );

                } catch (Exception e) {
                    logger.error("Failed transformation to CSGOItem for " + fullName);
                }
            }
        }
        logger.info(MessagesUtils.getItemsScrapedLog(getTrader(), list.size()));
        return list.stream().toList();
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

    private boolean isStatTrack(String fullName) {
        return fullName.contains("StatTrak™");
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

    private Condition getCondition(String fullName) {
        if (fullName.endsWith(")")) {
            String[] nameAfterSplit = fullName.split("\\(");
            return TransformUtils.getConditionByLabel(
                    nameAfterSplit[nameAfterSplit.length-1].replaceAll("\\)", "")
            );
        } else {
            return Condition.NONE;
        }
    }

    private JsonObject getDataAsJson() throws IOException, InterruptedException {
        HttpResponse<String> response = getExternalApiResponse();
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    private HttpResponse<String> getExternalApiResponse() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(CSGO_URL)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
