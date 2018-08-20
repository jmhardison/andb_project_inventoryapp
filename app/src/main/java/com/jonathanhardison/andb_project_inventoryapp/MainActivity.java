package com.jonathanhardison.andb_project_inventoryapp;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

import com.jonathanhardison.andb_project_inventoryapp.data.DataOperations;
import com.jonathanhardison.andb_project_inventoryapp.data.InventoryContract;
import com.jonathanhardison.andb_project_inventoryapp.data.InventoryDBHelper;
import com.jonathanhardison.andb_project_inventoryapp.data.InventoryProvider;

public class MainActivity extends AppCompatActivity {

    /** log tag */
    private static final String LOG_TAG = InventoryDBHelper.class.getSimpleName();
    /** database operations */
    private DataOperations dbOps;
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

        //instantiate db operations.
        dbOps = new DataOperations(this);

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
        switch(item.getItemId()){
            case R.id.action_create_sample_data:
                //insert call to create sample data.
                insertSampleData();
                break;
            case R.id.action_delete_data:
                //insert call to delete all data
                dbOps.deleteAllData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    /***
     * insertSampleData creates a batch of sample items in the database and logs their creation.
     */
    private void insertSampleData(){

        //insert a few items of sample data.
        dbOps.insertInventoryItem("PixelBlaster", 100, 9, "Target", "111-111-1111");
        dbOps.insertInventoryItem("Earbuds", 132, 3, "Ingram Micro", "111-111-1121");
        dbOps.insertInventoryItem("MightySqueegie", 300, 2, "Squeegie Store", "111-111-1113");
        dbOps.insertInventoryItem("Paper", 1, 900, "Forest Supply", "111-111-2222");

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
//keep changing and changing


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
