package com.example.ahmed.tamrah;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class OfferActivity extends AppCompatActivity {

    private Offer offer;
    private Toolbar toolBar;
    //private TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        offer = (Offer) getIntent().getSerializableExtra("Offer");
        TextView title = (TextView) findViewById(R.id.OfferTitle);
        TextView city = (TextView) findViewById(R.id.City);
        TextView type = (TextView) findViewById(R.id.TamrahTypeOfferPage);
        TextView price = (TextView) findViewById(R.id.Phone);
        TextView desc = (TextView) findViewById(R.id.Description);
        TextView rate = (TextView) findViewById(R.id.OfferRating);
        ImageView offerImage = (ImageView) findViewById(R.id.Offer_Image);

        //ToolBar
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        //BackButton toolbar
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        offerImage.setImageDrawable(LoadImageFromWebOperations(offer.getOfferImage()));
        title.setText(offer.getTitle());
        city.setText(offer.getCity());
        type.setText(offer.getType());
        price.setText(offer.getPrice() + " S.R.");
        desc.setText(offer.getDescription());
        //Log.i("the description is:",offer.getDesc());
        if(offer.getRate().equals("-1"))
            rate.setText("N\\A");
        else
            rate.setText(offer.getRate());

        //OfferTitle.setText(offer.getTitle());

    }

    //BackButton toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected( item);
    }

    private void fetchOffer(final String OID) {
        /*DatabaseReference DBRef = FirebaseDatabase.getInstance().getReference("Offer");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Retrieving Offer ...");
        progressDialog.show();
        DBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot = dataSnapshot.child(OID); //????
                //offer.setValues((Map<String, Object>)dataSnapshot.getValue());
                Map<String,Object> map = (Map<String,Object>)dataSnapshot.getValue();
                OfferTitle = (TextView) findViewById(R.id.OfferTitle);
                OfferTitle.setText((String) map.get("Title"));;
                progressDialog.dismiss();
                //updateContext();???
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
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
}
