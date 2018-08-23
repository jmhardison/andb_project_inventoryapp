package com.jonathanhardison.andb_project_inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jonathanhardison.andb_project_inventoryapp.data.InventoryContract;

import java.util.Locale;

public class InventoryCursorAdapter extends CursorAdapter {
    /***
     * instantiate
     * @param context
     * @param c
     */
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /***
     * method to set the views contents
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        //pull views
        TextView prodNameView = view.findViewById(R.id.name);
        TextView prodPriceView = view.findViewById(R.id.price);
        TextView prodQuantityView = view.findViewById(R.id.quantity);
        Button saleButton = view.findViewById(R.id.buttonSale);

        //get index of columns
        int prodNameIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        int prodPriceIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
        int prodQuantityIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
        //get int for id index
        int prodIDIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);

        //get real value
        //quantity is pulled as an int first to be used in button action.
        final int prodQuantityInt = cursor.getInt(prodQuantityIndex);
        final Uri currentUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, cursor.getInt(prodIDIndex));

        String prodNameText = cursor.getString(prodNameIndex);
        String prodPriceText = context.getString(R.string.general_priceprepend) + String.format(Locale.ENGLISH, context.getString(R.string.general_priceformat), cursor.getDouble(prodPriceIndex));
        String prodQuantityText = context.getString(R.string.general_quantityprepend) + String.valueOf(prodQuantityInt);

        //set view text data
        prodNameView.setText(prodNameText);
        prodPriceView.setText(prodPriceText);
        prodQuantityView.setText(prodQuantityText);


        //bind button click for the sale
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need a method to decrement against the db.
                //maybe an update where I submit the args and where??

                //create new values
                if (prodQuantityInt > 0) {
                    int newQuantity = (prodQuantityInt - 1);
                    ContentValues newVal = new ContentValues();
                    newVal.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, newQuantity);

                    //now that we have the values of quantity, send it to the update method.
                    int updated = context.getContentResolver().update(currentUri, newVal, null, null);
                    if (updated > 0) {
                        //generate toast
                        Toast toastMessage = Toast.makeText(context, R.string.general_toast_solditem, Toast.LENGTH_SHORT);
                        toastMessage.show();
                    }

                } else {
                    //show toast saying they need to order
                    Toast toastMessage = Toast.makeText(context, R.string.general_toast_outofstock, Toast.LENGTH_LONG);
                    toastMessage.show();
                }

            }
        });
    }

    /***
     * returns new list_item view
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

}
