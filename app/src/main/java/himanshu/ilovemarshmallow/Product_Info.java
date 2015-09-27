/*

Following class gets URL through Recycler View Adapter and generates view of product information.
onCreate(Bundle savedInstanceState) - Calls AsyncTask to create connection and parse data

getData - Gets data from url and shows it after parsing JSON
    doInBackground - Creates connection to url and calls 2 functions 1.InputStream to String 2.Parse JSON
    onPostExecute - Displays data on views.

convertInputStreamToString - Converts input stream to string type

parseResult - Parse JSON string
*/
package himanshu.ilovemarshmallow;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
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
import java.util.*;
import java.util.regex.*;
import android.widget.Button;

public class Product_Info extends Activity {

    private TextView productNameView,priceView,productRatingView,productDesc,productTypeView;
    private String PrdnameString, priceString, ratingString,typeString,asinString,imageUrl,description,finalDesc;
    private ImageView productImage;
    private ProgressBar progressBar;
    private ListView childAsins;
    private Button share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_product__info);
            productNameView = (TextView)findViewById(R.id.product_name);
            priceView = (TextView)findViewById(R.id.price);
            productRatingView = (TextView)findViewById(R.id.product_rating);
            progressBar = (ProgressBar)findViewById(R.id.ProductProgress_bar);
            productTypeView = (TextView)findViewById(R.id.product_type);
            productImage = (ImageView)findViewById(R.id.product_image);
            productDesc = (TextView)findViewById(R.id.product_desc);

            //Get data from intent
            Intent productInfo = getIntent();
            asinString = productInfo.getExtras().getString("ProductAsin");
            priceString = productInfo.getExtras().getString("Price");
            ratingString = productInfo.getExtras().getString("Rating");

            progressBar.setVisibility(View.VISIBLE);

            //Add asin value of product to the url
            final String productURL = "https://zappos.amazon.com/mobileapi/v1/product/asin/" + asinString;
            new getData().execute(productURL);





    }

    public class getData extends AsyncTask<String, Void, Integer> {


        //Creates connection to url
        //Gets data in Input Stream
        //Call function to parse stream
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
            /* Download complete. Lets update UI */
            //Set Progress Bar visibility to gone
            progressBar.setVisibility(View.GONE);

            //Add to all the views
            productNameView.setText(PrdnameString);
            priceView.setText(priceString);
            productRatingView.setText(ratingString);
            productDesc.setText(finalDesc);

            //Add bitmap to image view
            productTypeView.setText(typeString);
            try {
                URL urlV = new URL(imageUrl);
                Bitmap bmp = BitmapFactory.decodeStream(urlV.openConnection().getInputStream());
                productImage.setImageBitmap(bmp);
            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(Product_Info.this, "Error loading images", Toast.LENGTH_LONG).show();;
            }



        }



    }


    //Convert Stream to String type
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

    //Parse JSON
    private void parseResult(String result) {

        try{
            JSONObject response = new JSONObject(result);

            description = response.optString("description");
            typeString = response.optString("defaultProductType");
            PrdnameString = response.optString("productName");
            imageUrl = response.optString("defaultImageUrl");
            JSONArray posts = response.optJSONArray("childAsins");


            //Get content from <li> tags
            List<String> tagValues = new ArrayList<String>();
            final Matcher matcher = Pattern.compile("<li>(.+?)</li>").matcher(description);
            while (matcher.find()) {
                tagValues.add(matcher.group(1));
            }
            for(String s : tagValues) {
                finalDesc += s + "\t";
            }

//            Log.d("Product Description", finalDesc);

           /*


            for(int i=0; i< posts.length();i++ ){
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("productName");
                String asinNumber = post.optString("asin");

                asin[i] = asinNumber;
                productName[i] = title;
            }*/

        }catch (JSONException e){
            e.printStackTrace();
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product__info, menu);
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
