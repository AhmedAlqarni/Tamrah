package com.example.ahmed.tamrah;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OfferActivity extends AppCompatActivity {

    private Offer offer;
    private User seller;
    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        offer = (Offer) getIntent().getSerializableExtra("Offer");

        //ToolBar
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        //BackButton toolbar
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        fetchProfile(offer.getSeller());
    }

    //BackButton toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected( item);
    }

    private void fetchProfile(final String UID) {
        if(MainActivity.user.getUID().equals(UID)) {
            seller = MainActivity.user;
            updateContext();
            return;
        }
        seller = new User();
        DatabaseReference DBRef = FirebaseDatabase.getInstance().getReference("User");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Retrieving Offer ...");
        progressDialog.show();
        DBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot = dataSnapshot.child(UID);
                seller.setProfileValues((Map<String, Object>)dataSnapshot.getValue());
                progressDialog.dismiss();
                updateContext();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateContext() {
        //Offer-Specific Context
        TextView title = (TextView) findViewById(R.id.OfferTitle);
        TextView city = (TextView) findViewById(R.id.City);
        TextView type = (TextView) findViewById(R.id.TamrahTypeOfferPage);
        TextView price = (TextView) findViewById(R.id.Phone);
        TextView desc = (TextView) findViewById(R.id.Description);
        TextView rate = (TextView) findViewById(R.id.OfferRating);
        ImageView offerImage = (ImageView) findViewById(R.id.Offer_Image);

        offerImage.setImageDrawable(LoadImageFromWebOperations(offer.getOfferImage()));
        title.setText(offer.getTitle());
        city.setText(offer.getCity());
        type.setText(offer.getType());
        price.setText(offer.getPrice() + " S.R.");
        desc.setText(offer.getDescription());
        if(offer.getRate().equals("-1")) {
            rate.setText("N\\A");
            rate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        }
        else
            rate.setText(offer.getRate());

        //Seller-specific Context
        ((TextView) findViewById(R.id.OfferTitleOfferPage)).setText(seller.getName());
        ((TextView) findViewById(R.id.textView4OfferPage)).setText(seller.getPhoneNum());
        ((CircleImageView) findViewById(R.id.profile_imageOfferPage)).setImageDrawable(new ImageFetcher().fetch(seller.getProfilePic()));
        setViewBasedOnUser();

    }


    private void setViewBasedOnUser() {
        if(MainActivity.user.getUID().equals(offer.getSeller())){
            findViewById(R.id.offerReviewPanel).setVisibility(View.GONE);
            findViewById(R.id.offerAddReview).setVisibility(View.GONE);
            findViewById(R.id.AddOfferToCart).setVisibility(View.GONE);
            findViewById(R.id.QuantitiyKG).setVisibility(View.GONE);
        }

        else{
            String[] quantityArray = new String[41];
            quantityArray[0] = "Quantity (in Kilograms)";
            for (int i = 1; i<quantityArray.length; i++)
                quantityArray[i] = (float) i / 2 + "";
            findViewById(R.id.EditOffer).setVisibility(View.GONE);
            ArrayAdapter<String> adp2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, quantityArray);
            adp2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ((Spinner) findViewById(R.id.QuantitiyKG)).setAdapter(adp2);
        }
    }


    //Button Handler
    //This is to make the app title clickable
    public void goToHome(View view) {
        finish();
    }

    //To convert image URL to drawable
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public void goToUserProfile(View view) {
        Intent intent = new Intent(this, AccountActivity.class);
        if (MainActivity.user.getUID().equals(seller.getUID())) {
            intent.putExtra("UID", "myProfile");
            intent.putExtra("User", seller);
        } else
            intent.putExtra("UID", seller.getUID());
        startActivity(intent);
    }

    public void addToCart(View view){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        if(((Spinner) findViewById(R.id.QuantitiyKG)).getSelectedItem().equals("Quantity (in Kilograms)")){
            alertDialog.setTitle("Unspecified Quantity");
            alertDialog.setMessage("Specify the quantity you\nwant to order (in Kg)");
            alertDialog.show();
        }
        else{
            DatabaseReference DBRef = FirebaseDatabase.getInstance().getReference().child("User")
                    .child(Auth.fbAuth.getUid()).child("Cart").child(offer.getOID());
            DBRef.child("Quantity").setValue(((Spinner) findViewById(R.id.QuantitiyKG)).
                    getSelectedItem());

            alertDialog.setTitle("Offer added to cart");
            alertDialog.setMessage("An order of " + ((Spinner) findViewById(R.id.QuantitiyKG)).
                    getSelectedItem() + " Kilograms of this offer\nhad been successfully added to\n" +
                    "your cart");
            alertDialog.show();
        }


    }

}
