package com.test.carweather.provider;

import java.util.HashMap;

import com.test.carweather.utils.MyLogConfig;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class CarWeatherProvider extends ContentProvider {
    private static final String TAG = "CarWeatherProvider";
    private static final boolean DEBUG = MyLogConfig.DEBUG;

    /**
     * The database that the provider uses as its underlying data store
     */
    private static final String DATABASE_NAME = "carweather.db";

    /**
     * The database version
     */
    private static final int DATABASE_VERSION = 2;

    /**
     * A projection map used to select columns from the database
     */
    private static HashMap<String, String> sCitiesProjectionMap;

    /*
     * Constants used by the Uri matcher to choose an action based on the
     * pattern of the incoming URI
     */
    // The incoming URI matches the Notes URI pattern
    private static final int CITIES = 1;

    // The incoming URI matches the Note ID URI pattern
    private static final int CITY_ID = 2;

    /**
     * A UriMatcher instance
     */
    private static final UriMatcher sUriMatcher;

    // Handle to a new DatabaseHelper.
    private DatabaseHelper mOpenHelper;

    /**
     * A block that instantiates and sets static objects
     */
    static {

        /*
         * Creates and initializes the URI matcher
         */
        // Create a new instance
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Add a pattern that routes URIs terminated with "cities" to a CITIES
        // operation
        sUriMatcher.addURI(CarWeather.AUTHORITY, "cities", CITIES);

        // Add a pattern that routes URIs terminated with "cities" plus an
        // integer
        // to a city ID operation
        sUriMatcher.addURI(CarWeather.AUTHORITY, "cities/#", CITY_ID);

        // Creates a new projection map instance. The map returns a column name
        // given a string. The two are usually equal.
        sCitiesProjectionMap = new HashMap<String, String>();

        // Maps the string "_ID" to the column name "_ID"
        sCitiesProjectionMap.put(CarWeather.Cities._ID, CarWeather.Cities._ID);

        // Maps "province" to "province"
        sCitiesProjectionMap.put(CarWeather.Cities.COLUMN_NAME_PROVINCE,
                CarWeather.Cities.COLUMN_NAME_PROVINCE);

        // Maps "city" to "city"
        sCitiesProjectionMap.put(CarWeather.Cities.COLUMN_NAME_CITY,
                CarWeather.Cities.COLUMN_NAME_CITY);

        // Maps "district" to "district"
        sCitiesProjectionMap.put(CarWeather.Cities.COLUMN_NAME_DISTRICT,
                CarWeather.Cities.COLUMN_NAME_DISTRICT);

        // Maps "polyphone" to "polyphone"
        sCitiesProjectionMap.put(CarWeather.Cities.COLUMN_NAME_POLYPHONE,
                CarWeather.Cities.COLUMN_NAME_POLYPHONE);

    }

    /**
     *
     * This class helps open, create, and upgrade the database file. Set to
     * package visibility for testing purposes.
     */
    static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {

            // calls the super constructor, requesting the default cursor
            // factory.
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         *
         * Creates the underlying database with table name and column names
         * taken from the CarWeather class.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            if (DEBUG) { Log.d(TAG, "databaseHelper onCreate(db) called"); }
            db.execSQL("CREATE TABLE IF NOT EXISTS " + CarWeather.Cities.TABLE_NAME + " ("
                    + CarWeather.Cities._ID + " INTEGER PRIMARY KEY,"
                    + CarWeather.Cities.COLUMN_NAME_PROVINCE + " TEXT,"
                    + CarWeather.Cities.COLUMN_NAME_CITY + " TEXT,"
                    + CarWeather.Cities.COLUMN_NAME_DISTRICT + " TEXT,"
                    + CarWeather.Cities.COLUMN_NAME_POLYPHONE + " TEXT"+ ");");
        }

        /**
         *
         * Demonstrates that the provider must consider what happens when the
         * underlying datastore is changed. In this sample, the database is
         * upgraded the database by destroying the existing data. A real
         * application should upgrade the database in place.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            // Logs that the database is being upgraded
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

            // Kills the table and existing data
            db.execSQL("DROP TABLE IF EXISTS cities");

            // Recreates the database with a new version
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        if (DEBUG) { Log.d(TAG, "provider onCreate called"); }
        // Creates a new helper object. Note that the database itself isn't
        // opened until
        // something tries to access it, and it's only created if it doesn't
        // already exist.
        mOpenHelper = new DatabaseHelper(getContext());

        // Assumes that any failures will be reported by a thrown exception.
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        if (DEBUG) { Log.d(TAG, "provider query called"); }

        // Constructs a new query builder and sets its table name
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CarWeather.Cities.TABLE_NAME);

        /**
         * Choose the projection and adjust the "where" clause based on URI
         * pattern-matching.
         */
        switch (sUriMatcher.match(uri)) {
        // If the incoming URI is for cities, chooses the Notes projection
        case CITIES:
            qb.setProjectionMap(sCitiesProjectionMap);
            break;

        /*
         * If the incoming URI is for a single city identified by its ID,
         * chooses the city ID projection, and appends "_ID = <cityID>" to the
         * where clause, so that it selects that single city
         */
        case CITY_ID:
            qb.setProjectionMap(sCitiesProjectionMap);
            qb.appendWhere(CarWeather.Cities._ID + // the name of the ID column
                    "=" +
                    // the position of the city ID itself in the incoming URI
                    uri.getPathSegments().get(
                            CarWeather.Cities.CITY_ID_PATH_POSITION));
            break;

        default:
            // If the URI doesn't match any of the known patterns, throw an
            // exception.
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy;
        // If no sort order is specified, uses the default
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = CarWeather.Cities.DEFAULT_SORT_ORDER;
        } else {
            // otherwise, uses the incoming sort order
            orderBy = sortOrder;
        }

        // Opens the database object in "read" mode, since no writes need to be
        // done.
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        /*
         * Performs the query. If no problems occur trying to read the database,
         * then a Cursor object is returned; otherwise, the cursor variable
         * contains null. If no records were selected, then the Cursor object is
         * empty, and Cursor.getCount() returns 0.
         */
        Cursor c = qb.query(db, // The database to query
                projection, // The columns to return from the query
                selection, // The columns for the where clause
                selectionArgs, // The values for the where clause
                null, // don't group the rows
                null, // don't filter by row groups
                orderBy // The sort order
                );

        // Tells the Cursor what URI to watch, so it knows when its source data
        // changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        if (DEBUG) { Log.d(TAG, "provider getType called"); }

        /**
         * Chooses the MIME type based on the incoming URI pattern
         */
        switch (sUriMatcher.match(uri)) {

        // If the pattern is for cities returns the general
        // content type.
        case CITIES:
            return CarWeather.Cities.CONTENT_TYPE;

            // If the pattern is for city IDs, returns the city ID content type.
        case CITY_ID:
            return CarWeather.Cities.CONTENT_ITEM_TYPE;

            // If the URI pattern doesn't match any permitted patterns, throws
            // an exception.
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (DEBUG) { Log.d(TAG, "provider insert called"); }

        // Validates the incoming URI. Only the full provider URI is allowed for
        // inserts.
        if (sUriMatcher.match(uri) != CITIES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // A map to hold the new record's values.
        ContentValues values;

        // If the incoming values map is not null, uses it for the new values.
        if (initialValues != null) {
            values = new ContentValues(initialValues);

        } else {
            // Otherwise, create a new value map
            values = new ContentValues();
        }

        // If the values map doesn't contain a title, sets the value to the
        // default title.
        if (values.containsKey(CarWeather.Cities.COLUMN_NAME_PROVINCE) == false) {
//            Resources r = Resources.getSystem();
//            values.put(CarWeather.Cities.COLUMN_NAME_CITY,
//                    r.getString(android.R.string.unknownName));
            return null;
        }

        // If the values map doesn't contain a title, sets the value to the
        // default title.
        if (values.containsKey(CarWeather.Cities.COLUMN_NAME_CITY) == false) {
//            Resources r = Resources.getSystem();
//            values.put(CarWeather.Cities.COLUMN_NAME_CITY,
//                    r.getString(android.R.string.unknownName));
            return null;
        }

        // If the values map doesn't contain a title, sets the value to the
        // default title.
        if (values.containsKey(CarWeather.Cities.COLUMN_NAME_DISTRICT) == false) {
//            Resources r = Resources.getSystem();
//            values.put(CarWeather.Cities.COLUMN_NAME_CITY,
//                    r.getString(android.R.string.unknownName));
            return null;
        }

        // If the values map doesn't contain city text, sets the value to an
        // empty string.
        if (values.containsKey(CarWeather.Cities.COLUMN_NAME_POLYPHONE) == false) {
//            values.put(CarWeather.Cities.COLUMN_NAME_POLYPHONE, "");
            return null;
        }

        //If the values map doesn't contain city index, sets the value to an empty string
//        if(values.containsKey(CarWeather.Cities.COLUMN_NAME_INDEX) == false) {
//            return null;
//        }

        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Performs the insert and returns the ID of the new city.
        long rowId = db.insert(CarWeather.Cities.TABLE_NAME, // The table to
                                                             // insert into.
                CarWeather.Cities.COLUMN_NAME_POLYPHONE, // A hack, SQLite sets
                                                         // this column value to
                                                         // null
                // if values is empty.
                values // A map of column names, and the values to insert
                       // into the columns.
                );

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            // Creates a URI with the city ID pattern and the new row ID
            // appended to it.
            Uri cityUri = ContentUris.withAppendedId(
                    CarWeather.Cities.CONTENT_ID_URI_BASE, rowId);

            // Notifies observers registered against this provider that the data
            // changed.
            getContext().getContentResolver().notifyChange(cityUri, null);
            return cityUri;
        }

        // If the insert didn't succeed, then the rowID is <= 0. Throws an
        // exception.
        throw new SQLException("Failed to insert row into " + uri);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String finalWhere;

        int count;

        // Does the delete based on the incoming URI pattern.
        switch (sUriMatcher.match(uri)) {

            // If the incoming pattern matches the general pattern for cities, does a delete
            // based on the incoming "where" columns and arguments.
            case CITIES:
                count = db.delete(
                    CarWeather.Cities.TABLE_NAME,  // The database table name
                    selection,                     // The incoming where clause column names
                    selectionArgs                  // The incoming where clause values
                );
                break;

                // If the incoming URI matches a single city ID, does the delete based on the
                // incoming data, but modifies the where clause to restrict it to the
                // particular city ID.
            case CITY_ID:
                /*
                 * Starts a final WHERE clause by restricting it to the
                 * desired city ID.
                 */
                finalWhere =
                        CarWeather.Cities._ID +                              // The ID column name
                        " = " +                                          // test for equality
                        uri.getPathSegments().                           // the incoming city ID
                            get(CarWeather.Cities.CITY_ID_PATH_POSITION)
                ;

                // If there were additional selection criteria, append them to the final
                // WHERE clause
                if (selection != null) {
                    finalWhere = finalWhere + " AND " + selection;
                }

                // Performs the delete.
                count = db.delete(
                    CarWeather.Cities.TABLE_NAME,  // The database table name.
                    finalWhere,                // The final WHERE clause
                    selectionArgs                  // The incoming where clause values.
                );
                break;

            // If the incoming pattern is invalid, throws an exception.
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        /*Gets a handle to the content resolver object for the current context, and notifies it
         * that the incoming URI changed. The object passes this along to the resolver framework,
         * and observers that have registered themselves for the provider are notified.
         */
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of rows deleted.
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {

        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        String finalWhere;

        // Does the update based on the incoming URI pattern
        switch (sUriMatcher.match(uri)) {

            // If the incoming URI matches the general notes pattern, does the update based on
            // the incoming data.
            case CITIES:

                // Does the update and returns the number of rows updated.
                count = db.update(
                    CarWeather.Cities.TABLE_NAME, // The database table name.
                    values,                   // A map of column names and new values to use.
                    selection,                    // The where clause column names.
                    selectionArgs                 // The where clause column values to select on.
                );
                break;

            // If the incoming URI matches a single note ID, does the update based on the incoming
            // data, but modifies the where clause to restrict it to the particular note ID.
            case CITY_ID:
                // From the incoming URI, get the note ID
                String noteId = uri.getPathSegments().get(CarWeather.Cities.CITY_ID_PATH_POSITION);

                /*
                 * Starts creating the final WHERE clause by restricting it to the incoming
                 * note ID.
                 */
                finalWhere =
                        CarWeather.Cities._ID +                              // The ID column name
                        " = " +                                          // test for equality
                        uri.getPathSegments().                           // the incoming note ID
                            get(CarWeather.Cities.CITY_ID_PATH_POSITION)
                ;

                // If there were additional selection criteria, append them to the final WHERE
                // clause
                if (selection !=null) {
                    finalWhere = finalWhere + " AND " + selection;
                }


                // Does the update and returns the number of rows updated.
                count = db.update(
                        CarWeather.Cities.TABLE_NAME, // The database table name.
                    values,                   // A map of column names and new values to use.
                    finalWhere,               // The final WHERE clause to use
                                              // placeholders for whereArgs
                    selectionArgs                 // The where clause column values to select on, or
                                              // null if the values are in the where argument.
                );
                break;
            // If the incoming pattern is invalid, throws an exception.
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        /*Gets a handle to the content resolver object for the current context, and notifies it
         * that the incoming URI changed. The object passes this along to the resolver framework,
         * and observers that have registered themselves for the provider are notified.
         */
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of rows updated.
        return count;
    }
}
