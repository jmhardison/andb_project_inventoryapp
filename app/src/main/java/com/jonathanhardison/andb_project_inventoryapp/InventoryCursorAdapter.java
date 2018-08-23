package com.jonathanhardison.andb_project_inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jonathanhardison.andb_project_inventoryapp.data.InventoryContract;

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
    public void bindView(View view, Context context, Cursor cursor) {
        //pull views
        TextView prodNameView = view.findViewById(R.id.name);
        TextView prodPriceView = view.findViewById(R.id.price);
        TextView prodQuantityView = view.findViewById(R.id.quantity);
        Button saleButton = view.findViewById(R.id.buttonSale);

        //get index of columns
        int prodNameIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        int prodPriceIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
        int prodQuantityIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);

        //get real value
        String prodNameText = cursor.getString(prodNameIndex);
        String prodPriceText = "Price: $" + String.format("%.2f", cursor.getDouble(prodPriceIndex));
        String prodQuantityText = "Quantity: " + String.valueOf(cursor.getInt(prodQuantityIndex));

        //set view text data
        prodNameView.setText(prodNameText);
        prodPriceView.setText(prodPriceText);
        prodQuantityView.setText(prodQuantityText);


        //bind button click for the sale
        saleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //need a method to decrement against the db.
                //maybe an update where I submit the args and where??
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
