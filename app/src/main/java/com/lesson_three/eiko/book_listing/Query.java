package com.lesson_three.eiko.book_listing;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eiko on 11/26/2016.
 */
public class Query {
    final static String TAG = "Query";

    private Query(){}

    public static List<List_item> fetchBookdata(String requestUrl){

        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        }catch (IOException e){
            Log.v(TAG,"Exception making makeHttpRequest in Query.java: "+e);
            }
        Log.v(TAG,"jsonResponse: " + jsonResponse);
        List<List_item> list_book = extractFromJson(jsonResponse);
        return list_book;
    }
    private static URL createUrl(String string_url){
        URL url = null;
        try {
            url = new URL(string_url);
        }catch (MalformedURLException e){}
        return url;
    }
    private static String makeHttpRequest(URL url)throws IOException{
        String jsonResponse = "";
        if (url == null) {
            Log.v(TAG,"url was null in makeHTTPRequest");
            return jsonResponse;
        }
    HttpURLConnection urlConnection = null;
    InputStream inputStream = null;
    try {
        Log.v(TAG,"Attempting connection");
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.connect();
        if (urlConnection.getResponseCode() == 200){
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        }else {
            Log.v(TAG,"not going through..");
            }
    }catch (IOException e){
        Log.v(TAG,"not going through..");
        }finally {
        if (urlConnection != null){
            urlConnection.disconnect();
        }if (inputStream != null){
            inputStream.close();
        }
    }
        return jsonResponse;
    }
    private static String readFromStream(InputStream input_stream)
            throws IOException{
        StringBuilder outputStringBuilder = new StringBuilder();
        if (input_stream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(
                    input_stream, Charset.forName("UTF-8"));
            BufferedReader buffReader = new BufferedReader(inputStreamReader);
            String line = buffReader.readLine();
            while (line != null){
                outputStringBuilder.append(line);
                line = buffReader.readLine();
            }
        }
        return outputStringBuilder.toString();
    }
    private static List<List_item> extractFromJson(String bookJSON){
        if (TextUtils.isEmpty(bookJSON)){
            return null;
        }

        List<List_item> booklist = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(bookJSON);

            JSONArray bookArray = jsonObject.getJSONArray("items");
            for (int i = 0; i < bookArray.length(); i++){
                ArrayList<String> author = new ArrayList<>();

                JSONObject gettingbook = bookArray.getJSONObject(i);
                JSONObject volumeInfo = gettingbook.getJSONObject("volumeInfo");

                JSONArray book_author = volumeInfo.getJSONArray("authors");
                for (int a = 0; a<book_author.length(); a++){
                    author.add(book_author.getString(a));
                }
                String book_title = volumeInfo.getString("title");
                String book_info = volumeInfo.getString("description");

                List_item listitem = new List_item(book_title, author,
                        book_info);
                booklist.add(listitem);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        return booklist;
    }
}
