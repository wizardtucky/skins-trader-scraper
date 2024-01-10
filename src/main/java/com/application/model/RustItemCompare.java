package com.application.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RustItemCompare {
    List<RustItem> items;
    Float referenceTraderEu;
    Float comparedTraderEu;
    Float profitability;
    Float profitabilityPercentage;

    public RustItemCompare(List<RustItem> items, Float profitability, Float profitabilityPercentage) {
        this.items = items;
        this.profitability = profitability;
        this.profitabilityPercentage = profitabilityPercentage;
    }
    public RustItemCompare(List<RustItem> items, Float referenceTraderEu, Float csDealsEu, Float profitability, Float profitabilityPercentage) {
        this.items = items;
        this.referenceTraderEu = referenceTraderEu;
        this.comparedTraderEu = csDealsEu;
        this.profitability = profitability;
        this.profitabilityPercentage = profitabilityPercentage;
    }
    public RustItemCompare() {
        super();
    }
}
