package com.example.teachablemachine;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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

    LinearLayout go,test,dropdown,hidden;
    ProgressBar rotate;
    String msg;
    EditText epochs,ip,lrate,batch;
    boolean trained = false;
    boolean dropped = false;
    ImageView dchange;
    TextView progress;
    CheckBox withdownload;

    ImageView epoch_help,lr_help,batch_help;

    String[] help = new String[]{"An epoch is a hyperparameter which is defined before training a model. One epoch is when an entire dataset is passed both forward and backward through the neural network only once."
    ,"The learning rate is a tuning parameter in an optimization algorithm that determines the step size at each iteration while moving toward a minimum of a loss function.","Batch size is a term used in machine learning and refers to the number of training examples utilized in one iteration."};

    Dialog help_dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        go = (LinearLayout) findViewById(R.id.train);
        rotate = (ProgressBar) findViewById(R.id.rotate);
        dropdown = (LinearLayout) findViewById(R.id.dropdown);
        hidden = (LinearLayout) findViewById(R.id.hidden);
        epochs = (EditText) findViewById(R.id.epochs);
        ip = (EditText) findViewById(R.id.ip);
        batch = (EditText) findViewById(R.id.batch);
        lrate = (EditText) findViewById(R.id.lrate);
        dchange = (ImageView) findViewById(R.id.dchange);
        test = (LinearLayout)findViewById(R.id.test);
        progress = (TextView) findViewById(R.id.progress);
        withdownload = (CheckBox) findViewById(R.id.download);

        help_dialog = new Dialog(this);
        help_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        epoch_help = (ImageView) findViewById(R.id.epoch_help);
        lr_help = (ImageView) findViewById(R.id.lrate_help);
        batch_help = (ImageView) findViewById(R.id.batch_help);

        epoch_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                help_dialog.setContentView(R.layout.help_dialog);
                TextView helptext =  (TextView) help_dialog.findViewById(R.id.help_text);
                helptext.setText(help[0]);
                help_dialog.show();
            }
        });

        lr_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                help_dialog.setContentView(R.layout.help_dialog);
                TextView helptext =  (TextView) help_dialog.findViewById(R.id.help_text);
                helptext.setText(help[1]);
                help_dialog.show();
            }
        });

        batch_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                help_dialog.setContentView(R.layout.help_dialog);
                TextView helptext =  (TextView) help_dialog.findViewById(R.id.help_text);
                helptext.setText(help[2]);
                help_dialog.show();
            }
        });

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
                    rotate.setVisibility(View.VISIBLE);
                    test.setEnabled(false);

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
                i.putExtra("trained",(trained || withdownload.isChecked()));
                startActivity(i);
            }
        });



    }

    public class send extends AsyncTask<Void, Void, Void> {

        Socket clientsocket;
        @Override
        protected Void doInBackground(Void... voids) {

            try{

                clientsocket = new Socket(ip.getText().toString(),8080);

                DataOutputStream dout=new DataOutputStream(clientsocket.getOutputStream());
                DataInputStream din=new DataInputStream(clientsocket.getInputStream());


                dout.writeUTF(msg);
                dout.flush();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.setText("Request Sent...");
                    }
                });

                boolean exitflag = true;
                while(exitflag)
                {
                    final String str = din.readUTF();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setText(str);
                        }
                    });

                    if(str.equals("Training Completed")){
                        exitflag= false;
                    }
                }


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
            rotate.setVisibility(View.GONE);
            test.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
