package com.application.services.rustTraders;

import com.application.exceptions.ElementNotFoundException;
import com.application.model.RustItem;
import com.application.repository.ItemRepository;
import com.application.services.TraderService;
import com.application.utils.SeleniumUtils;
import com.google.gson.*;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.application.constants.CsDealsConst.*;
import static com.application.enums.Game.RUST;
import static com.application.enums.Trader.BANDIT_CAMP;


@Component
public class BanditCampRustService extends TraderService<RustItem> {

    private ItemRepository<RustItem> itemRepository;

    protected BanditCampRustService(ItemRepository<RustItem> itemRepository) {
        super(itemRepository, BANDIT_CAMP, RUST);
        this.itemRepository = itemRepository;
    }

    public CompletableFuture<Void> setup() throws ElementNotFoundException, InterruptedException, IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("python", "bandit.py");
        processBuilder.redirectErrorStream(true);
        // Start the process
        Process process = processBuilder.start();
        // Wait for the process to complete
        int exitCode = process.waitFor();
        System.out.println("Process exited with code: " + exitCode);
        return null;
    }

    @Override
    public CompletableFuture<Void> updateItemsInRepository() throws InterruptedException, ElementNotFoundException, IOException { //updateItemsInRepository
        System.out.println("called updateItemsInRepo");
        setup();
        System.out.println("Bandint CAMP SCRAPING IS FINISHED, WILL TAKE DATA FROM FILE AND SAVE TO REPO");
        itemRepository.saveAll(getItemsList());
        return CompletableFuture.allOf();
    }

    private List<RustItem> getItemsList() throws ElementNotFoundException, InterruptedException {
        List<RustItem> list = new ArrayList<>();
        System.out.println("called updateItemsInRepository to start scraper");
        try {
            String data = "";
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\rober\\Desktop\\newScraper\\skins-trader-scrapper\\data.json"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            data = sb.toString();
            JsonArray array = new JsonParser().parse(data).getAsJsonArray();

            for (JsonElement element : array) {
                JsonObject object = element.getAsJsonObject();
                String name = object.getAsJsonPrimitive("name").getAsString();
                String priceRemovingChar = object.getAsJsonPrimitive("price").getAsString().replace(",", "");
                Float price = Float.parseFloat(priceRemovingChar);
                int count = object.getAsJsonPrimitive("amount").getAsInt();
                list.add(new RustItem(name.trim(), price, count, getTrader()));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public CompletableFuture<List<RustItem>> getCompletableItemsList() { //getCompletableItemsList
        System.out.println("first call");
        return CompletableFuture.supplyAsync( () -> {
            try {
                setup();
                return getItemsList();
            } catch (ElementNotFoundException | InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
