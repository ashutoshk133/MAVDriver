package com.techwarriors.mavdriver;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class AssignRide extends AppCompatActivity {

    Connectivity connectivity;
    private MongoClient mongoClient;
    private MongoClientURI mongoClientURI;
    private DBCollection requestcollection;
    final String DATABASE_NAME=connectivity.DATABASE_NAME;
    final static String COLLECTION_NAME1="request";
    final  String mongouri=connectivity.DATABASE_URI;
    String rid,did,source,destination,riders;
    final Timer timer=new Timer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_ride);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final ProgressBar progressBar=(ProgressBar)findViewById(R.id.progressBar);

        Intent i2=getIntent();
        did=i2.getStringExtra("did");
        mongoClientURI = new MongoClientURI(mongouri);
        mongoClient = new MongoClient(mongoClientURI);
        DB db = mongoClient.getDB(DATABASE_NAME);
        requestcollection = db.getCollection(COLLECTION_NAME1);




        long delay=10;
        long intervalPeriod = 2000;
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {

                try{
                    DBCursor cur = requestcollection.find();
                    cur.sort(new BasicDBObject("_id", 1)).limit(1);
                  if(cur.count()>0){
                    while (cur.hasNext()) {
                        BasicDBObject dbobj = (BasicDBObject) cur.next();
                        rid = dbobj.get("r_utaid").toString();
                        source = dbobj.get("source").toString();
                        destination = dbobj.get("destination").toString();
                        riders = dbobj.get("no_of_riders").toString();

                        BasicDBObject set = new BasicDBObject("$set", new BasicDBObject("status", "assigned").append("d_utaid",did));
                        // set.append("$set", new BasicDBObject("name", "Some Name"));
                        requestcollection.update(new BasicDBObject("r_utaid",rid), set);


                      //  requestcollection.remove(dbobj);
                        Intent i = new Intent(AssignRide.this, CurrentPickup.class);
                        i.putExtra("rid", rid);
                        i.putExtra("source", source);
                        i.putExtra("destination", destination);
                        i.putExtra("riders", riders);
                        i.putExtra("did",did);
                        startActivity(i);
                        timer.cancel();
                        finish();

                    }

                    }
                  else
                  {
                      Toast.makeText(getApplicationContext(),"looking for riders",Toast.LENGTH_SHORT).show();
                  }


                }
                catch (Exception e)
                {
                    Log.d("Assign",e.toString());
                }

            }
        };



        timer.scheduleAtFixedRate(timerTask, delay, intervalPeriod);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent start=new Intent(AssignRide.this,Start.class);
        timer.cancel();
        start.putExtra("did",did);
        startActivity(start);
        finish();
    }
}
