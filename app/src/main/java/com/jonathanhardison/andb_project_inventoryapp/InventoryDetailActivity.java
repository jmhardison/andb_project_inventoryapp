package com.jonathanhardison.andb_project_inventoryapp;

import android.app.AlertDialog;
import android.content.ContentValues;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
    private Button restockButton;
    private Button saleButton;
    private ImageButton restockSubtractQuantityButton;
    private ImageButton restockAddQuantityButton;
    private Uri inputUri;
    private static int LOADER_ID = 2;
    private static int MIN_QUANTITY_ADD = 1;
    private static int MAX_QUANTITY_ADD = 50;
    private int prodQuantityInt;
    private int restockQuantity = 1;


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
        restockButton = findViewById(R.id.buttonDetailRestock);
        saleButton = findViewById(R.id.buttonDetailSale);
        restockAddQuantityButton = findViewById(R.id.buttonDetailAddRestockAmount);
        restockSubtractQuantityButton = findViewById(R.id.buttonDetailSubtrackRestockAmount);

        //pull in intent extra's if there.
        //if null its a new record, if not then it's editing an existing record, which means pull it up.
        Intent intent = getIntent();
        inputUri = intent.getData();

        if (inputUri != null) {
            //init the loader
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        } else {
            //pop toast and finish
            Toast toastMessage = Toast.makeText(this, R.string.error_loadinginventory, Toast.LENGTH_LONG);
            toastMessage.show();
            finish();
        }

        //change the amount to default
        restockButton.setText(getString(R.string.activityxml_button_restock, restockQuantity));

        //add onclick listener to adjust quantity to restock
        restockAddQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(restockQuantity < MAX_QUANTITY_ADD && restockQuantity >= MIN_QUANTITY_ADD){
                    restockQuantity++;
                    restockButton.setText(getString(R.string.activityxml_button_restock, restockQuantity));
                }
            }
        });

        //subtract onclick listener to adjust quantity to restock
        restockSubtractQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(restockQuantity <= MAX_QUANTITY_ADD && restockQuantity > MIN_QUANTITY_ADD){
                    restockQuantity--;
                    restockButton.setText(getString(R.string.activityxml_button_restock, restockQuantity));
                }
            }
        });

        //bind button click for the sale
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //decrement the quantity if above 0.
                //create new values
                if (prodQuantityInt > 0) {
                    int newQuantity = (prodQuantityInt - 1);
                    ContentValues newVal = new ContentValues();
                    newVal.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, newQuantity);

                    //now that we have the values of quantity, send it to the update method.
                    int updated = InventoryDetailActivity.this.getContentResolver().update(inputUri, newVal, null, null);
                    if (updated > 0) {
                        //generate toast
                        Toast toastMessage = Toast.makeText(InventoryDetailActivity.this, R.string.general_toast_solditem, Toast.LENGTH_SHORT);
                        toastMessage.show();
                    }

                } else {
                    //show toast saying they need to order
                    Toast toastMessage = Toast.makeText(InventoryDetailActivity.this, R.string.general_toast_outofstock, Toast.LENGTH_LONG);
                    toastMessage.show();
                }

            }
        });

        //bind button click for the sale
        restockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //restock the quantity if within bounds
                //create new values
                if (prodQuantityInt >= 0) {
                    int newQuantity = (prodQuantityInt + restockQuantity);
                    ContentValues newVal = new ContentValues();
                    newVal.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, newQuantity);

                    //now that we have the values of quantity, send it to the update method.
                    int updated = InventoryDetailActivity.this.getContentResolver().update(inputUri, newVal, null, null);
                    if (updated > 0) {
                        //generate toast
                        Toast toastMessage = Toast.makeText(InventoryDetailActivity.this, R.string.general_toast_restockitem, Toast.LENGTH_SHORT);
                        toastMessage.show();
                    }

                } else {
                    //show toast saying they need to order
                    Toast toastMessage = Toast.makeText(InventoryDetailActivity.this, R.string.error_restocking, Toast.LENGTH_LONG);
                    toastMessage.show();
                }

            }
        });
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

            String prodPriceText = String.format(Locale.ENGLISH, getString(R.string.general_priceformat), cursor.getDouble(prodPriceIndex));
            prodPrice.setText(prodPriceText);

            prodQuantityInt = cursor.getInt(prodQuantityIndex);
            String prodQuantityText = String.valueOf(prodQuantityInt);
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
        prodPrice.setText("");
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
        switch (item.getItemId()) {
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
                dialPhone();
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
                Toast toastMessage = Toast.makeText(this, R.string.general_toast_inventoryitemdeleted, Toast.LENGTH_LONG);
                toastMessage.show();

                //finishing here so that if there is an error the intent stays up.
                finish();

            } catch (Exception c) {
                //display toast of deletion and info don't call finish so the dialog remains open
                Toast toastMessage = Toast.makeText(this, R.string.error_deletingrecord, Toast.LENGTH_LONG);
                toastMessage.show();
                Log.e(LOG_TAG, R.string.error_deletingrecord + c.getMessage());
            }
        } else {
            //in theory should not hit this safety message as the option is hidden until it's an existing record.
            Toast toastMessage = Toast.makeText(this, R.string.error_deletingrecord, Toast.LENGTH_LONG);
            toastMessage.show();
        }
    }


    /***
     * method for handling delete confirmation
     */
    private void showDeleteConfirmation() {
        //create dialog for handling deletion confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.general_deleteinventoryitem_message);
        builder.setPositiveButton(R.string.general_deleteinventoryitem_positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // delete selected, commit action
                deleteRecord();
            }
        });
        builder.setNegativeButton(R.string.general_deleteinventoryitem_negative, new DialogInterface.OnClickListener() {
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

    /***
     * helper method to start phone intent
     */
    private void dialPhone(){
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
        phoneIntent.setData(Uri.parse(R.string.general_phoneintent_prependtype + suppPhone.getText().toString()));
        if (phoneIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(phoneIntent);
        }
        else{
            //display toast of deletion and info don't call finish so the dialog remains open
            Toast toastMessage = Toast.makeText(this, R.string.error_callingvendor, Toast.LENGTH_LONG);
            toastMessage.show();
        }
    }

}
