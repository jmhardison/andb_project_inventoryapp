package com.jonathanhardison.andb_project_inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jonathanhardison.andb_project_inventoryapp.R;

public class InventoryProvider extends ContentProvider {
    /**
     * instantiate db helper
     */
    private static InventoryDBHelper dbHelper;

    /**
     * log tag
     */
    private static final String LOG_TAG = InventoryDBHelper.class.getSimpleName();

    /**
     * inventory ids for matcher
     */
    private static final int INVENTORY = 100;
    private static final int INVENTORY_ID = 101;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //uri for content://com.jonathanhardison.andb_project_inventoryapp/inventory
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);
        //uri for content://com.jonathanhardison.andb_project_inventoryapp/inventory/#
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    /***
     * onCreate initializes the dbHelper and gets us ready.
     * @return
     */
    @Override
    public boolean onCreate() {
        //instantiate Inventory DB Helper.
        dbHelper = new InventoryDBHelper(getContext());

        return true;
    }

    /***
     * query method for inventory provider
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // get read mode for db
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Hold the cursor of query
        Cursor cursorHolder = null;

        //get type of request uri and switch for appropriate actions.
        int reqtype = sUriMatcher.match(uri);
        switch (reqtype) {
            //if type is of INVENTORY
            case INVENTORY:
                //set cursorHolder to query and return to caller after the break
                cursorHolder = db.query(
                        InventoryContract.InventoryEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            //if type is of INVENTORY_ID
            case INVENTORY_ID:
                //check for =? tog et id
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                //set cursorHolder to query and return to caller after the break
                cursorHolder = db.query(
                        InventoryContract.InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                //throw exception when unknown uri not matching any above.
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }

        cursorHolder.setNotificationUri(getContext().getContentResolver(), uri);

        return cursorHolder;
    }

    /***
     * method to return type of uri for inventory
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        //pull uri
        final int match = sUriMatcher.match(uri);
        //switch based on type of uri
        switch (match) {
            case INVENTORY:
                //return MIME type for dir
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                //return MIME type for item
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("GetType is not supported for or Unknown URI " + uri + " with match " + match);
        }
    }

    /***
     * insert method for inventory
     * @param uri
     * @param values
     * @return
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        //pull uri
        final int match = sUriMatcher.match(uri);
        //switch based on type of uri
        switch (match) {
            case INVENTORY:
                return insertInventory(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /***
     * delete inventory entry method
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // get write mode for db
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //pull uri to match
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                //delete all items matching given selection and args
                int returnVal1 = db.delete(InventoryContract.InventoryEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                //notify all listeners of change
                if (returnVal1 > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return returnVal1;
            case INVENTORY_ID:
                //delete specific item
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int returnVal2 = db.delete(InventoryContract.InventoryEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                //notify all listeners of change
                if (returnVal2 > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return returnVal2;
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }
    }

    /***
     * update inventory entry method
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        //pull uri to match
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateInventory(uri, values, selection, selectionArgs);
            case INVENTORY_ID:
                //extract id and pass
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInventory(uri, values, selection, selectionArgs);
            default:
                //throw exception for unsupported uri
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /***
     * helper method to insert Inventory item
     * @param uri
     * @param values
     * @return
     */
    private Uri insertInventory(Uri uri, ContentValues values) {
        // get write mode for db
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        /* perform validation logic */
        //check product name is not null
        String prodName = values.getAsString(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        if (prodName == null) {
            throw new IllegalArgumentException("Product name is required");
        }
        //check price is not negative
        double prodPrice = values.getAsDouble(InventoryContract.InventoryEntry.COLUMN_PRICE);
        if (prodPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        //check quantity is not negative
        int prodQuantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
        if (prodQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }


        //creation of some objects with data.
        long insertedRowId = db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);

        //if insert failed it will be -1
        if (insertedRowId == -1) {
            Log.e(LOG_TAG, getContext().getString(R.string.error_failedinsertrow) + uri);
            return null;
        }

        //notify all listeners of change
        getContext().getContentResolver().notifyChange(uri, null);

        //otherwise we have the rowID and will return it with the appendid method.
        return ContentUris.withAppendedId(uri, insertedRowId);
    }

    /***
     * helper method to update inventory item
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        /* perform validation logic */
        //first step is to see if we are checking only for quantity
        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_QUANTITY) &&
                !(values.containsKey(InventoryContract.InventoryEntry.COLUMN_PRICE) &&
                        values.containsKey(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME) &&
                        values.containsKey(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_PHONE) &&
                        values.containsKey(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME))) {
            //perform validation of only the quantity
            //check quantity is not negative
            int prodQuantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
            if (prodQuantity < 0) {
                throw new IllegalArgumentException("Quantity cannot be negative");
            }
        } else {
            //check product name is not null
            String prodName = values.getAsString(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
            if (prodName == null) {
                throw new IllegalArgumentException("Product name is required");
            }
            //check price is not negative
            double prodPrice = values.getAsDouble(InventoryContract.InventoryEntry.COLUMN_PRICE);
            if (prodPrice < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
            //check quantity is not negative
            int prodQuantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
            if (prodQuantity < 0) {
                throw new IllegalArgumentException("Quantity cannot be negative");
            }

            //if nothing in values, then don't do work.
            if (values.size() == 0) {
                return 0;
            }
        }
        // get write mode for db
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int returnVal = db.update(InventoryContract.InventoryEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        //notify all listeners of change
        if (returnVal > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnVal;

    }
}
