package com.jonathanhardison.andb_project_inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jonathanhardison.andb_project_inventoryapp.data.InventoryContract;
import com.jonathanhardison.andb_project_inventoryapp.data.InventoryDBHelper;
import com.jonathanhardison.andb_project_inventoryapp.data.InventoryProvider;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * log tag
     */
    private static final String LOG_TAG = InventoryDBHelper.class.getSimpleName();
    private static int LOADER_ID = 0;
    private ProgressBar progressBarView;
    private InventoryCursorAdapter customAdapter;
    private ListView listView;
    private View emptyView;
    private FloatingActionButton fabInventoryAdd;

    /***
     * onStart method to handle additional query functions.
     */
    @Override
    protected void onStart() {
        super.onStart();
        progressBarView.setVisibility(View.INVISIBLE);
    }

    /***
     * onCreate method called on activity creation. General setup actions.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //pull view references
        progressBarView = findViewById(R.id.indeterminateBar);
        emptyView = findViewById(R.id.empty_view);
        listView = findViewById(R.id.list);
        fabInventoryAdd = findViewById(R.id.fabActionButton);

        //set empty view on listview.
        listView.setEmptyView(emptyView);

        //set onclick for fab
        fabInventoryAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: setup onclick to insert new item into inventory
                Intent addInvIntent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(addInvIntent);
            }
        });

        //set up onclick for the list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //create uri of item
                Uri invURI = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);
                //setup intent and load extras
                Intent detailInvIntent = new Intent(MainActivity.this, InventoryDetailActivity.class);
                detailInvIntent.setData(invURI);
                startActivity(detailInvIntent);
            }
        });

        //setup custom adapter and attach to listView
        customAdapter = new InventoryCursorAdapter(this, null);
        listView.setAdapter(customAdapter);

        //init the loader
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
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
        //delete all from table.
        int deletedRows = getContentResolver().delete(InventoryContract.InventoryEntry.CONTENT_URI,
                null,
                null);

        //display toast of deletion and info
        Toast toastMessage = Toast.makeText(this, deletedRows + getString(R.string.mainactivity_toast_deletedfrominventory), Toast.LENGTH_LONG);
        toastMessage.show();
    }

    /***
     * insertSampleData creates a batch of sample items in the database and logs their creation.
     */
    private void insertSampleData() {

        //insert a few items of sample data.
        //creation of some objects with data.
        ContentValues inv1 = new ContentValues();
        inv1.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, getString(R.string.sampledata_product1_name));
        inv1.put(InventoryContract.InventoryEntry.COLUMN_PRICE, 100.19); //100 = 1.00 will use the last two digits as decimals.
        inv1.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, 9);
        inv1.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, getString(R.string.sampledata_product1_suppliername));
        inv1.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE, getString(R.string.sampledata_product1_supplierphone));

        //creation of some objects with data.
        ContentValues inv2 = new ContentValues();
        inv2.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, getString(R.string.sampledata_product2_name));
        inv2.put(InventoryContract.InventoryEntry.COLUMN_PRICE, 132); //100 = 1.00 will use the last two digits as decimals.
        inv2.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, 3);
        inv2.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, getString(R.string.sampledata_product2_suppliername));
        inv2.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE, getString(R.string.sampledata_product2_supplierphone));

        Uri holder1 = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, inv1);
        Uri holder2 = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, inv2);

    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        //projection of items we want.
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_PRICE
        };

        return new CursorLoader(this,
                InventoryContract.InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        customAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        customAdapter.swapCursor(null);
    }
}
