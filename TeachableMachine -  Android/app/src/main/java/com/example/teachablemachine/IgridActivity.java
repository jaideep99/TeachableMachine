package com.example.teachablemachine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IgridActivity extends AppCompatActivity {

    private String path,classname;

    private TextView classtitle;
    private LinearLayout addImage,picker;
    private GridView imageGrid;
    private ImageAdapter adapter;
    private Dialog delete_dialog;

    private TextView filename;
    private LinearLayout deletebtn;

    List<Bitmap> itemList = new ArrayList<Bitmap>();
    List<String> sItemList = new ArrayList<String>();
    Uri OutputUri;

    String local_path="";
    String fpath = "";
    String project = "";
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final int CAMERA_REQUEST = 9999;

    int to_delete = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igrid);

        createCache();
        local_path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/TeachableMachine/"+uid+"/Cache";

        path = (String) getIntent().getStringExtra("path");
        classname = (String) getIntent().getStringExtra("classname");
        project = (String) getIntent().getStringExtra("project");
        fpath = (String) getIntent().getStringExtra("fpath");


        classtitle = (TextView) findViewById(R.id.classTitle);
        addImage = (LinearLayout) findViewById(R.id.addimage);
        picker = (LinearLayout) findViewById(R.id.picker);
        classtitle.setText(classname);

        delete_dialog =  new Dialog(this);
        delete_dialog.setContentView(R.layout.delete_dialog);

        filename = (TextView) delete_dialog.findViewById(R.id.foldername);
        deletebtn = (LinearLayout) delete_dialog.findViewById(R.id.delete);

        loadBitmaps();

        imageGrid = (GridView) findViewById(R.id.imagegrid);
        adapter = new ImageAdapter(IgridActivity.this,itemList);
        imageGrid.setAdapter(adapter);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ActivityCompat.checkSelfPermission(IgridActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(IgridActivity.this,
                            new String[]{Manifest.permission.CAMERA},121);
                    return;
                }

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File imagesFolder = new File(local_path);
                File newfile = new File(imagesFolder, "take.jpg");
                OutputUri = FileProvider.getUriForFile(IgridActivity.this,"com.example.teachablemachine.fileprovider", newfile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, OutputUri);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });

        imageGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                to_delete = i;
                filename.setText(sItemList.get(i));
                delete_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                delete_dialog.show();

                return true;
            }
        });

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File f = new File(path+"/"+sItemList.get(to_delete));
                String fbpath = fpath+"/"+sItemList.get(to_delete);
                f.delete();
                itemList.remove(to_delete);
                sItemList.remove(to_delete);
                to_delete = -1;

                StorageReference delref = FirebaseStorage.getInstance().getReference().child(fbpath);
                delref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(IgridActivity.this,"Deleted Image Successfully",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(IgridActivity.this,"Deletion Failed!",Toast.LENGTH_SHORT).show();
                    }
                });

                adapter.notifyDataSetChanged();
                imageGrid.invalidateViews();
                imageGrid.setAdapter(adapter);
                delete_dialog.dismiss();
            }
        });

        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(IgridActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(IgridActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
                }

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setType("image/*");
                startActivityForResult(intent,1);


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST) {

            File imagesFolder = new File(path);
            String ntemp = "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
            File newfile = new File(imagesFolder,ntemp);

            Bitmap photo = (Bitmap) BitmapFactory.decodeFile(local_path+"/take.jpg");
            photo = Bitmap.createScaledBitmap(photo, 256, 256, false);
            photo = getRBitmap(local_path+"/take.jpg",photo);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            FileOutputStream fo = null;
            try {
                newfile.createNewFile();
                fo = new FileOutputStream(newfile);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap temp = BitmapFactory.decodeFile(path+"/"+ntemp);
            upload(path,ntemp);
            itemList.add(temp);
            sItemList.add(ntemp);

            adapter.notifyDataSetChanged();
            imageGrid.invalidateViews();
            imageGrid.setAdapter(adapter);
        }

        if(requestCode == 1 && resultCode==RESULT_OK)
        {
            List<Bitmap> bits = new ArrayList<>();
            ClipData clip = data.getClipData();

            if(clip!=null)
            {
                for(int i=0;i<clip.getItemCount();i++)
                {
                    Uri imageUri = clip.getItemAt(i).getUri();
                    try{
                        InputStream is = getContentResolver().openInputStream(imageUri);
                        Bitmap bit = BitmapFactory.decodeStream(is);
                        bits.add(bit);
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                Uri img = data.getData();
                try{
                    InputStream is = getContentResolver().openInputStream(img);
                    Bitmap bit = BitmapFactory.decodeStream(is);
                    bits.add(bit);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            apply(bits);

        }

    }

    void apply(final List<Bitmap> bits)
    {
        final File imagesFolder = new File(path);
        final List<String> temps = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<bits.size();i++)
                {
                    final String ntemp = "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                    File newfile = new File(imagesFolder,ntemp);
                    Bitmap b = bits.get(i);
                    b = Bitmap.createScaledBitmap(b, 256, 256, false);

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    FileOutputStream fo = null;
                    try {
                        newfile.createNewFile();
                        fo = new FileOutputStream(newfile);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Bitmap temp = BitmapFactory.decodeFile(path+"/"+ntemp);
//                    upload(path,ntemp);
                    itemList.add(temp);
                    sItemList.add(ntemp);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            upload(path,ntemp);
                        }
                    }).start();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        imageGrid.invalidateViews();
                        imageGrid.setAdapter(adapter);
                    }
                });
            }
        }).start();


    }


    private void upload(String path,String ntemp) {

        String npath = uid+"/Images/"+project+"/"+classname+"/"+ntemp;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference();
        StorageReference imgref = ref.child(npath);

        Uri uri = Uri.fromFile(new File(path+"/"+ntemp));
        imgref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //Toast.makeText(IgridActivity.this,"upload successful",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(IgridActivity.this, e.getCause().getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }

    public void createCache()
    {
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/TeachableMachine/"+uid+"/Cache");
        if(!root.exists()){
            root.mkdir();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public Bitmap getRBitmap(String photoPath,Bitmap bitmap)
    {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }

        return rotatedBitmap;
    }

    public void loadBitmaps()
    {
        File myDirectory = new File(path);
        File[] directories = myDirectory.listFiles();


        if(directories!=null){
            for(int i=0;i<directories.length;i++){
                sItemList.add(directories[i].getName());
                Bitmap temp =  BitmapFactory.decodeFile(path+"/"+directories[i].getName());
                itemList.add(temp);
            }
        }

    }




}
