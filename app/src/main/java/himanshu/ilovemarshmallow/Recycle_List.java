package himanshu.ilovemarshmallow;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import android.widget.ProgressBar;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.StrictMode;

public class Recycle_List extends Activity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String[] productName,productAsin,productPrice, productRating,imageURL;


    private Recycler_View_Adapter recycler_view_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle__list);
        recyclerView = (RecyclerView)findViewById(R.id.recycle_view);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setVisibility(View.VISIBLE);

        //Allows to be run on main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


         Intent getSearchVal = getIntent();
        String searchVal = getSearchVal.getStringExtra("SearchValue");
        if(searchVal == null){
            Log.d("Intent value is null", "onCreate");
        }


        final String url = "https://zappos.amazon.com/mobileapi/v1/search?term=" + searchVal;

        new getData().execute(url);
    }






    public class getData extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;

            Integer result = 0;

            try{
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection)url.openConnection();


                urlConnection.setRequestProperty("Content-Type", "application/json");


                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("GET");

                int statusCode = urlConnection.getResponseCode();



                if(statusCode == 200){
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String response = convertInputStreamToString(inputStream);
                    parseResult(response);

                }


            }
            catch(Exception e){
                e.printStackTrace();
            }

            return result;
        }


        @Override
        protected void onPostExecute(Integer result) {


                progressBar.setVisibility(View.GONE);


                recycler_view_adapter = new Recycler_View_Adapter(Recycle_List.this, productName,productAsin,productPrice,productRating,imageURL);
                recyclerView.setAdapter(recycler_view_adapter);


        }



    }

    private String convertInputStreamToString(InputStream inputStream){
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));

        String line = "";
        String result = "";

        try {
            while((line = bufferedReader.readLine()) != null){
                result += line;
            }
            if(null!=inputStream){
                inputStream.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print(result);
            /* Close Stream */

        return result;
    }

    private void parseResult(String result) {

        try{
            JSONObject response = new JSONObject(result);

            JSONArray posts = response.optJSONArray("results");



            productName = new String[posts.length()];
            productAsin = new String[posts.length()];
            productPrice = new String[posts.length()];
            productRating = new String[posts.length()];
            imageURL = new String[posts.length()];

            for(int i=0; i< posts.length();i++ ){
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("productName");
                String asinNumber = post.optString("asin");
                String priceProduct = post.optString("price");
                String ratingProduct = post.optString("productRating");
                String url = post.optString("imageUrl");

                productPrice[i] = priceProduct;
                productAsin[i] = asinNumber;
                productName[i] = title;
                productRating[i] = ratingProduct;
                imageURL[i] = url;
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recycle__list, menu);
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
