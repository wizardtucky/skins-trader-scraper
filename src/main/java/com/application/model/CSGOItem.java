package com.application.model;

import com.application.enums.Condition;
import com.application.enums.Trader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
public class CSGOItem extends Item {
    public String itemType;
    public Condition condition;
    public boolean isStatTrack;

    @OneToMany(mappedBy="csgoItem")
    private Set<CSGOItemPriceHistory> priceHistory;

    // full item (only for steam item)
    public CSGOItem(String name, Float priceEu, int count, String itemType, Condition condition, Boolean isStatTrack, Set<CSGOItemPriceHistory> priceHistory, Trader trader) {
        super(name, priceEu, count, trader);
        this.itemType = itemType;
        this.condition = condition;
        this.isStatTrack = isStatTrack;
        this.priceHistory = priceHistory;
    }

    // without count and price history
    public CSGOItem(String name, Float priceEu, int count, String itemType, Condition condition, Boolean isStatTrack, Trader trader) {
        super(name, priceEu, count, trader);
        this.itemType = itemType;
        this.condition = condition;
        this.isStatTrack = isStatTrack;
    }

    public CSGOItem() {
        super();
    }

    @Data
    static
    public class CSGOItemGrouping {
        private String name;
        private String itemType;
        private Condition condition;
        private boolean isStatTrack;

        public CSGOItemGrouping(String name, String itemType, Condition condition, boolean isStatTrack) {
            this.name = name;
            this.itemType = itemType;
            this.condition = condition;
            this.isStatTrack = isStatTrack;
        }
    }

    public CSGOItemGrouping getGroupingObject() {
        return new CSGOItemGrouping(this.name, this.itemType, this.condition, this.isStatTrack);
    }
}
