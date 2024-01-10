package com.application.services.rustTraders;

import com.application.model.RustItem;
import com.application.repository.RustItemRepository;
import com.application.services.TraderService;
import com.application.utils.MessagesUtils;
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

import static com.application.constants.CsTradeConst.*;
import static com.application.enums.Game.RUST;
import static com.application.enums.Trader.CSTRADE;

@Component
public class CSTraderRustService extends TraderService<RustItem> {

    private final RustItemRepository itemRepository;

    @Autowired
    public CSTraderRustService(RustItemRepository itemRepository) {
        super(itemRepository, CSTRADE, RUST);
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
    public CompletableFuture<List<RustItem>> getCompletableItemsList() {
        return CompletableFuture.supplyAsync( () -> {
            try {
                return getItemsList();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<RustItem> getItemsList() throws IOException, InterruptedException {
        List<RustItem> list = new ArrayList<>();
        for(Map.Entry<String, JsonElement> element :  getDataAsJson().entrySet()){
            JsonObject object = element.getValue().getAsJsonObject();
            int count = object.getAsJsonPrimitive("tradable").getAsInt();
            if (count > 0) {
                String name = element.getKey();
                Float price = object.getAsJsonPrimitive("price").getAsFloat();
                list.add(new RustItem(name, price, count, getTrader()));
            }
        }
        return list.stream().toList();
    }

    private JsonObject getDataAsJson() throws IOException, InterruptedException {
        HttpResponse<String> response = getExternalApiResponse();
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    private HttpResponse<String> getExternalApiResponse() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(RUST_URL)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
