package com.example.teachablemachine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TestActivity extends AppCompatActivity {

    ImageView input;
    ImageView click,picker;
    TextView output;


    static final int REQUEST_IMAGE_CAPTURE = 1;
    List<String> classes;
    String path;
    String name;

    RecyclerView recycler;
    LinearLayoutManager manager;
    ProbAdapter adapter;

    int nclasses = 2;

    public static final int PICK_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        input = (ImageView) findViewById(R.id.image);
        click = (ImageView) findViewById(R.id.click);
        picker = (ImageView) findViewById(R.id.filepicker);
        output = (TextView) findViewById(R.id.output);

        classes = getIntent().getStringArrayListExtra("classes");
        path = getIntent().getStringExtra("path");
        boolean istrained = getIntent().getBooleanExtra("trained",false);

        recycler = (RecyclerView) findViewById(R.id.recycler);



        click.setEnabled(false);
        String[] paths = path.split("/");
        name = paths[7];

        nclasses = classes.size();

        if(istrained){
            Toast.makeText(TestActivity.this,"Model is downloading...",Toast.LENGTH_SHORT).show();
            download(path);
        }

        click.setEnabled(true);

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ActivityCompat.checkSelfPermission(TestActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(TestActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);

                }

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

    }

    private void download(String path) {
        String[] paths = path.split("/");
        String npath = paths[5]+"/"+paths[6]+"/model/"+paths[7]+".tflite";
        StorageReference root = FirebaseStorage.getInstance().getReference();
        StorageReference ref = root.child(npath);

        name = paths[7];
        Uri uri = Uri.parse(path+"/"+paths[7]+".tflite");

        final Dialog md = new Dialog(this);
        md.setCanceledOnTouchOutside(false);
        md.setContentView(R.layout.model_dialog);

        md.show();

        ref.getFile(uri).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(TestActivity.this,"File downloaded.",Toast.LENGTH_SHORT).show();
                md.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                md.dismiss();
                Toast.makeText(TestActivity.this,"File download failed. or No models present!",Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap,256,256,false);
            input.setImageBitmap(imageBitmap);

            to_model(imageBitmap);
        }

        if(requestCode == PICK_IMAGE && resultCode==RESULT_OK)
        {
            Uri filePath = data.getData();

            try {
                Bitmap img=MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                img = Bitmap.createScaledBitmap(img,256,256,false);
                input.setImageBitmap(img);

                to_model(img);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    void to_model(Bitmap bitmap){


        int batchNum = 0;
        float[][][][] input = new float[1][256][256][3];
        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 256; y++) {
                int pixel = bitmap.getPixel(x, y);
                // Normalize channel values to [-1.0, 1.0]. This requirement varies by
                // model. For example, some models might require values to be normalized
                // to the range [0.0, 1.0] instead.
                input[batchNum][x][y][0] = (Color.red(pixel)) / 255.0f;
                input[batchNum][x][y][1] = (Color.green(pixel)) / 255.0f;
                input[batchNum][x][y][2] = (Color.blue(pixel)) / 255.0f;
            }
        }

        try {
            FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder().setFilePath(path+"/"+name+".tflite").build();
            FirebaseModelInterpreter interpreter;
            FirebaseModelInterpreterOptions options =
                    new FirebaseModelInterpreterOptions.Builder(localModel).build();
            interpreter = FirebaseModelInterpreter.getInstance(options);

            FirebaseModelInputs inputs = null;
            inputs = new FirebaseModelInputs.Builder()
                    .add(input)  // add() as many input arrays as your model requires
                    .build();


            FirebaseModelInputOutputOptions inputOutputOptions;
            inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
                    .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 256, 256, 3})
                    .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, nclasses})
                    .build();

            interpreter.run(inputs, inputOutputOptions)
                    .addOnSuccessListener(
                            new OnSuccessListener<FirebaseModelOutputs>() {
                                @Override
                                public void onSuccess(FirebaseModelOutputs result) {
                                    Toast.makeText(TestActivity.this,"Task Success",Toast.LENGTH_SHORT).show();

                                    float[][] out = result.getOutput(0);

                                    float[] probabilities = out[0];

                                    int index = 0;
                                    float max = probabilities[0];
                                    String probs = "";

                                    List<probitem> probitems = new ArrayList<probitem>();

                                    for(int i=0;i<probabilities.length;i++){
                                        if(max<probabilities[i]){
                                            max = probabilities[i];
                                            index = i;
                                        }

                                        probs = probs+probabilities[i]+" : ";

                                        probitems.add(new probitem(classes.get(i),probabilities[i]));
                                    }

                                    output.setText(classes.get(index));
                                    adapter = new ProbAdapter(probitems);
                                    manager = new LinearLayoutManager(TestActivity.this);
                                    recycler.setLayoutManager(manager);
                                    recycler.setAdapter(adapter);

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(TestActivity.this,"Task failed",Toast.LENGTH_SHORT).show();

                                }
                            });

        }
        catch (FirebaseMLException e)
        {
            Toast.makeText(TestActivity.this,"Firebase Error",Toast.LENGTH_SHORT).show();
            Log.d("Test",e.getMessage());
        }
    }
}
