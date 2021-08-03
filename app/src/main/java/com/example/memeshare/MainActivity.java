  package com.example.memeshare;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    ImageView memeImage;
    private static OkHttpClient client;
    private static Request request;
    static JSONObject objectjson;
    static Response response;
    private ProgressBar screenloader;
    private String image_url;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        memeImage=findViewById(R.id.memeImage);
        screenloader=findViewById(R.id.progressBar);
        //loading first meme on app start
        sendMemeRequest();


    }

static class Singleton {
         private static OkHttpClient clientObj=null;
      private static   JSONObject jsonObject=null;
      private static Request requestObj;

    private Singleton(){

        }
            //single client object every time
            public static OkHttpClient getClientInstance(){
                if(Singleton.clientObj==null) {
                      clientObj = new OkHttpClient();
                }
                return clientObj;
            }
            //single request object every time
            static Request getRequestInstance(){
                if (requestObj == null) {
                    requestObj = new Request.Builder()
                            .url("https://meme-api.herokuapp.com/gimme")
                            .get()
                            .build();
                }
                return requestObj;
            }

}
    public void sendMemeRequest(){
        //shows screen loader at begining
        screenloader.setVisibility(View.VISIBLE);
        client=Singleton.getClientInstance();
         request=Singleton.getRequestInstance();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    response = client.newCall(request).execute();
                    String res = response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void run() {
                            try {
                               objectjson = new JSONObject(res);
                                 image_url=objectjson.getString("url");
                                Toast.makeText(getApplicationContext(),"Great!!!" ,Toast.LENGTH_SHORT).show();


                                Picasso.with(MainActivity.this).load(image_url).into(memeImage);
                                screenloader.setVisibility(View.GONE);
                                //hiding loader after image gets loaded



                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }

                        }


                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        thread.start();

}
    public void shareMeme(View view) {
        Intent send=new Intent();
        send.setAction(Intent.ACTION_SEND);
//        send.setAction(Intent.ACTION_WEB_SEARCH);
        send.setType("text/plain");
        //this sends the meme link to choosen application
        send.putExtra(Intent.EXTRA_TEXT,image_url);
        //this opens the dialog box to choose app to send link
        Intent chooser=Intent.createChooser(send,"share meme using..");
        startActivity(chooser);

    }

    public void nextButton(View view) {
        sendMemeRequest();
    }
}