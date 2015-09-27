/*

Adapter for custom list view
Adds data to image and text view for each item
On item click sends intent to Product_Info class with information about the product


 */


package himanshu.ilovemarshmallow;


import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.content.Intent;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.net.URL;


public class Recycler_View_Adapter extends RecyclerView.Adapter<Recycler_View_Adapter.ViewHolder>{

    private String[] title,asin,price,rating,url;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder  {


        public TextView productName,productPrice;
        public ImageView img;

        public ViewHolder(View v) {
            super(v);
            //Define views
            productName = (TextView) v.findViewById(R.id.title);
            img = (ImageView)v.findViewById(R.id.thumbnail);
        }


    }



    //Initialize Views
    public Recycler_View_Adapter(Context context,String[] title, String[] asin, String[] price,String[] rating,String[] url ){
        this.title = title;
        this.context = context;
        this.asin = asin;
        this.price = price;
        this.rating = rating;
        this.url = url;
    }

    //Inflate custom list view
    @Override
    public Recycler_View_Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {


        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_list_view,null);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    //Adds data to views
    //Send intent to Product_Info class with data of the product
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final String name = title[i];
        viewHolder.productName.setText(name);
     //   viewHolder.productPrice.setText(price[i]);
       try {
           URL urlV = new URL(url[i]);
           Bitmap bmp = BitmapFactory.decodeStream(urlV.openConnection().getInputStream());
           viewHolder.img.setImageBitmap(bmp);
       }
       catch (Exception e){
           e.printStackTrace();
           Toast.makeText(context, "Error loading images",Toast.LENGTH_LONG).show();;
       }
        viewHolder.productName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent ProductIntent = new Intent(context, Product_Info.class);
                ProductIntent.putExtra("ProductAsin", asin[viewHolder.getPosition()]);
                ProductIntent.putExtra("Price",price[viewHolder.getPosition()]);
                ProductIntent.putExtra("Rating",rating[viewHolder.getPosition()]);

                context.startActivity(ProductIntent);

            }
        });

        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent ProductIntent = new Intent(context, Product_Info.class);
                ProductIntent.putExtra("ProductAsin", asin[viewHolder.getPosition()]);
                ProductIntent.putExtra("Price",price[viewHolder.getPosition()]);
                ProductIntent.putExtra("Rating",rating[viewHolder.getPosition()]);

                context.startActivity(ProductIntent);

            }
        });
    }



    @Override
    public int getItemCount() {
        return title.length;
    }
}
