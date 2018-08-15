package com.jonathanhardison.andb_project_inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jonathanhardison.andb_project_inventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryDBHelper extends SQLiteOpenHelper {

    /** db file name */
    private static final String DB_NAME = "inventory.db";

    /** db schema version. */
    private static final int DB_VERSION = 1;

    /***
     * InventoryDBHelper constructor
     * @param context
     */
    public InventoryDBHelper(Context context) {super(context, DB_NAME, null, DB_VERSION);}

    /***
     * onCreate method to execute sql query and create db table and schema.
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // String to create table for inventory. Defaults for 0 are used for integers.
        String SQL_CREATE_INVENTORY_TABLE =  "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_SUPPLIER_NAME + " TEXT, "
                + InventoryEntry.COLUMN_SUPPLIER_PHONE + " TEXT);";

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    /***
     * onUpgrade handles upgrade tasks when needed.
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //no upgrades yet.
    }
}
