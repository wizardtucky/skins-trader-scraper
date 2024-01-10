package com.application.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RustItemCompareBuy extends RustItemCompare{
    int howManyToBuy;
    Float scrapsMade;
    Float scrapsToEu;
    Float totalScrapsMade;
    Float totalScrapsToEu;
    Float moneyLeft;

    public RustItemCompareBuy(RustItemCompare rustItemCompare, int howManyToBuy, Float moneyLeft, Float scrapsMade) {
        super(rustItemCompare.getItems(), rustItemCompare.getProfitability(), rustItemCompare.getProfitabilityPercentage());
        this.howManyToBuy = howManyToBuy;
        this.moneyLeft = moneyLeft;
        this.scrapsMade = scrapsMade;
    }

    public RustItemCompareBuy(RustItemCompare rustItemCompare) {
        super(rustItemCompare.getItems(), rustItemCompare.getReferenceTraderEu(), rustItemCompare.getComparedTraderEu() ,rustItemCompare.getProfitability(), rustItemCompare.getProfitabilityPercentage());
        this.scrapsMade = 0F;
        this.scrapsToEu = 0F;
    }

    public RustItemCompareBuy() {
    }
}
