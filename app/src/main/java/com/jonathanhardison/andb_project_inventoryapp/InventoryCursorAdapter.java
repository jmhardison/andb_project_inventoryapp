package com.jonathanhardison.andb_project_inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

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

        //get index of columns
        int prodNameIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        int prodPriceIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
        int prodQuantityIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);

        //get real value
        String prodNameText = cursor.getString(prodNameIndex);
        String prodPriceText = String.valueOf(cursor.getInt(prodPriceIndex));
        String prodQuantityText = String.valueOf(cursor.getInt(prodQuantityIndex));

        //set view text data
        prodNameView.setText(prodNameText);
        prodPriceView.setText(prodPriceText);
        prodQuantityView.setText(prodQuantityText);
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
