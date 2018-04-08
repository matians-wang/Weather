package com.test.carweather.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class CarWeather {
    public static final String AUTHORITY = "com.test.carweather";

    private CarWeather(){
    }
    /**
     * Cities table contract
     */
    public static final class Cities implements BaseColumns {

        // This class cannot be instantiated
        private Cities() {}

        /**
         * The table name offered by this provider
         */
        public static final String TABLE_NAME = "cities";

        /*
         * URI definitions
         */

        /**
         * The scheme part for this provider's URI
         */
        private static final String SCHEME = "content://";

        /**
         * Path parts for the URIs
         */

        /**
         * Path part for the Notes URI
         */
        private static final String PATH_CITIES = "/cities";

        /**
         * Path part for the Note ID URI
         */
        private static final String PATH_CITY_ID = "/cities/";

        /**
         * 0-relative position of a city ID segment in the path part of a city ID URI
         */
        public static final int CITY_ID_PATH_POSITION = 1;

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_CITIES);

        /**
         * The content URI base for a single city. Callers must
         * append a numeric city id to this Uri to retrieve a city
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + PATH_CITY_ID);

        /*
         * MIME type definitions
         */

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of cities.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.test.city";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * city.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.test.city";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "area_id ASC";

        /*
         * Column definitions
         */

        /**
         * Column name for the name of the province
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_PROVINCE = "province";
        /**
         * Column name for the name of the city
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_CITY = "city";
        /**
         * Column name for the name of the district
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_DISTRICT = "district";
        /**
         * Column name of the city polyphone
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_POLYPHONE = "area_id";

        /**
         * Column name of the city index
         * <P>Type: Integer</P>
         */
        //public static final String COLUMN_NAME_INDEX = "initial";

    }
}
