package com.jonathanhardison.andb_project_inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataOperations {
    /** instantiate db helper */
    private final InventoryDBHelper dbHelper;
    /** log tag */
    private static final String LOG_TAG = InventoryDBHelper.class.getSimpleName();

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



}
