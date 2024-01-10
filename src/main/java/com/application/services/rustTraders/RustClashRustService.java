package com.application.services.rustTraders;

import com.application.exceptions.ElementNotFoundException;
import com.application.model.RustItem;
import com.application.repository.ItemRepository;
import com.application.services.TraderService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.application.enums.Game.RUST;
import static com.application.enums.Trader.RUSTCLASH;

@Component
public class RustClashRustService extends TraderService<RustItem> {

    private ItemRepository<RustItem> itemRepository;
    protected RustClashRustService(ItemRepository<RustItem> itemRepository) {
        super(itemRepository, RUSTCLASH, RUST);
        this.itemRepository = itemRepository;
    }

    @Override
    public CompletableFuture<Void> updateItemsInRepository() throws InterruptedException, ElementNotFoundException, IOException {
        System.out.println("called updateItemsInRepo  RUSTCLASH");
        setup();
        System.out.println("RUSTCLASH SCRAPING IS FINISHED, WILL TAKE DATA FROM FILE AND SAVE TO REPO");
        itemRepository.saveAll(getItemsList());
        return CompletableFuture.allOf();
    }

    public CompletableFuture<Void> setup() throws ElementNotFoundException, InterruptedException, IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", "rustclash.py");
        processBuilder.redirectErrorStream(true);
        // Start the process
        Process process = processBuilder.start();
        // Wait for the process to complete
        int exitCode = process.waitFor();
        System.out.println("Process exited with code: " + exitCode);
        return null;
    }
    private List<RustItem> getItemsList() throws ElementNotFoundException, InterruptedException {
        List<RustItem> list = new ArrayList<>();
        System.out.println("called updateItemsInRepository to start scraper");
        try {
            String data = "";
            Thread.sleep(5000);
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\rober\\Desktop\\newScraper\\skins-trader-scrapper\\rustclashdata.json"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            data = sb.toString();
            JsonArray innerArray = new JsonParser().parse(data).getAsJsonArray();



            for (JsonElement element : innerArray) {
                JsonObject object = element.getAsJsonObject();
                String name = object.getAsJsonPrimitive("name").getAsString();
                String priceRemovingChar = object.getAsJsonPrimitive("price").getAsString().replace(",", "");
                Float price = Float.parseFloat(priceRemovingChar);
                int count = object.getAsJsonPrimitive("amount").getAsInt();
                list.add(new RustItem(name, price, count, getTrader()));
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public CompletableFuture<List<RustItem>> getCompletableItemsList() throws InterruptedException, ElementNotFoundException, ExecutionException {
        return null;
    }
}
