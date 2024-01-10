package com.application.utils;

import com.application.enums.Condition;
import com.application.enums.Trader;
import com.application.model.Item;
import com.application.model.RustItem;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public class TransformUtils {

    private TransformUtils() {
    }

    public static Condition getConditionByLabel(String label) {
        var condition = Arrays.stream(Condition.values()).filter((item) -> item.label.equals(label)).findFirst();
        return condition.orElse(Condition.NONE);
    }

    public static float getEuroFromUSD(float usd) {
        return usd * 0.92F;
    }

    public static <T extends Item> double getItemsProfitabilityScore(List<T> items, Optional<Trader> trader) {
        if (trader.isPresent()) {
            return getItemsSteamProfitabilityByTrader(items, trader.get());
        } else {
            OptionalDouble mostExpensive = items
                    .stream()
                    .mapToDouble(item -> item.priceEu)
                    .max();

            OptionalDouble lestExpensive = items
                    .stream()
                    .mapToDouble(item -> item.priceEu)
                    .min();

            if (mostExpensive.isPresent()) {
                return mostExpensive.getAsDouble() / lestExpensive.getAsDouble();
            } else {
                return 0F;
            }
        }
    }

    public static <T extends Item> double getItemsSteamProfitabilityByTrader(List<T> items, Trader trader) {
        OptionalDouble traderItemPrice = items.stream().filter(a -> a.trader == trader)
                .mapToDouble(item -> item.priceEu)
                .max();

        OptionalDouble steamItemPrice = items.stream().filter(a -> a.trader == Trader.STEAM)
                .mapToDouble(item -> item.priceEu)
                .max();

        if (traderItemPrice.isPresent() && steamItemPrice.isPresent()) {
            return steamItemPrice.getAsDouble() / traderItemPrice.getAsDouble();
        } else {
            return 0F;
        }
    }
}
