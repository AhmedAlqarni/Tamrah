package com.example.ahmed.tamrah;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchResultActivity extends AppCompatActivity {

    private List<Offer> offerList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference ;
    private Toolbar toolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        if(!MainActivity.user.isLoggedIn())
            findViewById(R.id.Floating_Add_Offer).setVisibility(View.INVISIBLE);
        //ToolBar
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        //BackButton toolbar
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final SearchView searchView = (android.widget.SearchView) findViewById(R.id.search_view);
        final Context context = this;
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if(recyclerView.getAdapter().getItemCount() > 0) {
                    Intent intent = new Intent(context, SearchResultActivity.class);
                    intent.putExtra("text", query);
                    startActivity(intent);
                    finish();
                }
                firebaseOfferSearch(query);
                return false;
            }


        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Offer");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onMovieClick(View view, int position) {
                TextView tv = (TextView) findViewById(R.id.OfferTitle);
                Intent intent = new Intent(SearchResultActivity.this, OfferActivity.class);
                intent.putExtra("Offer", offerList.get(position));
                startActivityForResult(intent, 0);
            }

            @Override
            public void onMovieLongClick(View view, int position) {

            }
        }));
        firebaseOfferSearch("");
        if(getIntent().getStringExtra("text").equals("$"))
            recyclerView.setAdapter(null);
        else{
            firebaseOfferSearch(getIntent().getStringExtra("text"));
            getIntent().putExtra("text", "$");

        }

    }


    //BackButton toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected( item);
    }

    public void goToHome(View view) {
        finish();
    }

    public void firebaseOfferSearch(String query){
        recyclerView.setAdapter(null);
        query = query.toLowerCase();
        Query firebaseQuerySearch = databaseReference.orderByChild("SearchQuery").startAt(query).endAt(query+"\uf8ff");

        FirebaseRecyclerAdapter<Offer,MyViewHolder1> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Offer, MyViewHolder1>(
                Offer.class,
                R.layout.search_result_format,
                MyViewHolder1.class,
                firebaseQuerySearch) {
            @Override
            protected void populateViewHolder(MyViewHolder1 viewHolder, Offer model, int position) {
                viewHolder.setDetails(model.getTitle(), model.getType(), model.getPrice(),
                        model.getCity(), model.getOfferImage(), model.getQuantity());
                offerList.add(model);
            }

//            @Override
//            public void onBindViewHolder(MyViewHolder1 viewHolder, int position) {
//                super.onBindViewHolder(viewHolder, position);
//            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MyViewHolder1 extends RecyclerView.ViewHolder {
        public TextView title, type, city, price, rate;
        private View view;

        public MyViewHolder1(View view) {
            super(view);

            //Created by Khalid
            this.view = view;


        }

        public void setDetails(String title, String type, String price, String city,
                               String imgURL, String Quantity){
            android.support.v7.widget.AppCompatImageView imgView = (android.support.v7.widget.AppCompatImageView) view.findViewById(R.id.offerImg);
            TextView textViewTitle = (TextView) view.findViewById(R.id.OfferTitle);
            TextView textViewType = (TextView)view.findViewById(R.id.TamrahType);
            TextView textViewPrice = (TextView)view.findViewById(R.id.OfferPrice);
            TextView textViewCity = (TextView)view.findViewById(R.id.city);
            TextView textViewRate = (TextView)view.findViewById(R.id.Rating);

            textViewTitle.setText(title);
            textViewType.setText(type);
            textViewPrice.setText(price+"");
            textViewCity.setText(city);
            textViewRate.setText(rate+"");
            if(!imgURL.equals(""))
                imgView.setImageDrawable(new ImageFetcher().fetch(imgURL));
        }
    }

    public void goToAddOffer(View view) {
        startActivity(new Intent(this, AddOfferActivity.class));
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector mGestureDetector;
        private ClickListener mClickListener;


        public RecyclerTouchListener(final Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
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

    public static interface ClickListener{
        void onMovieClick(View view, int position);
        void onMovieLongClick(View view, int position);
    }
}
