package com.example.wilberg.bankapp;

import com.example.wilberg.bankapp.Model.CarInfo;

import java.util.ArrayList;

/**
 * Created by WILBERG on 8/20/2016.
 */
public class Globals {

    private ArrayList<CarInfo> cars;
    private ArrayList<CarInfo> favoritedCars;
    private int page;

    private static Globals globalsInstance;

    private Globals() {

        cars = new ArrayList<>();
        favoritedCars = new ArrayList<>();
        page = 0;

    }

    public static Globals getInstance() {

        if(globalsInstance == null)
            globalsInstance = new Globals();
        return globalsInstance;

    }
    public ArrayList<CarInfo> getCars() {
        return cars;
    }

    public void updateCars(ArrayList<CarInfo> currentCars) {
        cars = currentCars;
    }

    public ArrayList<CarInfo> getFavoritedCars() {
        return favoritedCars;
    }

    public void addFavoritedCar(CarInfo selectedCar) {
        favoritedCars.add(selectedCar);
    }

    public void removeFavoritedCar(CarInfo selectedCar) {
        favoritedCars.remove(selectedCar);
    }
    public Integer getPage() { return page; }

    public void updatePage(int currentPage) {
        page = currentPage;
    }
}
