package com.example.teachablemachine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class TrainActivity extends AppCompatActivity {

    LinearLayout go,test;
    String msg;
    EditText epochs,ip;
    boolean trained = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        go = (LinearLayout) findViewById(R.id.train);
        epochs = (EditText) findViewById(R.id.epochs);
        ip = (EditText) findViewById(R.id.ip);

        final String config= getIntent().getStringExtra("config");
        final String path = getIntent().getStringExtra("path");
        final List<String> classes = getIntent().getStringArrayListExtra("classes");
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg = FirebaseAuth.getInstance().getCurrentUser().getUid();
                msg = msg + ","+"Images"+","+epochs.getText().toString()+","+config;
                trained = true;

                send sendargs = new send();
                sendargs.execute();
            }
        });

        test = (LinearLayout)findViewById(R.id.test);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =  new Intent(TrainActivity.this,TestActivity.class);
                i.putStringArrayListExtra("classes", (ArrayList<String>) classes);
                i.putExtra("path",path);
                i.putExtra("trained",trained);
                startActivity(i);
            }
        });



    }

    public class send extends AsyncTask<Void, Void, Void> {

        Socket clientsocket;
        PrintWriter pw;
        @Override
        protected Void doInBackground(Void... voids) {

            try{
                clientsocket = new Socket(ip.getText().toString(),8080);
                pw = new PrintWriter(new OutputStreamWriter(clientsocket.getOutputStream()));
                pw.write(msg);
                pw.flush();
                pw.close();
                clientsocket.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
