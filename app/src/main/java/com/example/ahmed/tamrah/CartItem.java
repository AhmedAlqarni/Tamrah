package com.example.ahmed.tamrah;

/**
 * Created by Warsh on 4/9/2018.
 */

public class CartItem {
    private String quantity;
    private Offer offer;
    private String OID;

    public CartItem(String OID) {
        this.OID = OID;
    }

    public CartItem(String quantity, Offer offer) {
        this.quantity = quantity;
        this.offer = offer;
        this.OID = offer.getOID();
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public String getOID() {
        return OID;
    }

    public void setOID(String OID) {
        this.OID = OID;
    }
}
