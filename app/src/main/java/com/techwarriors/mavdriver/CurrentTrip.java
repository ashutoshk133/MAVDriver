package com.techwarriors.mavdriver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static java.lang.Integer.parseInt;

public class CurrentTrip extends AppCompatActivity {

    private MongoClient mongoClient;
    private MongoClientURI mongoClientURI;
    private DBCollection tripcollection;
    // final static String DATABASE_NAME="mav";
    final static String TRIP_COLLECTION ="trip";
  //  final static String REQ_COLLECTION="request";
    //final static String mongouri="mongodb://192.168.43.71:27017";
    int tripid;
    String rid,source,destination,riders,did;
    Connectivity connectivity;
    String date,starttime,endtime,durationString;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
         date = df.format(Calendar.getInstance().getTime());

       // Calendar.getInstance(TimeZone.getTimeZone("GMT-6:00"));
        final long currentLocalTime = System.currentTimeMillis();
        final Timestamp start = new Timestamp(currentLocalTime);
        starttime=start.toString();


        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);





        sp=getSharedPreferences("data",MODE_PRIVATE);
        final String did=sp.getString("did", null);


        Intent i=getIntent();

        rid=i.getStringExtra("rid");
        source=i.getStringExtra("source");
        destination=i.getStringExtra("destination");
        riders=i.getStringExtra("riders");
        //did=i.getStringExtra("did");

        TextView tvsrc=(TextView)findViewById(R.id.tvsrc1);
        TextView tvdes=(TextView)findViewById(R.id.tvdes1);
        TextView tvriders=(TextView)findViewById(R.id.tvriders);

        tvsrc.setText(source);
        tvdes.setText(destination);
        tvriders.setText(riders);



        FloatingActionButton sos= (FloatingActionButton)findViewById(R.id.SOS);
        sos.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:123456789"));
                startActivity(callIntent);

            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                long currentLocalTime2 = System.currentTimeMillis();
                TimeZone tz = TimeZone.getDefault();

                Timestamp end =  new Timestamp(currentLocalTime2);
                endtime=end.toString();

                long duration = (currentLocalTime2 - currentLocalTime)/1000;
                durationString=Long.toString(duration);


                try {
                    mongoClientURI = new MongoClientURI(connectivity.DATABASE_URI);
                    mongoClient = new MongoClient(mongoClientURI);
                    DB db = mongoClient.getDB(connectivity.DATABASE_NAME);
                    tripcollection = db.getCollection(TRIP_COLLECTION);
                    DBCursor tripcur = tripcollection.find();

                    tripcur.sort(new BasicDBObject("_id", -1)).limit(1);
                    while (tripcur.hasNext()) {
                        BasicDBObject dbObject = (BasicDBObject) tripcur.next();
                        String tripidString = dbObject.get("trip_id").toString();
                        tripid=parseInt(tripidString);


                    }
                    tripid = tripid + 1;
                    BasicDBObject newnoshow = new BasicDBObject("trip_id", tripid).append("start_time", starttime)
                            .append("end_time", endtime).append("source", source).append("destination", destination)
                            .append("no_of_riders", riders).append("d_utaid", did).append("status", "Completed")
                            .append("duration", durationString).append("date",date).append("r_utaid",rid);
                    tripcollection.insert(newnoshow);
                    mongoClient.close();

                    Toast.makeText(getApplicationContext(),"Ride Complete",Toast.LENGTH_SHORT).show();
                    Intent start=new Intent(CurrentTrip.this,Start.class);
                    start.putExtra("did",did);
                    startActivity(start);
                    finish();


                }
                catch (Exception e)
                {
                    Log.d("noshow",e.toString());
                }



            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"You Can't Go Back!",Toast.LENGTH_SHORT).show();

    }
}
