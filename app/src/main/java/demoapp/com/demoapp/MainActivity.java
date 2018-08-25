package demoapp.com.demoapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import java.util.ArrayList;

import demoapp.com.demoapp.Adaptors.ImageListAdaptor;
import demoapp.com.demoapp.DataBeans.DataBeanImages;

import static demoapp.com.demoapp.Tools.httpUrls.MAIN_API;

public class MainActivity extends AppCompatActivity
{
    GridView gridView;
    ArrayList<DataBeanImages> imageArray=new ArrayList<>();
    DataBeanImages dataBeanImages;
    ImageListAdaptor imageListAdaptor;

    String imageId,imageOwner,imageSecret,imageFarm,imageTitle,imageServer;

    private ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //type casting the view elements
        gridView=(GridView)findViewById(R.id.gridView);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);

        if (isInternetOn() == true)
        {
            GetAllImages getAllImages= new GetAllImages(MainActivity.this);
            getAllImages.execute();
        }
        else
        {
            Toast.makeText(this, "No network", Toast.LENGTH_SHORT).show();
        }


    }


    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    public class GetAllImages extends AsyncTask<String, String, String>
    {
        Context ctx;


        GetAllImages(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            spinner.setVisibility(View.VISIBLE);

            
        }

        @Override
        protected String doInBackground(String... params) {
            String branch_url = MAIN_API;
            try {
                URL url = new URL(branch_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
         /*       httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
*/
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {

                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;
            } catch (MalformedURLException s) {
                s.printStackTrace();
            } catch (IOException s)
            {
                s.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            Log.d("RESPONSEDATA", "" + result);

             try
            {
                try
                {
                    String strData = result;
                    Log.d("strData",""+strData);

                    JSONObject jsonRootObject = new JSONObject(strData);
                    String d1=jsonRootObject.getString("photos");
                    Log.d("d1d1d1d1",""+d1);

                    JSONObject jsonRootObject1 = new JSONObject(d1);
                    JSONArray jsonArray = jsonRootObject1.getJSONArray("photo");
                    Log.d("jsonArray",""+jsonArray);
                    for (int i = 0; i < jsonArray.length(); i++)
                    {

                        dataBeanImages = new DataBeanImages();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        imageId = jsonObject.getString("id");
                        imageOwner = jsonObject.getString("owner");
                        imageSecret = jsonObject.getString("secret");
                        imageFarm = jsonObject.getString("farm");
                        imageTitle = jsonObject.getString("title");
                        imageServer = jsonObject.getString("server");

                        dataBeanImages.setImageId(imageId);
                        dataBeanImages.setImageOwner(imageOwner);
                        dataBeanImages.setImageSecret(imageSecret);
                        dataBeanImages.setImageFarm(imageFarm);
                        dataBeanImages.setImageTitle(imageTitle);
                        dataBeanImages.setImageServer(imageServer);

                        imageArray.add(dataBeanImages);
                    }

                    setValues(imageArray);

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            catch (NullPointerException e)
            {
                Toast.makeText(ctx, "Server issue", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(result);

        }
    }

    private void setValues(ArrayList<DataBeanImages> imageArray)
    {
        spinner.setVisibility(View.GONE);
        imageListAdaptor= new ImageListAdaptor(MainActivity.this, R.layout.image_list_items, imageArray);
        gridView.setAdapter(imageListAdaptor);

        runOnUiThread(new Runnable() {
            public void run() {
                imageListAdaptor.notifyDataSetChanged();
            }
        });
    }
}
