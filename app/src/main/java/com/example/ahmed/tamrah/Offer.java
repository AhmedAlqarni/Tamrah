package com.example.ahmed.tamrah;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Warsh on 3/7/2018.
 */

public class Offer implements Serializable{
    private String Title, Description, Type, mCity, Seller, Price, Rate, OID, OfferImage;


    public Offer() {}

    public Offer(String title, String type, String city, String price, String rate, String mDesc, String oImage) {
        setTitle(title);
        setType(type);
        setCity(city);
        setPrice(price);
        setRate(rate);
        setDescription(mDesc);
        setOfferImage(oImage);

    }

    //Setters & Getters:
    public String getTitle() {
//        Log.i("", mType);
        return Title;

    }

    public String getOID(){
        return OID;
    }
    public void setOID(String OID){
        this.OID = OID;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        this.mCity = city;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        this.Price = price;
    }

    public String getRate() {
        if(Double.parseDouble(Rate) < 0 )
            return "N\\A";
        else
            return Rate;
    }

    public void setRate(String rate) {
        this.Rate = rate;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Desc) {
        this.Description = Desc;
    }

    public String getSeller() {
        return Seller;
    }

    public void setSeller(String SellerUID) {
        this.Seller = SellerUID;
    }

    public String getOfferImage() {
        return OfferImage;
    }

    public void setOfferImage(String offerImage) {
        this.OfferImage = offerImage;
    }

    public void setValues(Map<String,Object> offerValues) {
        Title = offerValues.get("Title").toString();
        Description = offerValues.get("Description").toString();
        Type = offerValues.get("Type").toString();
        Seller = offerValues.get("Seller").toString();
        Rate = offerValues.get("Rate").toString();
        Type = offerValues.get("Type").toString();
        OfferImage = offerValues.get("OfferImage").toString();
        Price = offerValues.get("Price").toString();

    }

}