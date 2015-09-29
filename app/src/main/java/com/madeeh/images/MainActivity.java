package com.madeeh.images;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    Button btn;
    ImageView img;
    CheckBox chk;
    Button btnsave;
    Button btnsavephone;

    Bitmap imageBitmap;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn=(Button)findViewById(R.id.btn_takephoto);
        img=(ImageView)findViewById(R.id.iv_photo);
        chk=(CheckBox)findViewById(R.id.chk_invert);
        btnsave=(Button)findViewById(R.id.btn_save);
        btnsavephone=(Button)findViewById(R.id.btn_savePhone);

        if(!hasCamera()){
            btn.setEnabled(false);
        }

        loadPreferences();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreferences();
                finish();
            }
        });

        btnsavephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage(imageBitmap);
                Toast.makeText(getApplicationContext(),"image saved",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            if(chk.isChecked()){
                imageBitmap=invertImage(imageBitmap);
            }

            img.setImageBitmap(imageBitmap);
        }
    }

    private void savePreferences(){
        SharedPreferences prefs=getSharedPreferences("Info", 0);

        SharedPreferences.Editor editor=prefs.edit();
        editor.putBoolean("Inverted",chk.isChecked());
        editor.commit();
    }

    private void loadPreferences(){
        SharedPreferences prefs=getSharedPreferences("Info", 0);
        boolean checked=prefs.getBoolean("Inverted",false);
        chk.setChecked(checked);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //use this method to convert an image in the drawable folder to bitmap if needed
    private void getBitmapFromDrawable(){
        Drawable src=getResources().getDrawable(R.drawable.ic_launcher);
        Bitmap photo=((BitmapDrawable)src).getBitmap();
    }

    private Bitmap invertImage(Bitmap original){

        int height=original.getHeight();
        int width=original.getWidth();

        Bitmap img=Bitmap.createBitmap(width,height,original.getConfig());
        int R,G,B,A;
        int pixel;

        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                pixel=original.getPixel(x,y);
                R=255- Color.red(pixel);
                G=255- Color.green(pixel);
                B=255- Color.blue(pixel);
                A=Color.alpha(pixel);

                img.setPixel(x,y,Color.argb(A,R,G,B));
            }
        }

        return img;
    }

    //SAVE IMAGE TO DEVICE:
    private void saveImage(Bitmap img){
        MediaStore.Images.Media.insertImage(getContentResolver(),img,"Image Title","Image Desc");
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
