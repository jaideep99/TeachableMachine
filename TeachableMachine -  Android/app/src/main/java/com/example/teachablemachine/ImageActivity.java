package com.example.teachablemachine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity implements ClassAdapter.OnItemClickListener{

    private TextView title;
    private LinearLayout train;
    private FloatingActionButton create;
    private RecyclerView classlist;
    private Dialog delete_dialog;
    private RecyclerView.LayoutManager layoutManager;
    String path;
    String name;

    List<String> itemList = new ArrayList<String>();

    int classcount = 1;

    private ClassAdapter adapter;

    private TextView foldername;
    private LinearLayout deletebtn;
    int to_delete = -1;
    String fpath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        path = (String) getIntent().getStringExtra("path");
        name = (String) getIntent().getStringExtra("projectname");
        fpath = (String) getIntent().getStringExtra("fpath");

        title = (TextView) findViewById(R.id.title);
        train = (LinearLayout) findViewById(R.id.train);
        create = (FloatingActionButton) findViewById(R.id.addclass);
        title.setText(name);

        loadfolders();

        classlist = (RecyclerView) findViewById(R.id.classes);
        layoutManager = new LinearLayoutManager(this);
        classlist.setLayoutManager(layoutManager);
        adapter = new ClassAdapter(itemList);
        classlist.setAdapter(adapter);
        adapter.setOnItemClickListener(ImageActivity.this);


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File newf = new File(path+"/Class"+classcount);
                if(!newf.exists()){
                    newf.mkdir();
                }
                else {
                    Toast.makeText(ImageActivity.this,"Project Name Already Taken!",Toast.LENGTH_SHORT).show();
                }
                classcount++;

                checkfolders();
            }
        });

        delete_dialog = new Dialog(this);
        delete_dialog.setContentView(R.layout.delete_dialog);

        foldername = (TextView) delete_dialog.findViewById(R.id.foldername);
        deletebtn = (LinearLayout) delete_dialog.findViewById(R.id.delete);

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(to_delete!=-1) {
                    File del = new File(path + "/" + itemList.get(to_delete));
                    deleteRecursive(del);
                    itemList.remove(to_delete);
                    to_delete = -1;
                    adapter.notifyDataSetChanged();
                    delete_dialog.dismiss();
                    classcount--;

                    final String  fbpath = fpath+"/"+itemList.get(to_delete);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            StorageReference listref = FirebaseStorage.getInstance().getReference().child(fbpath);
                            deletefb(listref);
                        }
                    }).start();

                    Toast.makeText(ImageActivity.this,"Class Deleted Sucessfully!!",Toast.LENGTH_SHORT).show();
                }



            }
        });

        train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ImageActivity.this,TrainActivity.class);
                String msg = name+createClassStr();
                i.putExtra("config",msg);
                i.putExtra("path",path);
                i.putStringArrayListExtra("classes", (ArrayList<String>) itemList);
                startActivity(i);
            }
        });


    }


    void deletefb(StorageReference listref)
    {
        listref.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {

                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.

                            deletefb(prefix);
                        }

                        final int[] pp = {0};
                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            item.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pp[0]++;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                }
                            });
                        }
                    }
                });
    }

    public String createClassStr(){
        String classes = "";

        File myDirectory = new File(path);
        File[] directories = myDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        if(directories!=null){
            for(int i=0;i<directories.length;i++){
                classes = classes+","+directories[i].getName();
            }
        }

        return classes;
    }


    public void loadfolders(){
        File myDirectory = new File(path);
        File[] directories = myDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        if(directories!=null){
            for(int i=0;i<directories.length;i++){
                itemList.add(directories[i].getName());
            }
        }

        classcount = itemList.size()+1;

    }

    public void checkfolders(){
        File myDirectory = new File(path);
        File[] directories = myDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        if(directories!=null){
            for(int i=0;i<directories.length;i++){
                if(!itemList.contains(directories[i].getName()))
                    itemList.add(directories[i].getName());
            }
        }

        classcount = itemList.size()+1;
        adapter.notifyDataSetChanged();
    }


    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(ImageActivity.this,IgridActivity.class);
        i.putExtra("path",path+"/"+itemList.get(position));
        i.putExtra("classname",itemList.get(position));
        i.putExtra("project",name);
        i.putExtra("fpath",fpath+"/"+itemList.get(position));
        startActivity(i);

    }

    @Override
    public void deleteClick(int position) {
        to_delete = position;
        foldername.setText(itemList.get(to_delete));
        delete_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        delete_dialog.show();

    }

    @Override
    public void onRename(int position, String newname) {

        File olds  = new File(path+"/"+itemList.get(position));
        File news  = new File(path+"/"+newname);

        try {
            olds.renameTo(news);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        itemList.set(position,newname);
        adapter.notifyDataSetChanged();

    }
}
