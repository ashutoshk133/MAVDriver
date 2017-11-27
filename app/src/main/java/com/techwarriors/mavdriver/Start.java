package com.techwarriors.mavdriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class Start extends AppCompatActivity {

    Connectivity connectivity;
    private MongoClient mongoClient;
    private MongoClientURI mongoClientURI;
    private DBCollection collection;
    final  String DATABASE_NAME=connectivity.DATABASE_NAME;
    final static String COLLECTION_NAME1="driver";
    final String mongouri=connectivity.DATABASE_URI;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



              Button b1=(Button)findViewById(R.id.AcceptTrips);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

       /*Intent intent = getIntent();
       String UTAID=intent.getStringExtra("did");*/



        mongoClientURI = new MongoClientURI(mongouri);
        mongoClient = new MongoClient(mongoClientURI);
        DB db = mongoClient.getDB(DATABASE_NAME);
        collection = db.getCollection(COLLECTION_NAME1);
        //final DBCursor firstcursor = collection.find(new BasicDBObject("d_utaid", UTAID));



        sp=getSharedPreferences("data",MODE_PRIVATE);
        final String UTAID=sp.getString("did", null);

        try {


            BasicDBObject set = new BasicDBObject("$set", new BasicDBObject("driver_isavailable", "true"));
            // set.append("$set", new BasicDBObject("name", "Some Name"));
            collection.update(new BasicDBObject("d_utaid",UTAID), set);


            //  collection.insert(new BasicDBObject("d_utaid",UTAID),dbObject);
            //DBCursor cursorall = not.find(new BasicDBObject("uta_id", "ALL"));
            // count = cursor.count() + cursorall.count();
            // Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_LONG).show();
            mongoClient.close();




        } catch (Exception e) {
            e.printStackTrace();
        }





        Button logout=(Button)findViewById(R.id.LogOut);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sp=getSharedPreferences("data",MODE_PRIVATE);

                SharedPreferences.Editor editor=sp.edit();
                editor.putInt("isLogged", 0);
                editor.commit();
                Intent i =new Intent(Start.this,LogInDriver.class);
                startActivity(i);
                finish();

            }
        });


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mongoClientURI = new MongoClientURI(mongouri);
                mongoClient = new MongoClient(mongoClientURI);
                DB db = mongoClient.getDB(DATABASE_NAME);
                collection = db.getCollection(COLLECTION_NAME1);
                final DBCursor cursor = collection.find(new BasicDBObject("d_utaid", UTAID));
                if (cursor.count() == 0) {

                    Toast.makeText(getApplicationContext(), "Invalid Details", Toast.LENGTH_SHORT).show();
                } else {
                    if (cursor.count() > 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                  /*  StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                    StrictMode.setThreadPolicy(policy);*/

                                try {


                                    BasicDBObject set = new BasicDBObject("$set", new BasicDBObject("driver_isavailable", "false"));
                                   // set.append("$set", new BasicDBObject("name", "Some Name"));
                                    collection.update(new BasicDBObject("d_utaid",UTAID), set);


                                  //  collection.insert(new BasicDBObject("d_utaid",UTAID),dbObject);
                                    //DBCursor cursorall = not.find(new BasicDBObject("uta_id", "ALL"));
                                    // count = cursor.count() + cursorall.count();
                                    // Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_LONG).show();
                                    mongoClient.close();
                                    Intent i=new Intent(Start.this,AssignRide.class);
                                    i.putExtra("did",UTAID);
                                    startActivity(i);
                                    finish();


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }







            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
