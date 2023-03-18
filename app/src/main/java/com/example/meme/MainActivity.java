package com.example.meme;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.content.Intent;
import android.content.res.Resources;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageView imageView,download_button,share_button;
    Button button;
    Dialog dialog;
    TextView subReddit_name,meme_title,meme_user_name,meme_nsfw;
    String meme_link = null,meme_author_name,meme_url = null;
    Bitmap bitmap;
    BitmapDrawable bitmapDrawable;


   public static ArrayList<String> arr = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dailogbar);

        if(dialog.getWindow()!=null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        imageView = findViewById(R.id.meme_image);
        button = findViewById(R.id.meme_button);
        subReddit_name = findViewById(R.id.meme_subreddit_name);
        meme_title = findViewById(R.id.meme_title);
        meme_user_name = findViewById(R.id.meme_user_name);
        meme_nsfw = findViewById(R.id.meme_nsfw);
        download_button = findViewById(R.id.meme_download_button);
        share_button = findViewById(R.id.meme_share_button);

        subReddit_name.setOnClickListener(onClickListener);

        meme_user_name.setOnClickListener(listener_author_name);

        meme_nsfw.setOnClickListener(listener_nsfw_text);

        dialog.show();

         meme_nsfw.setVisibility(View.INVISIBLE);

        RequestManager requestManager = new RequestManager(this);

        requestManager.getData(listener);


        download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                bitmap = bitmapDrawable.getBitmap();

                FileOutputStream fileOutputStream = null;

                File card = Environment.getExternalStorageDirectory();
                File directory = new File(card.getAbsolutePath() + "/Download");
                directory.mkdir();

                String filename = String.format("%d.jpg",System.currentTimeMillis());
                File output =new File(directory,filename);

                Toast.makeText(MainActivity.this,"Image Saved Successfully",Toast.LENGTH_SHORT).show();

                try{

                    fileOutputStream = new FileOutputStream(output);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(output));
                    sendBroadcast(intent);



                }
                catch (Exception e){
                    e.printStackTrace();
                }




            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestManager requestManager = new RequestManager(MainActivity.this);

                requestManager.getData(listener);

                dialog.show();

            }
        });




        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Drawable mDrawable = imageView.getDrawable();
                Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();

                String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Image Description", null);
                Uri uri = Uri.parse(path);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent, "Share Image"));



            }
        });







    }

    private final OnFetchDataListener listener = new OnFetchDataListener() {
        @Override
        public void OnFetchData(String postLink, String subreddit, String title, String url, boolean nsfw, boolean spoiler, String author, int ups, String[] arr, String message) {
                  meme_link = postLink;
                  meme_author_name = author;
                  meme_url = url;

                  boolean bool = checkUrlRepeatedOrNot(title);

                  if(bool==false)
            display_meme(url,subreddit,title,author,nsfw);
                  else{

                      RequestManager requestManager = new RequestManager(MainActivity.this);

                      requestManager.getData(listener);

                  }

        }

        @Override
        public void OnError(String message) {

            dialog.dismiss();
            Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();

        }
    };

    private void display_meme(String url,String subreddit,String title,String author,boolean nsfw) {
             dialog.dismiss();
             meme_title.setText(title);
             meme_user_name.setText("u/" + author);
        subReddit_name.setText("r/" + subreddit);
        Picasso.get().load(Uri.parse(url)).into(imageView);


        if(nsfw==true) {

            meme_nsfw.setVisibility(View.VISIBLE);

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    imageView.setRenderEffect(
                            RenderEffect.createBlurEffect(40.0f, 40.0f, Shader.TileMode.CLAMP)

                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }



        if(nsfw==false && imageView!=null) {

            meme_nsfw.setVisibility(View.INVISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                imageView.setRenderEffect(
                        null
                );
            }
        }




    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(meme_link!=null){

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(meme_link));
                startActivity(intent);

            }

        }
    };

    View.OnClickListener listener_author_name = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v!=null){


                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.reddit.com/user/" + meme_author_name + "/")));

            }

        }
    };

    View.OnClickListener listener_nsfw_text = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            meme_nsfw.setVisibility(View.INVISIBLE);

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    imageView.setRenderEffect(
                            null

                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    };

    public boolean checkUrlRepeatedOrNot(String url){

        if(arr.contains(url))
            return true;

        arr.add(url);

        return false;
    }


}