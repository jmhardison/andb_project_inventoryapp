package com.jonathanhardison.andb_project_inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.jonathanhardison.andb_project_inventoryapp.data.InventoryContract;
import com.jonathanhardison.andb_project_inventoryapp.data.InventoryDBHelper;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = InventoryDBHelper.class.getSimpleName();
    private EditText prodName;
    private EditText prodQuantity;
    private EditText prodPrice;
    private EditText suppName;
    private EditText suppPhone;
    private Uri inputUri;
    private static int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //harness the views
        prodName = findViewById(R.id.editTextProdName);
        prodPrice = findViewById(R.id.editTextPrice);
        prodQuantity = findViewById(R.id.editTextQuantity);
        suppName = findViewById(R.id.editTextSuppName);
        suppPhone = findViewById(R.id.editTextSuppPhone);

        //pull in intent extra's if there.
        //if null its a new record, if not then it's editing an existing record, which means pull it up.
        Intent intent = getIntent();
        inputUri = intent.getData();

        if(inputUri != null){

            //set title
            this.setTitle("Edit Inventory");

            //init the loader
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);


        }

    }

    /***
     * onCreateOptionsMenu sets the active menu action.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_newactivity, menu);
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
            case R.id.action_newactivity_saverecord:
                saveRecord();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     * helper method to insert new record based on data entered.
     */
    private void saveRecord() {

        //get data from items and insert record.
        ContentValues inv1 = new ContentValues();
        inv1.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, prodName.getText().toString());
        inv1.put(InventoryContract.InventoryEntry.COLUMN_PRICE, prodPrice.getText().toString()); //100 = 1.00 will use the last two digits as decimals.
        inv1.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, prodQuantity.getText().toString());
        inv1.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, suppName.getText().toString());
        inv1.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE, suppPhone.getText().toString());

        try {
            if (inputUri != null) {
                //update existing
                getContentResolver().update(inputUri, inv1, null, null);

            } else {
                //create new record
                getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, inv1);
            }
            //finishing here so that if there is an error the intent stays up.
            finish();

        } catch (Exception c) {
            //display toast of deletion and info
            Toast toastMessage = Toast.makeText(this, "Error saving record.", Toast.LENGTH_LONG);
            toastMessage.show();
            Log.e(LOG_TAG, "Error saving record. " + c.getMessage());
        }
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        //projection of items we want.
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_PRICE,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE
        };


        return new CursorLoader(this,
                inputUri,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        //set cursor position
        if(cursor.moveToFirst()) {

            //get index of columns
            int prodNameIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
            int prodPriceIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
            int prodQuantityIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
            int suppNameIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME);
            int suppPhoneIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE);

            //get real value and set edittext fields
            String prodNameText = cursor.getString(prodNameIndex);
            prodName.setText(prodNameText);

            String prodPriceText = String.format("%.2f", cursor.getDouble(prodPriceIndex));
            prodPrice.setText(prodPriceText);

            String prodQuantityText = String.valueOf(cursor.getInt(prodQuantityIndex));
            prodQuantity.setText(prodQuantityText);

            //supplier is optional so check if -1 first.
            if (suppNameIndex != -1) {
                String suppNameText = cursor.getString(suppNameIndex);
                suppName.setText(suppNameText);
            } else {
                suppName.getText().clear();
            }
            if (suppPhoneIndex != -1) {
                String suppPhoneText = cursor.getString(suppPhoneIndex);
                suppPhone.setText(suppPhoneText);
            } else {
                suppPhone.getText().clear();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //clear out data
        prodName.getText().clear();
        prodPrice.getText().clear();
        prodQuantity.getText().clear();
        suppName.getText().clear();
        suppPhone.getText().clear();
    }
}
