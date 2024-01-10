import React from "react";

interface CSGOItemCardProps {
    item: CSGOItem;
}

const CSGOItemCard: React.FC<CSGOItemCardProps> = ({ item }) => {
    return (
        <div style={csgoItemCardStyles}>
            <h3>{item.groupingObject.name}</h3>
            <p>Price: {item.priceEu}</p>
            <p>Trader: {item.trader}</p>
            <p>Count: {item.count}</p>
            <p>Item Type: {item.itemType}</p>
            <p>Condition: {item.condition}</p>
            <p>Is StatTrack: {item.isStatTrack ? 'Yes' : 'No'}</p>
        </div>
    )
}

export default CSGOItemCard;

const csgoItemCardStyles = {
    border: '1px solid #ddd',
    borderRadius: '5px',
    padding: '10px',
    marginBottom: '10px',
    width: '200px',
    marginRight: '10px',
}