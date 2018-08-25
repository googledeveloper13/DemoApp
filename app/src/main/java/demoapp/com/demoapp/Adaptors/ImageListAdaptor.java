package demoapp.com.demoapp.Adaptors;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import demoapp.com.demoapp.DataBeans.DataBeanImages;
import demoapp.com.demoapp.ImageCache.ImageLoader;
import demoapp.com.demoapp.R;

public class ImageListAdaptor extends ArrayAdapter<DataBeanImages> {
    private ArrayList<DataBeanImages> objects;
    DataBeanImages dbItemsDist;
    TextView tvName;
    ImageView ivImage;
    private ImageLoader imgLoader;

    public static final String PREFS_NAME = "Images";
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    String ids,imagePath,encodedImage;
    public ImageListAdaptor(Context context, int resource, ArrayList<DataBeanImages> objects)
    {
        super(context, resource,objects);
        this.objects=objects;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.image_list_items, null);
            dbItemsDist = objects.get(position);

            if (dbItemsDist != null)
            {
                tvName=(TextView)v.findViewById(R.id.tvName);
                ivImage=(ImageView)v.findViewById(R.id.ivImage);

                String path="http://farm"+objects.get(position).getImageFarm()+".static.flickr.com/"
                        +objects.get(position).getImageServer()+"/"+objects.get(position).getImageId()
                        +"_"+objects.get(position).getImageSecret()+".jpg";

                settings = getContext().getSharedPreferences(PREFS_NAME, 0);
                editor = settings.edit();
                ids = settings.getString("id", "");
                imagePath = settings.getString("path", "");
                encodedImage = settings.getString("encodedImage", "");

                if (ids.equals(objects.get(position).getImageId()) && imagePath.equals(path))
                {
                    Log.d("ifif","if");

                    byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ivImage.setImageBitmap(decodedByte);

                }
                else
                {
                    Log.d("elseelse","else");
                    tvName.setText(objects.get(position).getImageId());
                    new DownloadImageTask((ivImage),objects.get(position).getImageId(),path)
                            .execute(path);
                }


                //calling to view the images

               /*imgLoader = new ImageLoader(getContext());
                imgLoader.DisplayImage(path, ivImage);*/
            }
            return v;
        }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        String imageId;
        String path;
        public DownloadImageTask(ImageView bmImage, String imageId, String path) {
            this.bmImage = bmImage;
            this.imageId = imageId;
            this.path = path;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
        protected void onPostExecute(Bitmap result)
        {
            bmImage.setImageBitmap(result);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            result.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            settings = getContext().getSharedPreferences(PREFS_NAME, 0);
            editor = settings.edit();
            editor.putString("path", path);
            editor.putString("id", imageId);
            editor.putString("encodedImage", encodedImage);
            editor.commit();

            /*Bitmap realImage = BitmapFactory.decodeStream(result);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            realImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);*/
        }
    }
}

