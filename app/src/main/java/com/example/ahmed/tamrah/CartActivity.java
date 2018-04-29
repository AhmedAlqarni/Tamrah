package com.example.ahmed.tamrah;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<CartItem> itemList = new ArrayList<>();
    private CartAdapter mAdapter;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId("AexoeTQlJhyKie47nTbqshmtwk8Ds3fQicOIG3T9BiY1U3PK2S30XyZCnvXwYleySF0OcHM6PDVEhBbL");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        fetchCartItems();
        //final Button button = (Button) findViewById(R.id.EditOffer);


        mAdapter = new CartAdapter(itemList);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new SearchResultActivity.ClickListener() {
            @Override
            public void onMovieClick(View view, int position) {
                TextView tv = (TextView) findViewById(R.id.OfferTitle);
                Intent intent = new Intent(CartActivity.this, OfferActivity.class);
                intent.putExtra("Offer", itemList.get(position).getOffer());
                startActivityForResult(intent, 0);
            }

            @Override
            public void onMovieLongClick(View view, int position) {
            }
        }));
    }

    public void fetchCartItems() {
        final String UID = Auth.fbAuth.getUid();
        final List<CartItem> tmpItemList = new ArrayList<>();
        DatabaseReference DBRef = FirebaseDatabase.getInstance().getReference().child("User")
                .child(UID).child("Cart");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Cart Items ...");
        progressDialog.show();
        DBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> offers = dataSnapshot.getChildren();
                    DataSnapshot tmp;
                    while (true) {
                        if(offers.iterator().hasNext()){
                            tmp = offers.iterator().next();
                            CartItem toBeAddedToList  = new CartItem(tmp.getKey());
                            toBeAddedToList.setQuantity((String)tmp.child("Quantity").getValue());
                            tmpItemList.add(toBeAddedToList);

                        }
                        else
                            break;
                    }
                    fetchOffer(tmpItemList);
                    progressDialog.dismiss();

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }

    private void fetchOffer(final List<CartItem> tmpItemList) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Almost There :)");
        progressDialog.show();
        DatabaseReference DBRef;
        for(int i = 0; i < tmpItemList.toArray().length; i++) {
            final int j = i;
            DBRef = FirebaseDatabase.getInstance().getReference().child("Offer")
                    .child(tmpItemList.get(i).getOID());
            DBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Offer offer = new Offer();
                    offer.setValues((Map<String, Object>) dataSnapshot.getValue());
                    itemList.add(new CartItem(tmpItemList.get(j).getQuantity(), offer));
                    if((j) == tmpItemList.toArray().length - 1) {
                        progressDialog.dismiss();
                        mAdapter.notifyDataSetChanged();
                        double total = 0;
                        for(int i = 0; i < itemList.size(); i++)
                            total = total + Double.parseDouble(itemList.get(i).getOffer().getPrice()) * Double.parseDouble(itemList.get(i).getQuantity().substring(0,itemList.get(i).getQuantity().length() -4));
                        TextView totalView = (TextView) findViewById(R.id.CartTotalPrice);
                        NumberFormat formatter = new DecimalFormat("#0.00");
                        totalView.setText("Total: " + formatter.format(total) + " S.R.");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
//
//    public void removeItem(View view){
//
//    }

    public void checkout(View view){
        double total = 0;
        for(int i = 0; i < itemList.size(); i++)
            total = total + Double.parseDouble(itemList.get(i).getOffer().getPrice()) * Double.parseDouble(itemList.get(i).getQuantity()                .substring(0,itemList.get(i).getQuantity().length() -4));
        total /= 3.75;
        total = Double.parseDouble(new DecimalFormat("#0.00").format(total));



        Intent serviceConfig = new Intent(this, PayPalService.class);
        serviceConfig.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(serviceConfig);

        PayPalPayment payment = new PayPalPayment(new BigDecimal(total),
                "USD", "Tamrah App Order", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent paymentConfig = new Intent(this, PaymentActivity.class);
        paymentConfig.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        paymentConfig.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(paymentConfig, 0);

    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Transaction Successful");
            alertDialog.setMessage("The Transaction has been made\nThe seller will contact you soon");
            updateQuantitiesAfterTransaction();
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            PaymentConfirmation confirm = data.getParcelableExtra(
                    PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null){
                try {
                    Log.i("sampleapp", confirm.toJSONObject().toString(4));

                    // TODO: send 'confirm' to your server for verification

                } catch (JSONException e) {
                    Log.e("sampleapp", "no confirmation data: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Transaction Canceled");
            alertDialog.setMessage("The Transaction has been canceled");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //FirebaseAuth.getInstance().signOut();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            Log.i("sampleapp", "The user canceled.");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Transaction Canceled");
            alertDialog.setMessage("The Transaction has been canceled\nCard Information are invalid");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //FirebaseAuth.getInstance().signOut();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            Log.i("sampleapp", "Invalid payment / config set");
        }
    }

    private void updateQuantitiesAfterTransaction() {


    }

    @Override
    public void onDestroy(){
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector mGestureDetector;
        private SearchResultActivity.ClickListener mClickListener;


        public RecyclerTouchListener(final Context context, final RecyclerView recyclerView, final SearchResultActivity.ClickListener clickListener) {
            this.mClickListener = clickListener;
            mGestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
                    if (child!=null && clickListener!=null){
                        clickListener.onMovieLongClick(child,recyclerView.getChildAdapterPosition(child));
                    }
                    super.onLongPress(e);
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child!=null && mClickListener!=null && mGestureDetector.onTouchEvent(e)){
                mClickListener.onMovieClick(child,rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
