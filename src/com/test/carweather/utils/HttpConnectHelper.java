package com.test.carweather.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;


import android.content.Context;
import android.util.Log;

public class HttpConnectHelper {
    private Context mContext;
    private static final String TAG = "xyj_HttpConnectHelper";
    private static final boolean DEBUG = MyLogConfig.DEBUG;

    /** HTTP Connection */
    private HttpURLConnection httpConnection;
    public HttpConnectHelper(Context context) {
        mContext = context;
    }

    private void requestConnectServer(String strURL) throws IOException {
        if (DEBUG) Log.d(TAG, "requestConnectServer: " + strURL);
        httpConnection = (HttpURLConnection) new URL(strURL).openConnection();
        httpConnection.setConnectTimeout(20 * 1000);
        httpConnection.setReadTimeout(20 * 1000);
        httpConnection.addRequestProperty("X-cc-Vin", "B324AB9A24F780F62");
        
        httpConnection.connect();

        if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            if (DEBUG)
                Log.e(TAG, "Something wrong with connection, responseCode is: "
                        + httpConnection.getResponseCode());
            httpConnection.disconnect();
            throw new IOException("Error in connection: " + httpConnection.getResponseCode());
        }
    }

    private void requestDisconnect() {
        if (httpConnection != null) {
            httpConnection.disconnect();
            httpConnection = null;
        }
    }

    public Document getDocumentFromURL(String strURL) throws IOException {
        //Verify the validity of this URL
        if (strURL == null) {
            if (DEBUG) Log.e(TAG, "Invalid URL :" + strURL);
            return null;
        }

        //Connect to server 
        requestConnectServer(strURL);
        //Get data from server
        String strDocContent = getDataFromConnection();
        Log.d(TAG, "strDocContent==" + strDocContent);
        //Close connection
        requestDisconnect();

        if (strDocContent == null) {
            if (DEBUG) Log.e(TAG, "Can't get data from server!");
            return null;
        }

        int strContentSize = strDocContent.length();
        StringBuffer strBuff = new StringBuffer();
        strBuff.setLength(strContentSize + 1);
        strBuff.append(strDocContent);
        ByteArrayInputStream is = new ByteArrayInputStream(strDocContent.getBytes());

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document docData = null;

        try {
            db = dbf.newDocumentBuilder();
            docData = db.parse(is);
        } catch (Exception e) {
            if (DEBUG) Log.e(TAG, "Parser data error");
            return null;
        }

        return docData;
    }
    
    public String getDataFromServer(String url) throws IOException {
        if (url == null) {
            Log.e(TAG, "Invalid URL : " + url);
        }
        //Connect to server 
        requestConnectServer(url);
        //Get data from server
        String content = getDataFromConnection();
        //Close connection
        requestDisconnect();
        return content;
    }

    private String getDataFromConnection() throws IOException {
        if (httpConnection == null) {
            if (DEBUG) Log.e(TAG, "Connection is null.");
            return null;
        }
        
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = httpConnection.getInputStream();
            if (inputStream == null) {
                if (DEBUG) Log.e(TAG, "Can't get inputStream from httpConnection!");
                return null;
            }
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String strLine = "";
            while ((strLine = reader.readLine()) != null) {
                buffer.append(strLine + "\n");
            }

            if (DEBUG) Log.d(TAG, "result : " + buffer.toString());
            return buffer.toString();
        } finally {
            /* Release resource to system */
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "IOException occured when close reader or inputStream!");
            }
        }
    }
}
