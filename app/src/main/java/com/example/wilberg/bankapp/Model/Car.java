package com.example.wilberg.bankapp.Model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Car {
	
	private String title;
	private String carID;
	private String name;
	private String distance;
	private String price;
	private String brand;
	private String model;
	private String coachbuilder;
	private String gearType;
	private String fuel;
	private String year;
	private String location;
	private String mainImgURL;
	private String description;
	private ArrayList<String> imgURLs;
	private LinkedHashMap<String, String> specs;

	public Car(String title, String carID, String name, String year, String distance, String price, String brand, String model, String coachbuilder,
			   String gearType, String fuel, String location, String mainImgURL, ArrayList<String> imgURLs, LinkedHashMap<String, String> specs, String description){
		
		this.title = title;
		this.carID = carID;
		this.name = name;
		this.year = year;
		this.distance = distance;
		this.price = price;
		this.brand = brand;
		this.model = model;
		this.coachbuilder = coachbuilder;
		this.gearType = gearType;
		this.fuel = fuel;
		this.location = location;
		this.mainImgURL = mainImgURL;
		this.imgURLs = imgURLs;
		this.specs = specs;
		this.description = description;
		
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getCarID() {
		return carID;
	}

	public void setCarID(String carID) {
		this.carID = carID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	
	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getCoachbuilder() {
		return coachbuilder;
	}

	public void setCoachbuilder(String coachbuilder) {
		this.coachbuilder = coachbuilder;
	}

	public String getGearType() {
		return gearType;
	}

	public void setGearType(String gearType) {
		this.gearType = gearType;
	}

	public String getFuel() {
		return fuel;
	}

	public void setFuel(String fuel) {
		this.fuel = fuel;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMainImgURL() { return mainImgURL; }

	public void setMainImgURL(String mainImgURL) { this.mainImgURL = mainImgURL; }

	public ArrayList<String> getImgURLs() { return imgURLs; }

	public void setImgURLs(ArrayList<String> imgURLs) { this.imgURLs = imgURLs; }

	public LinkedHashMap<String, String> getSpecs() { return specs; }

	public void setSpecs(LinkedHashMap<String, String> specs) { this.specs = specs; }

	public String getDescription() { return description; }

	public void setDescription(String description) { this.description = description; }

	public static ArrayList<Car> createCarsList() {
		return null;
	}


}
