package com.wilsonburhan.instagramphotofeeds;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Wilson on 11/12/14.
 */
public class InstagramClient {

    // Change this if you want to change the source of the feeds.
    private final String INSTAGRAM_POPULAR_PHOTO_URL = "https://api.instagram.com/v1/media/popular?client_id=";
    private String mClientId;
    private AsyncHttpClient mClient;

    public InstagramClient(String clientId) {
        mClientId = clientId;
        mClient = new AsyncHttpClient();
    }

    /**
     * Try to get the photos from the popular photo url.
     * @param object
     * @param method
     */
    public void retrievePhotos(final Object object, final Method method) {
        mClient.get(INSTAGRAM_POPULAR_PHOTO_URL + mClientId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    method.invoke(object, statusCode, headers, response);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    /**
     * Parse the JSON data.
     * @param response JSON object
     * @return list of instagram photo data structure.
     */
    public ArrayList<InstagramPhoto> parseJSONResponse(JSONObject response) {
        ArrayList<InstagramPhoto> photos = new ArrayList<InstagramPhoto>();
        JSONArray photosJSONArray;
        try {
            photosJSONArray = response.getJSONArray("data");
            for (int i = 0; i < photosJSONArray.length(); i++) {
                try {
                    // Create the InstagramPhoto object and populate it the JSON data..
                    JSONObject photoJSON = photosJSONArray.getJSONObject(i);
                    InstagramPhoto photo = new InstagramPhoto();

                    JSONObject user = photoJSON.getJSONObject("user");
                    photo.username = user.getString("username");
                    photo.avatarUrl = user.getString("profile_picture");

                    photo.timestamp = photoJSON.getLong("created_time");

                    JSONObject standardResolutionImage = photoJSON.getJSONObject("images").getJSONObject("standard_resolution");
                    photo.url = standardResolutionImage.getString("url");
                    photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");

                    photos.add(photo);
                } catch (Exception e) {
                    Log.e("JSON parse error", e.getMessage());
                }
            }
        } catch (JSONException e) {
            Log.e("JSON array error", e.getMessage());
        }
        return photos;
    }
}
