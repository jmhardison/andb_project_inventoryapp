package com.jonathanhardison.andb_project_inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Contract for Inventory App
 */
public class InventoryContract {

    // empty constructor
    private InventoryContract() {
    }

    //content authority
    public static final String CONTENT_AUTHORITY = "com.jonathanhardison.andb_project_inventoryapp";
    //base extension
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //path for table
    public static final String PATH_INVENTORY = "inventory";


    public static final class InventoryEntry implements BaseColumns {
        //uri for access to provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        //mime types
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * Name of db table
         */
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
         * Type: DOUBLE (REAL)
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
