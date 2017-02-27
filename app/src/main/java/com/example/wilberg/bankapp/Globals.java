package com.example.wilberg.bankapp;

import com.example.wilberg.bankapp.Model.Car;

import java.util.ArrayList;

/**
 * Created by WILBERG on 8/20/2016.
 */
public class Globals {

    private ArrayList<Car> cars;
    private ArrayList<Car> favoritedCars;
    private int page;

    private static Globals globalsInstance;

    private Globals() {

        cars = new ArrayList<>();
        favoritedCars = new ArrayList<>();
        page = 1;

    }

    public static Globals getInstance() {

        if(globalsInstance == null)
            globalsInstance = new Globals();
        return globalsInstance;

    }
    public ArrayList<Car> getCars() {
        return cars;
    }

    public void updateCars(ArrayList<Car> currentCars) {
        cars = currentCars;
    }

    public ArrayList<Car> getFavoritedCars() {
        return favoritedCars;
    }

    public void addFavoritedCar(Car selectedCar) {
        favoritedCars.add(selectedCar);
    }

    public void removeFavoritedCar(Car selectedCar) {
        favoritedCars.remove(selectedCar);
    }
    public Integer getPage() { return page; }

    public void updatePage(int currentPage) {
        page = currentPage;
    }
}
