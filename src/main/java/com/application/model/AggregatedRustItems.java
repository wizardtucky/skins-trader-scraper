package com.application.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AggregatedRustItems {
    List<RustItem> items;
    Double profitability;

    public AggregatedRustItems(List<RustItem> items, Double profitability) {
        this.items = items;
        this.profitability = profitability;
    }

    public Double getProfitability() {
        return profitability;
    }

    public AggregatedRustItems() {
        super();
    }
}
