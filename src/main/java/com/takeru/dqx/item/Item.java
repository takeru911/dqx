package com.takeru.dqx.item;

public class Item {
    private final String itemId;
    private final String itemName;

    public Item(String itemId, String itemName){
        this.itemId = itemId;
        this.itemName = itemName;
    }

    public String getItemName(){
        return itemName;
    }

    public String getItemId() {
        return itemId;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId='" + itemId + '\'' +
                ", itemName='" + itemName + '\'' +
                '}';
    }
}
