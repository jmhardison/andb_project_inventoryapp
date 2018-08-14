package com.jonathanhardison.andb_project_inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataOperations {
    /** instantiate db helper */
    private InventoryDBHelper dbHelper;
    /** log tag */
    public static final String LOG_TAG = InventoryDBHelper.class.getSimpleName();

    /***
     * Instantiate DataOperations class with context.
     * @param context
     */
    public DataOperations(Context context) {
        super();
        dbHelper = new InventoryDBHelper(context);
    }

    /***
     * deleteAllData removes all entries from the database.
     */
    public void deleteAllData(){
        // get write mode for db
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //pre-row count
        long rowCountBefore = DatabaseUtils.queryNumEntries(db, InventoryContract.InventoryEntry.TABLE_NAME);
        //delete all from table.
        db.execSQL("delete from "+ InventoryContract.InventoryEntry.TABLE_NAME);
        //post-row count
        long rowCountAfter = DatabaseUtils.queryNumEntries(db, InventoryContract.InventoryEntry.TABLE_NAME);

        //log the details to log file.
        Log.i(LOG_TAG, "Row count before delete: "+ rowCountBefore + " Row count after delete: " + rowCountAfter);
    }


    /***
     * Inserts item as inventory entry.
     * @param inProductName
     * @param inPrice
     * @param inQuantity
     * @param inSupplierName
     * @param inSupplierPhone
     */
    public long insertInventoryItem(String inProductName, int inPrice, int inQuantity, String inSupplierName, String inSupplierPhone){
        // get write mode for db
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //creation of some objects with data.
        ContentValues inv1 = new ContentValues();
        inv1.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, inProductName);
        inv1.put(InventoryContract.InventoryEntry.COLUMN_PRICE, inPrice); //100 = 1.00 will use the last two digits as decimals.
        inv1.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, inQuantity);
        inv1.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, inSupplierName);
        inv1.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE, inSupplierPhone);
        long insertedRowId = db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, inv1);
        Log.i(LOG_TAG, "Created inventory entry with ID: "+ insertedRowId);

        return insertedRowId;
    }

    public Cursor getIventory(){
        // get write mode for db
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //projection of items we want.
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRICE,
                InventoryContract.InventoryEntry.COLUMN_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE
        };

        Cursor cursor = db.query(
                InventoryContract.InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        return cursor;

    }

}
