package com.techwarriors.mavdriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.lang.Integer.parseInt;

public class CurrentPickup extends AppCompatActivity {

    private MongoClient mongoClient;
    private MongoClientURI mongoClientURI;
    private DBCollection tripcollection,requestcollection;
   //final static String DATABASE_NAME="mav";
    final static String TRIP_COLLECTION ="trip";
    final static String REQ_COLLECTION="request";
    //final static String mongouri="mongodb://192.168.43.71:27017";
    int tripid=0;
    String rid,source,destination,riders,did;
    Connectivity connectivity;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_pickup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i=getIntent();
        sp=getSharedPreferences("data",MODE_PRIVATE);
        final String did=sp.getString("did", null);
        rid=i.getStringExtra("rid");
        source=i.getStringExtra("source");
        destination=i.getStringExtra("destination");
        riders=i.getStringExtra("riders");
       // did=i.getStringExtra("did");
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        final String date = df.format(Calendar.getInstance().getTime());

        TextView tvsrc=(TextView)findViewById(R.id.tvsrc);
        TextView tvdes=(TextView)findViewById(R.id.tvdes);
        TextView tvriders=(TextView)findViewById(R.id.riders);

        tvsrc.setText(source);
        tvdes.setText(destination);
        tvriders.setText(riders);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        final FloatingActionButton fabPickup = (FloatingActionButton) findViewById(R.id.Pickup);
        FloatingActionButton fabNoshow = (FloatingActionButton) findViewById(R.id.NoShow);


        fabPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mongoClientURI = new MongoClientURI(connectivity.DATABASE_URI);
                mongoClient = new MongoClient(mongoClientURI);
                DB db = mongoClient.getDB(connectivity.DATABASE_NAME);
                requestcollection = db.getCollection(REQ_COLLECTION);


                try{

                    DBCursor cur = requestcollection.find(new BasicDBObject("r_utaid",rid));
                    while (cur.hasNext()){
                        BasicDBObject dbobj = (BasicDBObject) cur.next();
                        requestcollection.remove(dbobj);
                    }
                    mongoClient.close();



                }
                catch(Exception e)
                {

                    Log.d("pickup",e.toString());

                }



                Intent trip=new Intent(CurrentPickup.this,CurrentTrip.class);
                trip.putExtra("source",source);
                trip.putExtra("destination",destination);
                trip.putExtra("rid",rid);
                trip.putExtra("riders",riders);
                trip.putExtra("did",did);
                startActivity(trip);
                finish();

            }
        });


        fabNoshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

new Thread(new Runnable() {
    @Override
    public void run() {


//Delete Request
        mongoClientURI = new MongoClientURI(connectivity.DATABASE_URI);
        mongoClient = new MongoClient(mongoClientURI);
        DB db = mongoClient.getDB(connectivity.DATABASE_NAME);
        requestcollection = db.getCollection(REQ_COLLECTION);


        try{

            DBCursor reqcur = requestcollection.find(new BasicDBObject("r_utaid",rid));
            while (reqcur.hasNext()){
                BasicDBObject dbobj = (BasicDBObject) reqcur.next();
                requestcollection.remove(dbobj);
            }
            //mongoClient.close();



        }
        catch(Exception e)
        {

            Log.d("pickup",e.toString());

        }







        //Handle noshow


       // mongoClientURI = new MongoClientURI(connectivity.DATABASE_URI);
       // mongoClient = new MongoClient(mongoClientURI);
      //  DB db1 = mongoClient.getDB(connectivity.DATABASE_NAME);
        tripcollection = db.getCollection(TRIP_COLLECTION);


        try {
            DBCursor tripcur = tripcollection.find();

            tripcur.sort(new BasicDBObject("_id", -1)).limit(1);
            while (tripcur.hasNext()) {
                BasicDBObject dbObject = (BasicDBObject) tripcur.next();
                String tripidString = dbObject.get("trip_id").toString();
                tripid=parseInt(tripidString);


                }
                tripid = tripid + 1;
                BasicDBObject newnoshow = new BasicDBObject("trip_id", tripid).append("start_time", "NOSHOW")
                        .append("end_time", "NOSHOW").append("source", source).append("destination", destination)
                        .append("no_of_riders", riders).append("d_utaid", did).append("status", "NOSHOW")
                        .append("duration", "0").append("date",date).append("r_utaid",rid);
                tripcollection.insert(newnoshow);
            mongoClient.close();
            Intent start=new Intent(CurrentPickup.this,Start.class);
            start.putExtra("did",did);
            startActivity(start);
            finish();


        }
        catch (Exception e)
        {
            Log.d("noshow",e.toString());
        }


    }
}).start();





               
            }
        });
    }

    @Override
    public void onBackPressed() {
       // Snackbar.make(view,"Error : Same Source and Destination", Snackbar.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),"You Can't Go Back!",Toast.LENGTH_SHORT).show();
    }
}
