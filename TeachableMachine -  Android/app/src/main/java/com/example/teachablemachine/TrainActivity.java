package com.example.teachablemachine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class TrainActivity extends AppCompatActivity {

    LinearLayout go,test,rotate,dropdown,hidden;
    String msg;
    EditText epochs,ip,lrate,batch;
    boolean trained = false;
    boolean dropped = false;
    ImageView dchange;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        go = (LinearLayout) findViewById(R.id.train);
        rotate = (LinearLayout) findViewById(R.id.rotate);
        dropdown = (LinearLayout) findViewById(R.id.dropdown);
        hidden = (LinearLayout) findViewById(R.id.hidden);
        epochs = (EditText) findViewById(R.id.epochs);
        ip = (EditText) findViewById(R.id.ip);
        batch = (EditText) findViewById(R.id.batch);
        lrate = (EditText) findViewById(R.id.lrate);
        dchange = (ImageView) findViewById(R.id.dchange);
        test = (LinearLayout)findViewById(R.id.test);


        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!dropped)
                {
                    hidden.setVisibility(View.VISIBLE);
                    dchange.setImageResource(R.drawable.up_arrow);
                    dropped=true;
                }
                else
                {
                    hidden.setVisibility(View.GONE);
                    dchange.setImageResource(R.drawable.down_arrow);
                    dropped=false;
                }
            }
        });

        final String config= getIntent().getStringExtra("config");
        final String path = getIntent().getStringExtra("path");
        final List<String> classes = getIntent().getStringArrayListExtra("classes");


        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg = FirebaseAuth.getInstance().getCurrentUser().getUid();

                try {

                    int eps = Integer.parseInt(epochs.getText().toString());
                    int btch = Integer.parseInt(batch.getText().toString());
                    float lrt = Float.parseFloat(lrate.getText().toString());

                    msg = msg + "," + "Images" + "," + eps + ","+btch +","+ lrt + "," + config;
                    trained = true;
                    go.setVisibility(View.GONE);
                    rotate.setVisibility(View.VISIBLE);

                    send sendargs = new send();
                    sendargs.execute();

                }
                catch (Exception e){
                    Toast.makeText(TrainActivity.this,"Please make valid entries in Epochs, Batch Size, Learning Rate(Decimal)",Toast.LENGTH_SHORT).show();
                }
            }
        });


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

                DataOutputStream dout=new DataOutputStream(clientsocket.getOutputStream());
                DataInputStream din=new DataInputStream(clientsocket.getInputStream());


                dout.writeUTF(msg);
                dout.flush();

                System.out.println("send first mess");
                final String str = din.readUTF();//in.readLine();

                System.out.println("Message"+str);

                dout.close();
                din.close();
                clientsocket.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(TrainActivity.this,"Training Completed...",Toast.LENGTH_SHORT).show();
            go.setVisibility(View.VISIBLE);
            rotate.setVisibility(View.GONE);
        }
    }


}
