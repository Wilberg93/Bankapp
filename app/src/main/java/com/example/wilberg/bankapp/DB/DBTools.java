package com.example.wilberg.bankapp.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.wilberg.bankapp.Model.Car;

import java.util.ArrayList;

/**
 * Created by WILBERG on 2/2/2017.
 */
public class DBTools extends SQLiteOpenHelper{

    /* LOG TAG */
    private static final String  TAG = "Database Error";

    private static DBTools dbInstance;

    /* Database Info */
    private static final String DATABASE_NAME = "findyourcar.db";
    private static final int DATABASE_VERSION = 1;

    /* Table Names */
    private static final String TABLE_CARS = "favCars";

    /* Car Table Columns */
    private static final String KEY_CAR_ID = "id";
    private static final String KEY_CAR_NAME = "name";
    private static final String KEY_CAR_YEAR = "year";
    private static final String KEY_CAR_DISTANCE = "distance";
    private static final String KEY_CAR_PRICE = "price";
    private static final String KEY_CAR_LOCATION = "location";
    private static final String KEY_CAR_IMG_URL = "imgURL";


    public static synchronized DBTools getInstance(Context context) {
        /* Use singleton with application context to avoid */
        /* leaking an activity's context */
        if(dbInstance == null)
            dbInstance = new DBTools(context.getApplicationContext());
        return dbInstance;
    }

    /* private constructor, make call to getInstance instead */
    private DBTools(Context applicationContext) {
        super(applicationContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        String CREATE_FAV_CARS_TABLE = "CREATE TABLE " +  TABLE_CARS +
                "(" +
                    KEY_CAR_ID + " TEXT PRIMARY KEY," +
                    KEY_CAR_NAME + " TEXT," +
                    KEY_CAR_YEAR + " TEXT," +
                    KEY_CAR_DISTANCE + " TEXT," +
                    KEY_CAR_PRICE + " TEXT," +
                    KEY_CAR_LOCATION + " TEXT," +
                    KEY_CAR_IMG_URL + " TEXT" +
                ")";
        Log.d("LOLO", CREATE_FAV_CARS_TABLE);

        database.execSQL(CREATE_FAV_CARS_TABLE);

    }
    public void dropDatabase() {
        onUpgrade(getWritableDatabase(), 1, 2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if(oldVersion != newVersion) {
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_CARS);
            onCreate(database);
        }
    }

    public void addFavCar(Car car) {

        SQLiteDatabase database = getWritableDatabase();

        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CAR_ID, car.getCarID());
            values.put(KEY_CAR_NAME, car.getName());
            values.put(KEY_CAR_YEAR, car.getYear());
            values.put(KEY_CAR_DISTANCE, car.getDistance());
            values.put(KEY_CAR_PRICE, car.getPrice());
            values.put(KEY_CAR_LOCATION, car.getLocation());
            values.put(KEY_CAR_IMG_URL, car.getMainImgURL());

            database.insertOrThrow(TABLE_CARS, null, values);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add new car to database");
        } finally {
            database.endTransaction();
        }
    }

    public Car getFavCar(String carID) {

        Car theCar = null;

        SQLiteDatabase database = getReadableDatabase();
        String SELECT_CAR = String.format("SELECT * FROM %s WHERE %s = %s",
                TABLE_CARS,
                KEY_CAR_ID,
                carID);

        Cursor cursor = database.rawQuery(SELECT_CAR, null);
        try {
            if(cursor.moveToFirst()) {
                do {
                    theCar = new Car(null, cursor.getString(cursor.getColumnIndex(KEY_CAR_ID)), cursor.getString(cursor.getColumnIndex(KEY_CAR_NAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_CAR_YEAR)), cursor.getString(cursor.getColumnIndex(KEY_CAR_DISTANCE)),
                            cursor.getString(cursor.getColumnIndex(KEY_CAR_PRICE)), null, null, null, null, null, cursor.getString(cursor.getColumnIndex(KEY_CAR_LOCATION)),
                            cursor.getString(cursor.getColumnIndex(KEY_CAR_IMG_URL)), null, null, null);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
        Log.d(TAG, "Error while trying to get car from database");
    } finally {
        if(cursor != null && !cursor.isClosed())
            cursor.close();
        }
        return theCar;
    }

    public ArrayList<Car> getFavCars() {

        ArrayList<Car> cars = new ArrayList<>();

        SQLiteDatabase database = getReadableDatabase();
        String SELECT_CARS = "SELECT * FROM " + TABLE_CARS;
        Cursor cursor = database.rawQuery(SELECT_CARS, null);
        try {
            if(cursor.moveToFirst()) {
                do {

                    Car theCar = new Car(null, cursor.getString(cursor.getColumnIndex(KEY_CAR_ID)), cursor.getString(cursor.getColumnIndex(KEY_CAR_NAME)),
                            cursor.getString(cursor.getColumnIndex(KEY_CAR_YEAR)), cursor.getString(cursor.getColumnIndex(KEY_CAR_DISTANCE)),
                            cursor.getString(cursor.getColumnIndex(KEY_CAR_PRICE)), null, null, null, null, null, cursor.getString(cursor.getColumnIndex(KEY_CAR_LOCATION)),
                            cursor.getString(cursor.getColumnIndex(KEY_CAR_IMG_URL)), null, null, null);
                    cars.add(theCar);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get all cars from database");
        } finally {
            if(cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return cars;
    }

    public void deleteFavCar(Car favoritedCar) {

        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            database.delete(TABLE_CARS, KEY_CAR_ID + "=" + favoritedCar.getCarID(), null);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete car from database");
        } finally {
            database.endTransaction();
        }
    }

    public void deleteAllFavCars() {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_CARS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all cars");
        } finally {
            db.endTransaction();
        }
    }

    public boolean checkForCar(String carID) {
        String booleanReturn = "0";

        SQLiteDatabase database = getReadableDatabase();
        String selectQuery = String.format("SELECT EXISTS (SELECT 1 FROM %s WHERE %s = %s LIMIT 1)",
                TABLE_CARS,
                KEY_CAR_ID,
                carID);
        Cursor cursor = database.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    booleanReturn = cursor.getString(0);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to check if car exists in database");
        } finally {
            if(cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return booleanReturn.equals("1");
    }
}