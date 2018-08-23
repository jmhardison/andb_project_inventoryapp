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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jonathanhardison.andb_project_inventoryapp.data.InventoryContract;
import com.jonathanhardison.andb_project_inventoryapp.data.InventoryDBHelper;

import java.util.Locale;

/***
 * EditActivity is used for both edit and new inventory items.
 */
public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = InventoryDBHelper.class.getSimpleName();
    private EditText prodName;
    private EditText prodQuantity;
    private EditText prodPrice;
    private EditText suppName;
    private EditText suppPhone;
    private Uri inputUri;
    private static int LOADER_ID = 1;
    private boolean changesMade = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //harness the views
        prodName = findViewById(R.id.editTextProdName);
        prodPrice = findViewById(R.id.editTextPrice);
        prodQuantity = findViewById(R.id.editTextQuantity);
        suppName = findViewById(R.id.editTextSuppName);
        suppPhone = findViewById(R.id.editTextSuppPhone);

        //set touch listener
        prodName.setOnTouchListener(changesMadeListener);
        prodPrice.setOnTouchListener(changesMadeListener);
        prodQuantity.setOnTouchListener(changesMadeListener);
        suppName.setOnTouchListener(changesMadeListener);
        suppPhone.setOnTouchListener(changesMadeListener);


        //pull in intent extra's if there.
        //if null its a new record, if not then it's editing an existing record, which means pull it up.
        Intent intent = getIntent();
        inputUri = intent.getData();

        if (inputUri != null) {

            //set title
            this.setTitle("Edit Inventory");

            //init the loader
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);

            //invalidate options menu to change the menu to "edit menu"
            invalidateOptionsMenu();

        } else {
            this.setTitle("Add Inventory");
        }

    }

    /***
     * method that changes the active menu
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (inputUri == null) {
            //get menu items and then set to be invisible.
            MenuItem menuOrderItem = menu.findItem(R.id.action_editactivity_ordersupply);
            menuOrderItem.setVisible(false);
        }
        return true;
    }

    /***
     * onCreateOptionsMenu sets the active menu action.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editactivity, menu);
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
            case R.id.action_editactivity_ordersupply:
                //call supplier
                break;
            case android.R.id.home:
                if (!changesMade) {
                    finish();
                } else {
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // discarded
                                    finish();
                                }
                            };

                    //show dialog
                    showUnsavedChanges(discardButtonClickListener);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /***
     * helper method to insert new record based on data entered.
     */
    private void saveRecord() {

        //if everything is empty then the user must have tried to save in error
        if (TextUtils.isEmpty(prodName.getText().toString()) && TextUtils.isEmpty(prodPrice.getText().toString()) && TextUtils.isEmpty(prodQuantity.getText().toString()) &&
                TextUtils.isEmpty(suppName.getText().toString()) && TextUtils.isEmpty(suppPhone.getText().toString())) {
            Toast toastMessage = Toast.makeText(this, "You must provide some info to save.", Toast.LENGTH_LONG);
            toastMessage.show();
        } else {
            //get data from items and insert record.
            ContentValues inv1 = new ContentValues();
            inv1.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, prodName.getText().toString());

            //check if price is null and set it as 0 instead
            if (TextUtils.isEmpty(prodPrice.getText().toString())) {
                inv1.put(InventoryContract.InventoryEntry.COLUMN_PRICE, "0"); //100 = 1.00 will use the last two digits as decimals.
            } else {
                inv1.put(InventoryContract.InventoryEntry.COLUMN_PRICE, prodPrice.getText().toString()); //100 = 1.00 will use the last two digits as decimals.
            }

            //check if quantity is null and set it as 0 instead.
            if (TextUtils.isEmpty(prodPrice.getText().toString())) {
                inv1.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, "0");
            } else {
                inv1.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, prodQuantity.getText().toString());
            }

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
    }


    /***
     * method to handle create of loader and define the projection and get cursor.
     * @param i
     * @param bundle
     * @return
     */
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

    /***
     * method to handle when load is finished and assigning values to views.
     * @param loader
     * @param cursor
     */
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

            String prodPriceText = String.format(Locale.ENGLISH, "%.2f", cursor.getDouble(prodPriceIndex));
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

    /***
     * method called on reset, clear out text fields.
     * @param loader
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //clear out data
        prodName.getText().clear();
        prodPrice.getText().clear();
        prodQuantity.getText().clear();
        suppName.getText().clear();
        suppPhone.getText().clear();
    }


    /***
     * handler for touch events on view to mark that changes were made or attempted
     */
    private View.OnTouchListener changesMadeListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            changesMade = true;
            return false;
        }
    };


    /***
     * method to handle showing unsaved changes dialog
     * @param discardButtonClickListener
     */
    private void showUnsavedChanges(
            DialogInterface.OnClickListener discardButtonClickListener) {

        //create the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard changes and return to list?");
        builder.setPositiveButton("DISCARD", discardButtonClickListener);
        builder.setNegativeButton("KEEP EDITING", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //if user did not click discard, stay and close this dialog.
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
     * method for handling back pressed and determining if unsaved changes exist.
     */
    @Override
    public void onBackPressed() {
        //no changes, move on with back
        if (!changesMade) {
            super.onBackPressed();
            return;
        }

        // Changes in progress should be saved, warn the user.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //discarded so close intent
                        finish();
                    }
                };


        showUnsavedChanges(discardButtonClickListener);
    }
}
