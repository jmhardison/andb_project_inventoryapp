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

        /**
         * Unique ID
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Product Name for entry
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME = "productname";

        /**
         * Price of product entry
         * Type: INTEGER
         */
        public final static String COLUMN_PRICE = "price";

        /**
         * Quantity of product entry that is in-stock.
         * Type: INTEGER
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * Supplier name for the product.
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_NAME = "suppliername";

        /**
         * Supplier phone number for the product.
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_PHONE = "supplierphone";
    }

}
