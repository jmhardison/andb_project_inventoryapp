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

public class InventoryProvider extends ContentProvider {
    /** instantiate db helper */
    private InventoryDBHelper dbHelper;

    /** log tag */
    private static final String LOG_TAG = InventoryDBHelper.class.getSimpleName();

    /** inventory ids for matcher */
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
        switch(reqtype){
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
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

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

        return cursorHolder;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
