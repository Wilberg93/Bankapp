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

    private final static String CAR_ID = "com.example.wilberg.bankapp.CAR_ID";
    private final static String LIST_POSITION = "com.example.wilberg.bankapp.LIST_POSITION";

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

    public CarsAdapter(Context context, List<Car> cars) {
        this.context = context;
        this.cars = cars;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View carView = inflater.inflate(R.layout.item_car, parent, false);

        ViewHolder viewHolder = new ViewHolder(carView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Car car = cars.get(position);

        TextView carInfoTextView = holder.carInfoTextView;
        TextView locationTextView = holder.locationTextView;
        TextView priceTextView = holder.priceTextView;
        TextView distanceTextView = holder.distanceTextView;
        TextView yearTextView = holder.yearTextView;
        ImageView carImageView = holder.carImageView;

        carInfoTextView.setText(car.getName());
        yearTextView.append(car.getYear());
        distanceTextView.setText(car.getDistance());
        locationTextView.setText(car.getLocation());
        priceTextView.setText(context.getString(R.string.price_value_text_view, car.getPrice()));
        Picasso.with(context).load(car.getMainImgURL()).fit().into(carImageView);


    }

    @Override
    public int getItemCount() {
        return (cars != null ? cars.size() : 0);
    }

    // Used to cache the views within the item layout for fast access
    class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public Button messageButton;
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
                        if (position != RecyclerView.NO_POSITION) {
                            carImageView.setTransitionName("carImage" + cars.get(position).getCarID());
                            carInfoTextView.setTransitionName("carTitle" + cars.get(position).getTitle());
                            Pair<View, String> p1 = Pair.create((View) carImageView, carImageView.getTransitionName());
                            Pair<View, String> p2 = Pair.create((View) carInfoTextView, carInfoTextView.getTransitionName());
                            Intent intent = new Intent(context, CarInfoActivity.class);
                            intent.putExtra(CAR_ID, cars.get(position).getCarID());
                            intent.putExtra(LIST_POSITION, position);
                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, p1, p2);
                            context.startActivity(intent, optionsCompat.toBundle());
                            //listener.onItemClick(itemView, position, carImageView, carInfoTextView);
                        }
                    }
                }
            });

        }

    }

}
