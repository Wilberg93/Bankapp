package com.example.wilberg.bankapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.wilberg.bankapp.Model.Car;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CarsAdapter extends
        RecyclerView.Adapter<CarsAdapter.ViewHolder> {

    // Define listener member variable
    private OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, ImageView carImage, TextView carInfoTextView);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Provide a direct reference to each of the views within a data item
    private Context context;
    private List<Car> cars;
    private boolean inListView;

    public CarsAdapter(Context context, List<Car> cars) {
        this.context = context;
        this.cars = cars;
        inListView = true;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View carView = inflater.inflate(inListView ? R.layout.item_car : R.layout.item_car_narrow, parent, false);
        return new ViewHolder(carView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Car car = cars.get(position);

        holder.carInfoTextView.setText(car.getName());
        holder.locationTextView.setText(car.getLocation());
        holder.priceTextView.setText(context.getString(R.string.price_value_text_view, car.getPrice()));
        holder.distanceTextView.setText(car.getDistance());
        holder.yearTextView.setText(context.getString(R.string.year_text_view, car.getYear()));
        ImageView carImageView = holder.carImageView;
        Picasso.with(context).load(car.getMainImgURL()).fit().into(carImageView);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) carImageView.setTransitionName("carImage" + car.getCarID());
        carImageView.setTag("carImage" + car.getCarID());
    }

    @Override
    public int getItemCount() {
        return (cars != null ? cars.size() : 0);
    }

    // Used to cache the views within the item item_car_narrow for fast access
    class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView carInfoTextView;
        TextView locationTextView;
        TextView priceTextView;
        TextView distanceTextView;
        TextView yearTextView;
        ImageView carImageView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(final View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            carInfoTextView = (TextView) itemView.findViewById(R.id.carInfoTextView);
            locationTextView = (TextView) itemView.findViewById(R.id.locationTextView);
            priceTextView = (TextView) itemView.findViewById(R.id.priceTextView);
            distanceTextView = (TextView) itemView.findViewById(R.id.distanceTextView);
            yearTextView = (TextView) itemView.findViewById(R.id.yearTextView);
            carImageView = (ImageView) itemView.findViewById(R.id.carImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onItemClick(itemView, position, carImageView, carInfoTextView);
                    }
                }
            });
        }
    }

    public void setInListView(boolean inListView) {
        this.inListView = inListView;
    }
}