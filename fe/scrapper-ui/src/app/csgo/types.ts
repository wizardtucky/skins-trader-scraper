interface CSGOItem {
    id: number;
    name: string;
    priceEu: number;
    trader: string;
    count: number;
    itemType: string;
    condition: string;
    isStatTrack: boolean;
    priceHistory: any[]; // ToDo define price history interface
    statTrack: boolean;
    groupingObject: {
        name: string;
        itemType: string;
        condition: string;
        statTrack: boolean;
    };
}

interface AggregatedCSGOItems {
    items: CSGOItem[];
    profitability: number;
}
