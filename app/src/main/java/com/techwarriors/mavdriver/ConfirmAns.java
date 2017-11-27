package com.techwarriors.mavdriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmAns extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_ans);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i=getIntent();
        final String utaid=i.getStringExtra("did");
        String secque=i.getStringExtra("secque");
        final String secans=i.getStringExtra("secans");

        final EditText etsecans=(EditText)findViewById(R.id.etsecans);
        TextView tvsecque=(TextView)findViewById(R.id.tvsecque);
        tvsecque.setText(secque);
        TextView tvutaid=(TextView)findViewById(R.id.utaid2);
        tvutaid.setText(utaid);

        Button verify=(Button)findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newans=etsecans.getText().toString().toLowerCase();
                if(newans.equals(secans))
                {
                    Intent reset=new Intent(ConfirmAns.this,ResetPass.class);
                    reset.putExtra("did",utaid);
                    startActivity(reset);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Invalid Details",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
