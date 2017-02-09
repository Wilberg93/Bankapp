package com.example.wilberg.bankapp.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.wilberg.bankapp.Model.CarInfo;

import java.util.ArrayList;

/**
 * Created by WILBERG on 2/2/2017.
 */
public class DBTools extends SQLiteOpenHelper{

    private static DBTools dbtInstance;

    private static final String DATABASE_NAME = "findyourcar.db";
    private static final String DATABASE_TABLE = "favoritedCars";
    private static final int DATABASE_VERSION = 1;

    public static synchronized DBTools getInstance(Context context) {

        if(dbtInstance == null)
            dbtInstance = new DBTools(context.getApplicationContext());
        return dbtInstance;
    }


    private DBTools(Context applicationContext) {
        super(applicationContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        String createQuery = "CREATE TABLE " +  DATABASE_TABLE + " (carID VARCHAR PRIMARY KEY, carName TEXT, carYear TEXT, carDistance text, carPrice TEXT, carLocation TEXT, mainImgURL TEXT)";
        database.execSQL(createQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i1) {

        String dropQuery = "DROP TAble IF EXISTS " + DATABASE_TABLE;
        database.execSQL(dropQuery);
        onCreate(database);

    }

    public void insertFavCar(CarInfo favoritedCar) {

        SQLiteDatabase database = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("carID", favoritedCar.getCarID());
        values.put("carName", favoritedCar.getName());
        values.put("carYear", favoritedCar.getYear());
        values.put("carDistance", favoritedCar.getDistance());
        values.put("carPrice", favoritedCar.getPrice());
        values.put("carLocation", favoritedCar.getLocation());
        values.put("mainImgURL", favoritedCar.getMainImgURL());

        database.insert(DATABASE_TABLE, null, values);
        database.close();

    }

    public void removeFavCar(CarInfo favoritedCar) {

        SQLiteDatabase database = getWritableDatabase();

        String deleteQuery = "DELETE FROM " + DATABASE_TABLE + " WHERE carID='" + favoritedCar.getCarID() + "'";
        database.execSQL(deleteQuery);

    }

    public CarInfo getFavCar(String carID) {

        CarInfo theCar = null;

        SQLiteDatabase database = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE + " WHERE carID='" + carID + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                theCar = new CarInfo(null, cursor.getString(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4), null, null, null, null, null, cursor.getString(5), cursor.getString(6), null, null, null);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return theCar;

    }

    public ArrayList<CarInfo> getFavoritedCars() {

        ArrayList<CarInfo> favoritedCars = new ArrayList<>();

        SQLiteDatabase database = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE;
        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {

                CarInfo theCar = new CarInfo(null, cursor.getString(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4), null, null, null, null, null, cursor.getString(5), cursor.getString(6), null, null, null);
                favoritedCars.add(theCar);

            } while(cursor.moveToNext());
        }
        cursor.close();
        return favoritedCars;
    }

    public boolean checkForCar(String carID) {
        String booleanReturn = "0";

        SQLiteDatabase database = getReadableDatabase();
        String selectQuery = "SELECT EXISTS(SELECT 1 FROM " + DATABASE_TABLE + " WHERE carID ='" + carID + "' LIMIT 1)";
        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                booleanReturn = cursor.getString(0);
            } while(cursor.moveToNext());
        }
        return booleanReturn.equals("1");

    }
}
