package com.wilsonburhan.instagramphotofeeds;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.ListView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wilson on 11/12/14.
 */
public class InstagramPhotoFeedsActivity extends FragmentActivity {

    public final String INSTAGRAM_CLIENT_ID = "58a9634bc7fa43938672ee23baee1a47";
    private List<InstagramPhoto> mPhotoList = new ArrayList<InstagramPhoto>();
    private SwipeRefreshLayout mContainer;
    private InstagramClient mClient;
    private ListView mListView;
    private InstagramPhotosAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity);

        mClient = new InstagramClient(INSTAGRAM_CLIENT_ID);
        mContainer = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        // Set the color scheme of the refresh animation.
        mContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        refresh();
    }

    /**
     * Refreshes the list view and fetch new data.
     */
    private void refresh(){
        try {
            Class[] paramTypes = new Class[3];
            paramTypes[0] = int.class;
            paramTypes[1] = Header[].class;
            paramTypes[2] = JSONObject.class;
            Method method = InstagramPhotoFeedsActivity.class.getMethod("getPopularPhoto", paramTypes);
            mClient.retrievePhotos(this, method);
        } catch(Exception ex) {
            Log.e("Error", ex.getMessage());
        }
    }

    /**
     * Populate list view with the image feeds.
     */
    private void populateListView(){
        mListView = (ListView)findViewById(R.id.photo_list_view);
        mAdapter = new InstagramPhotosAdapter(this, mPhotoList);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Retrieve Instagram popular photo feeds.
     * @param status
     * @param headers
     * @param response
     */
    public void getPopularPhoto(int status, Header[] headers, JSONObject response){
        mPhotoList.clear();
        mPhotoList = mClient.parseJSONResponse(response);
        mContainer.setRefreshing(false);
        populateListView();
    }
}
