package com.sathya.instagramclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class PhotosActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "e05c462ebd86446ea48a5af73769b602";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter photosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().hide();
        photos = new ArrayList<InstagramPhoto>();
        photosAdapter = new InstagramPhotosAdapter(this,photos);
        ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        lvPhotos.setAdapter(photosAdapter);
        // Send request to Popular Photos API
        fetchPopularPhotos();


    }

    public void fetchPopularPhotos(){
        /*
        Instagram Popular API : https://api.instagram.com/v1/media/popular?client_id=e05c462ebd86446ea48a5af73769b602
        Metadata :
        - Type : { data[x].type } ("image" or "video")
        - Caption : { data[x].caption.text }
        - Author Name : { data[x].user.full_name }
        - Author Profile Pic Url : { data[x].user.profile_picture }
        - URL : { data[x].link }
        If type is image
        - Thumbnail Image : {data[x].images.thumbnail.url} (size : 150x150)
        - Low Res Image : {data[x].images.low_resolution.url} (size : 640x640)
        - Standard Res Image : {data[x].images.standard_resolution.url} (size : 640x640)
        If type is video
        - Low Bandwidth Video : {data[x].videos.low_bandwidth.url} (size : 640x640)
        - Standard Res Video : {data[x].videos.standard_resolution.url} (size : 150x150)
        - Low Resolution Video : {data[x].videos.low_resolution.url} (size : 640x640)
        - Creation Time : { data[x].created_time }
        - Likes : {data[x].likes.count}
        - Comments : {data[x].comments.count}
        */

        String apiUrl = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(apiUrl, null, new JsonHttpResponseHandler(){
            //onSuccess

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("DEBUG", response.toString());
                JSONArray photosJson = null;
                try {
                    photosJson = response.getJSONArray("data");
                    for(int i=0;i<photosJson.length();i++){
                        JSONObject photoJson = photosJson.getJSONObject(i);
                        if (photoJson.getString("type").equals("image")) {
                            InstagramPhoto photo = new InstagramPhoto();
                            // Setting full name as username
                            photo.username = photoJson.getJSONObject("user").getString("full_name");
                            // Setting username if full name is empty
                            photo.username = photo.username.isEmpty()?photoJson.getJSONObject("user").getString("username"):photo.username;
                            JSONObject objCaption = photoJson.optJSONObject("caption");
                            photo.caption = (objCaption == null)?"":objCaption.getString("text");
                            photo.imageHeight = photoJson.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                            photo.imageWidth = photoJson.getJSONObject("images").getJSONObject("standard_resolution").getInt("width");
                            photo.imageUrl = photoJson.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                            photo.userProfilePic = photoJson.getJSONObject("user").getString("profile_picture");
                            photo.likesCount = photoJson.getJSONObject("likes").getInt("count");
                            photo.commentsCount = photoJson.getJSONObject("comments").getInt("count");
                            photo.createdTime = photoJson.getString("created_time");
                            photos.add(photo);
                        }

                    }

                    photosAdapter.notifyDataSetChanged();

                } catch (JSONException e){
                    e.printStackTrace();

                }
            }


            //onFailure

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
