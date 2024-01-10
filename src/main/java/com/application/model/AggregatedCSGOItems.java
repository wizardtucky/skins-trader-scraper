package com.application.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AggregatedCSGOItems {
    List<CSGOItem> items;
    Double profitability;

    public AggregatedCSGOItems(List<CSGOItem> items, Double profitability) {
        this.items = items;
        this.profitability = profitability;
    }

    public Double getProfitability() {
        return profitability;
    }

    public AggregatedCSGOItems() {
        super();
    }
}
