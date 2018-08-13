package com.jonathanhardison.andb_project_inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Contract for Inventory App
 */
public class InventoryContract {

    // empty constructor
    private InventoryContract(){}

    public static final class InventoryEntry implements BaseColumns{

        /** Name of db table */
        public final static String TABLE_NAME = "inventory";

        //entry id
        public final static String _ID = BaseColumns._ID;

        //product name
        public final static String COLUMN_PRODUCT_NAME = "productname";

        //price
        public final static String COLUMN_PRICE = "price";

        //quantity
        public final static String COLUMN_QUANTITY = "quantity";

        //supplier name
        public final static String COLUMN_SUPPLIER_NAME = "suppliername";

        //supplier phone number
        public final static String COLUMN_SUPPLIER_PHONE = "supplierphone";
    }

}
