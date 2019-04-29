package com.example.efelek16.fleamarket;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class detailact extends AppCompatActivity {
    TextView articlename,price,username,email,phone,lat,lon,distance;
    Button mail,call,maps;
    double latt;

    double lonn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailact);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String name = bundle.getString("articlename");
        String pricee = bundle.getString("price");
        String usernamee = bundle.getString("username");
        String emaill = bundle.getString("email");
        String phonee = bundle.getString("phone");
         latt= bundle.getDouble("lat");
         lonn = bundle.getDouble("lon");
        int distancee = bundle.getInt("distance");

        mail = findViewById(R.id.buttonmail);
        call = findViewById(R.id.buttoncall);
        maps = findViewById(R.id.buttonmaps);

        articlename = findViewById(R.id.name);
        price = findViewById(R.id.price);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        lat = findViewById(R.id.lat);
        lon = findViewById(R.id.lon);
        distance = findViewById(R.id.distance);

        articlename.setText("Articelname= "+name);
        price.setText("Price= "+pricee);
        username.setText("Username= "+usernamee);
        email.setText("E-Mail= "+emaill);
        phone.setText("Phone= "+phonee);
        lat.setText("lat= "+latt);
        lon.setText("lon= "+lonn);
        distance.setText("Distance to= "+distancee/1000+" km");

        call.setOnClickListener(v->{
            makePhoneCall();
        });
        mail.setOnClickListener(v->{
            sendMail();
        });
        maps.setOnClickListener(v-> {
            openMap();

        });


    }
    //$$$$$$$$$$$$$$$$$$$$$$$$$$OPENMAP§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§
    public void openMap(){
        String pos="geo:0,0?q=\""+latt+","+lonn+"\"";

            if(latt != 0&&lonn!=0)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(pos));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
            else{
                Toast.makeText(this,"lat or lon = 0",Toast.LENGTH_SHORT).show();
            }
        }


    //###########################SEND MAIL###############################################
    private void sendMail(){
        String mailto = email.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, mailto);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Choose an email client"));

    }



    //++++++++++++++++++++++++++++++PHONECALL+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private static final int RQ_CALL_PHONE = 1;


    public void makePhoneCall(){
      String number = phone.getText().toString();
      if(number.trim().length()>0){
          if (ContextCompat.checkSelfPermission(detailact.this,Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
              ActivityCompat.requestPermissions(detailact.this,
                      new String[] {Manifest.permission.CALL_PHONE},RQ_CALL_PHONE);
          }else{
              String dial = "tel:"+number;
              startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));

          }
      }else{
            Toast.makeText(this,"phonenumbernotviable",Toast.LENGTH_SHORT).show();
      }
    }
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@REQUESTPERMISSIONRESULT@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == RQ_CALL_PHONE) {
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission is granted",Toast.LENGTH_SHORT).show();
            }
        }
    }


}
