package com.jonathanhardison.andb_project_inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

import com.jonathanhardison.andb_project_inventoryapp.data.InventoryContract;
import com.jonathanhardison.andb_project_inventoryapp.data.InventoryDBHelper;
import com.jonathanhardison.andb_project_inventoryapp.data.InventoryProvider;

public class MainActivity extends AppCompatActivity {

    /** log tag */
    private static final String LOG_TAG = InventoryDBHelper.class.getSimpleName();
    /** inventory provider */
    private InventoryProvider invProvider;


    /***
     * onCreate method called on activity creation. General setup actions.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiate inventory provider.
        invProvider = new InventoryProvider();
    }


    /***
     * onCreateOptionsMenu sets the active menu action.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mainactivity, menu);
        return true;
    }

    /***
     * onOptionsItemSelected handles when a menu option is selected.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_sample_data:
                insertSampleData();
                break;
            case R.id.action_delete_data:
                deleteAllData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     * helper method to delete all method from menu action.
     */
    private void deleteAllData() {
        //pre-row count
        Cursor holderPre = getContentResolver().query(InventoryContract.InventoryEntry.CONTENT_URI, null, null, null, null);
        long rowCountBefore = holderPre.getCount();
        holderPre.close();

        //delete all from table.
        int deletedRows = getContentResolver().delete(InventoryContract.InventoryEntry.CONTENT_URI,
                null,
                null);
        //post-row count
        Cursor holderPost = getContentResolver().query(InventoryContract.InventoryEntry.CONTENT_URI, null, null, null, null);
        long rowCountAfter = holderPost.getCount();
        holderPost.close();

        //log the details to log file.
        Log.i(LOG_TAG, "Row count before delete: " + rowCountBefore + " Row count after delete: " + rowCountAfter + " Total Rows Deleted: " + deletedRows);
    }

    /***
     * insertSampleData creates a batch of sample items in the database and logs their creation.
     */
    private void insertSampleData(){

        //insert a few items of sample data.
        //creation of some objects with data.
        ContentValues inv1 = new ContentValues();
        inv1.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, "PixelBlaster");
        inv1.put(InventoryContract.InventoryEntry.COLUMN_PRICE, 100); //100 = 1.00 will use the last two digits as decimals.
        inv1.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, 9);
        inv1.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, "Target");
        inv1.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE, "111-111-1111");

        //creation of some objects with data.
        ContentValues inv2 = new ContentValues();
        inv2.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, "Earbuds");
        inv2.put(InventoryContract.InventoryEntry.COLUMN_PRICE, 132); //100 = 1.00 will use the last two digits as decimals.
        inv2.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, 3);
        inv2.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, "Ingram Micro");
        inv2.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE, "111-111-1121");

        Uri holder1 = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, inv1);
        Uri holder2 = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, inv2);


        //read the items back out and log them.
        readAllDataAndLog();
    }

    /***
     * reads all rows and logs them. This is part of the temp read method construct. uses getInventory that returns a cursor.
     */
    private void readAllDataAndLog(){

        //projection of items we want.
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRICE,
                InventoryContract.InventoryEntry.COLUMN_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE
        };

        //get cursor
        Cursor holder = getContentResolver().query(InventoryContract.InventoryEntry.CONTENT_URI, projection, null, null, null);


        try {
            //get column index info
            int indexID = holder.getColumnIndex(InventoryContract.InventoryEntry._ID);
            int indexProductName = holder.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
            int indexPrice = holder.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
            int indexQuantity = holder.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
            int indexSupplierName = holder.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME);
            int indexSupplierPhone = holder.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE);

            //pass over items and log current (c) item.
            while (holder.moveToNext()) {
                int cID = holder.getInt(indexID);
                String cProductName = holder.getString(indexProductName);
                int cPrice = holder.getInt(indexPrice);
                int cQuantity = holder.getInt(indexQuantity);
                String cSupplierName = holder.getString(indexSupplierName);
                String cSupplierPhone = holder.getString(indexSupplierPhone);

                Log.i(LOG_TAG, "ID: " + cID + " Product Name: " + cProductName + " Price: " + cPrice + " Quantity: " + cQuantity + " Supplier Name: " + cSupplierName + " Supplier Phone: " + cSupplierPhone);
            }
        }
        finally {
            holder.close();
        }

    }

}
