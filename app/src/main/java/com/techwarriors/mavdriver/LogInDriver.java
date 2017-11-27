package com.techwarriors.mavdriver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoIncompatibleDriverException;
import com.mongodb.MongoTimeoutException;

public class LogInDriver extends AppCompatActivity  {

    Connectivity connectivity;
    private MongoClient mongoClient;
    private MongoClientURI mongoClientURI;
    private DBCollection collection;
    final  String DATABASE_NAME=connectivity.DATABASE_NAME;
    final static String COLLECTION_NAME1="driver";
    final  String mongouri=connectivity.DATABASE_URI;
    String passwordString,encryptedPWD,UTAID,test;
    SharedPreferences sharedpref;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_driver);

        TextView forgotpwd= (TextView)findViewById(R.id.forgotpwd);
        final EditText UTA_ID =(EditText)findViewById(R.id.UTAID);
        final EditText pwd=(EditText)findViewById(R.id.Pwd);

        Button Login =(Button)findViewById(R.id.login);


        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        sharedpref=getSharedPreferences("data",MODE_PRIVATE);
        int number=sharedpref.getInt("isLogged",0);


        if(number==1){

            Intent i=new Intent(this,Start.class);
            i.putExtra("did",sharedpref.getString("did",null));
            startActivity(i);
            finish();
        }


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UTAID=UTA_ID.getText().toString();
                passwordString=pwd.getText().toString();
                encryptedPWD= md5(passwordString);

                try {
                    mongoClientURI = new MongoClientURI(mongouri);
                    mongoClient = new MongoClient(mongoClientURI);

                    DB db = mongoClient.getDB(DATABASE_NAME);
                    collection = db.getCollection(COLLECTION_NAME1);
                    DBCursor cursor = collection.find(new BasicDBObject("d_utaid", UTAID)
                            .append("d_pass", encryptedPWD).append("driver_status","working"));

                    if (cursor.count() == 0) {
                        Snackbar.make(view,"Invalid Details!",Snackbar.LENGTH_SHORT).show();
                       // Toast.makeText(getApplicationContext(), "Invalid Details", Toast.LENGTH_SHORT).show();
                    } else {
                        if (cursor.count() > 0) {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                  /*  StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                    StrictMode.setThreadPolicy(policy);*/

                                    try {



                                        mongoClient.close();



                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            SharedPreferences.Editor editor = sharedpref.edit();
                            editor.putString("did", UTAID);
                            editor.putInt("isLogged", 1);
                            editor.commit();


                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(LogInDriver.this,Start.class);
                        i.putExtra("did",UTAID);
                        startActivity(i);
                        finish();

                        }


                    }


                } catch (MongoTimeoutException mte) {
                    mte.printStackTrace();
                } catch (MongoIncompatibleDriverException mide) {
                    mide.printStackTrace();
                } catch (Exception exe) {
                    exe.printStackTrace();
                }
                mongoClient.close();

            }




        });

        forgotpwd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LogInDriver.this,ForgotPwd.class);
                startActivity(i);
            }
        });


    }
    private static final String md5(final String password) {
        try {

            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }



}

