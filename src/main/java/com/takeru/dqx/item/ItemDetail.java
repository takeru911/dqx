package com.takeru.dqx.item;

import java.util.Objects;

public class ItemDetail extends Item{
    private final int itemQuality;
    private final int itemAlchemy;

    public ItemDetail(String itemId, String itemName, int itemQuality, int itemAlchemy){
        super(itemId, itemName);
        this.itemQuality = itemQuality;
        this.itemAlchemy = itemAlchemy;
    }

    public ItemDetail(Item item, int itemQuality, int itemAlchemy){
        super(item.getItemId(), item.getItemName());
        this.itemQuality = itemQuality;
        this.itemAlchemy = itemAlchemy;
    }

    public int getItemQuality() {
        return itemQuality;
    }

    public int getItemAlchemy() {
        return itemAlchemy;
    }

    @Override
    public String toString() {
        return "[ itemId: " + this.getItemId() + ", itemName: " + this.getItemName() + ", itemQuality: " + this.getItemQuality() +", itemAlchemy: " + this.getItemAlchemy() +" ]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ItemDetail) {
            ItemDetail itemDetail = (ItemDetail) obj;
            return this.getItemId().equals(itemDetail.getItemId()) &&
                    this.getItemName().equals(itemDetail.getItemName()) &&
                    this.getItemQuality() == itemDetail.getItemQuality() &&
                    this.getItemAlchemy() == itemDetail.getItemAlchemy();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItemId(), getItemName(), getItemQuality(), getItemAlchemy());
    }
}
