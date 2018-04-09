package com.example.ahmed.tamrah;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Warsh on 4/9/2018.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private List<CartItem> cartItemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, rate, city, type, price, quantity;
        public AppCompatImageView img;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.OfferTitle);
            rate = (TextView) view.findViewById(R.id.Rating);
            city = (TextView) view.findViewById(R.id.city);
            type = (TextView) view.findViewById(R.id.TamrahType);
            price = (TextView) view.findViewById(R.id.OfferPrice);
            quantity = (TextView) view.findViewById(R.id.quantity);
            img = (AppCompatImageView) view.findViewById(R.id.offerImg);
        }
    }

    public CartAdapter(List<CartItem> cartItems) {
        this.cartItemList = cartItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_format, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.title.setText(cartItem.getOffer().getTitle());
        holder.rate.setText(cartItem.getOffer().getRate());
        holder.city.setText(cartItem.getOffer().getCity());
        holder.type.setText(cartItem.getOffer().getType());
        holder.price.setText(cartItem.getOffer().getPrice() + " S.R.");
        holder.quantity.setText(cartItem.getQuantity());
        holder.img.setImageDrawable(new ImageFetcher().fetch(cartItem.getOffer().getOfferImage()));
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }
}

