package com.jonathanhardison.andb_project_inventoryapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.jonathanhardison.andb_project_inventoryapp.data.InventoryContract;
import com.jonathanhardison.andb_project_inventoryapp.data.InventoryDBHelper;

import java.util.Locale;

public class InventoryDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = InventoryDBHelper.class.getSimpleName();
    private TextView prodName;
    private TextView prodQuantity;
    private TextView prodPrice;
    private TextView suppName;
    private TextView suppPhone;
    private Uri inputUri;
    private static int LOADER_ID = 2;


    /***
     * onCreate method to handle setup of activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //harness the views
        prodName = findViewById(R.id.detailTextProdName);
        prodPrice = findViewById(R.id.detailTextPrice);
        prodQuantity = findViewById(R.id.detailTextQuantity);
        suppName = findViewById(R.id.detailTextSuppName);
        suppPhone = findViewById(R.id.detailTextSuppPhone);

        //pull in intent extra's if there.
        //if null its a new record, if not then it's editing an existing record, which means pull it up.
        Intent intent = getIntent();
        inputUri = intent.getData();

        if (inputUri != null) {
            //init the loader
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        } else {
            //pop toast and finish
            Toast toastMessage = Toast.makeText(this, "Error loading inventory item.", Toast.LENGTH_LONG);
            toastMessage.show();
            finish();
        }
    }

    /***
     * onCreateOptionsMenu sets up the menu for this activity.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detailactivity, menu);
        return true;
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
        if (cursor.moveToFirst()) {

            //get index of columns
            int prodNameIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
            int prodPriceIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
            int prodQuantityIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
            int suppNameIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME);
            int suppPhoneIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE);

            //get real value and set edittext fields
            String prodNameText = cursor.getString(prodNameIndex);
            prodName.setText(prodNameText);

            String prodPriceText = String.format(Locale.ENGLISH,"%.2f", cursor.getDouble(prodPriceIndex));
            prodPrice.setText(prodPriceText);

            String prodQuantityText = String.valueOf(cursor.getInt(prodQuantityIndex));
            prodQuantity.setText(prodQuantityText);

            //supplier is optional so check if -1 first.
            if (suppNameIndex != -1) {
                String suppNameText = cursor.getString(suppNameIndex);
                suppName.setText(suppNameText);
            } else {
                suppName.setText("");
            }
            if (suppPhoneIndex != -1) {
                String suppPhoneText = cursor.getString(suppPhoneIndex);
                suppPhone.setText(suppPhoneText);
            } else {
                suppPhone.setText("");
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //clear out data
        prodName.setText("");
        prodPrice.setText("");;
        prodQuantity.setText("");
        suppName.setText("");
        suppPhone.setText("");
    }

    /***
     * onOptionsItemSelected handles the menu item selection process.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_detailactivity_deleterecord:
                //delete the current record.
                showDeleteConfirmation();
                break;
            case R.id.action_detailactivity_editrecord:
                //create new intent and send it off. Passing the data payload in through the process.
                //setup intent and load extras
                Intent editInvIntent = new Intent(InventoryDetailActivity.this, EditActivity.class);
                editInvIntent.setData(inputUri);
                startActivity(editInvIntent);
                break;
            case R.id.action_detailactivity_ordersupply:
                //perform intent that launches the phone app
                break;
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }


    /***
     * helper method to delete the current record
     */
    private void deleteRecord() {
        if (inputUri != null) {
            //deletes the current record
            try {

                //create new record
                getContentResolver().delete(inputUri, null, null);
                Toast toastMessage = Toast.makeText(this, "Inventory item deleted.", Toast.LENGTH_LONG);
                toastMessage.show();

                //finishing here so that if there is an error the intent stays up.
                finish();

            } catch (Exception c) {
                //display toast of deletion and info don't call finish so the dialog remains open
                Toast toastMessage = Toast.makeText(this, "Error deleting record.", Toast.LENGTH_LONG);
                toastMessage.show();
                Log.e(LOG_TAG, "Error deleting record. " + c.getMessage());
            }
        } else {
            //in theory should not hit this safety message as the option is hidden until it's an existing record.
            Toast toastMessage = Toast.makeText(this, "Error deleting record.", Toast.LENGTH_LONG);
            toastMessage.show();
        }
    }


    /***
     * method for handling delete confirmation
     */
    private void showDeleteConfirmation() {
        //create dialog for handling deletion confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this inventory item?");
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // delete selected, commit action
                deleteRecord();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
