package com.example.wilberg.bankapp.Model;

public class CarInfo {
	
	private String rowId;
	private String carId;
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
	private String imgURL;

	public CarInfo(String rowId, String carId, String name, String year, String distance, String price, String brand, String model, String coachbuilder,
				   String gearType, String fuel, String location, String imgURL){
		
		this.rowId = rowId;
		this.carId = carId;
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
		this.imgURL = imgURL;
		
	}
	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}
	
	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
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

	public String getImgURL() { return imgURL; }

	public void setImgURL(String imgURL) { this.imgURL = imgURL; }

}
