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
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ProjectActivity extends AppCompatActivity implements ProjectAdapter.OnItemClickListener {

    private RelativeLayout addbtn;
    private RecyclerView projectlist;
    private RecyclerView.LayoutManager layoutManager;
    private ProjectAdapter adapter;
    private List<String> itemList = new ArrayList<String>();

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private Dialog create_dialog,delete_dialog;
    private EditText projectname;
    private LinearLayout savebtn,deletebtn;
    private TextView foldername;
    String category;

    int to_delete = -1;

    String path;

    Dialog downdialog;

    String flag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        category = (String) getIntent().getStringExtra("Category");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        path = Environment.getExternalStorageDirectory().toString()+"/TeachableMachine/"+uid+"/"+category;



        addbtn = (RelativeLayout) findViewById(R.id.addproject);
        projectlist = (RecyclerView) findViewById(R.id.projectlist);

        create_dialog = new Dialog(this);
        create_dialog.setContentView(R.layout.create_dialog);

        delete_dialog = new Dialog(this);
        delete_dialog.setContentView(R.layout.delete_dialog);

        projectname = (EditText) create_dialog.findViewById(R.id.projectname);
        savebtn = (LinearLayout) create_dialog.findViewById(R.id.save);

        foldername = (TextView) delete_dialog.findViewById(R.id.foldername);
        deletebtn = (LinearLayout) delete_dialog.findViewById(R.id.delete);

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(to_delete!=-1) {
                    final String fbpath = uid+"/"+category + "/" + itemList.get(to_delete);
                    final String dbpath = "Users/"+uid+"/Projects/"+category;
                    final String delname = itemList.get(to_delete);
                    File del = new File(path + "/" + itemList.get(to_delete));
                    deleteRecursive(del);
                    itemList.remove(to_delete);
                    to_delete = -1;
                    adapter.notifyDataSetChanged();
                    delete_dialog.dismiss();


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            StorageReference listref = FirebaseStorage.getInstance().getReference().child(fbpath);
                            deletefb(listref);
                            deletedb(dbpath,delname);
                        }
                    }).start();

                    Toast.makeText(ProjectActivity.this,"Project Deleted Sucessfully!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        layoutManager = new LinearLayoutManager(this);
        adapter = new ProjectAdapter(itemList);
        projectlist.setLayoutManager(layoutManager);
        projectlist.setAdapter(adapter);
        adapter.setOnItemClickListener(ProjectActivity.this);

        loadfolders();

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                create_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                create_dialog.show();
            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String projname = projectname.getText().toString();
                projectname.setText("");
                File newf = new File(path+"/"+projname);
                if(!newf.exists()){
                    newf.mkdir();
                    adddb(projname);
                }
                else {
                    Toast.makeText(ProjectActivity.this,"Project Name Already Taken!",Toast.LENGTH_SHORT).show();
                }
                create_dialog.dismiss();
            }
        });


    }

    void deletedb(String ref, final String dname)
    {
        final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child(ref);

        final List<String> pjs = new ArrayList<>();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds :  dataSnapshot.getChildren()){

                    if(!dname.equals((String) ds.getValue()))
                    {
                        Log.d("fbdata",(String) ds.getValue());
                        pjs.add((String) ds.getValue());
                    }

                }

                mDatabase.setValue(pjs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void adddb(final String pname)
    {
        final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users/"+uid+"/Projects/"+ category);

        final List<String> pjs = new ArrayList<>();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds :  dataSnapshot.getChildren()){
                    pjs.add((String) ds.getValue());
                    Log.d("fbdata", String.valueOf(ds.getValue()));
                }

                pjs.add(pname);
                mDatabase.setValue(pjs);

                itemList.add(pname);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    void RecDownload(StorageReference ref, final String path, final int position)
    {
        File openf = new File(path);
        openf.mkdir();
        ref.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {

                for (StorageReference prefix : listResult.getPrefixes()) {
                    // All the prefixes under listRef.
                    // You may call listAll() recursively on them.

                    RecDownload(prefix,path+"/"+prefix.getName(),position);

                }

                for (StorageReference item : listResult.getItems()) {

                    File file = new File(path+"/"+item.getName());

                    item.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProjectActivity.this,"Failed to download some files",Toast.LENGTH_SHORT).show();
                        }
                    });


                }

                if(flag.equals(path))
                {
                    downdialog.dismiss();
                    passActivity(position);
                }


            }
        });

    }

    @Override
    public void onItemClick(int position) {

        downdialog = new Dialog(this);
        downdialog.setCanceledOnTouchOutside(false);
        downdialog.setContentView(R.layout.download_dialog);

        File openf = new File(path+"/"+itemList.get(position));
        if(!openf.exists())
        {
            downdialog.show();

            flag = path+"/"+itemList.get(position);
            String downpath = uid+"/"+category+"/"+itemList.get(position);
            StorageReference listref = FirebaseStorage.getInstance().getReference().child(downpath);

            RecDownload(listref,path+"/"+itemList.get(position),position);


        }
        else
        {
            passActivity(position);
        }
    }

    void passActivity(int position)
    {

        if(category.equals("Images"))
        {
            Intent i  = new Intent(ProjectActivity.this,ImageActivity.class);
            String pathsend = path+"/"+itemList.get(position);
            i.putExtra("path",pathsend);
            i.putExtra("projectname",itemList.get(position));
            i.putExtra("fpath",uid+"/"+category + "/" + itemList.get(position));
            startActivity(i);
        }
        else if(category=="Sounds")
        {
            Intent i  = new Intent(ProjectActivity.this,ImageActivity.class);
            String pathsend = path+"/"+itemList.get(position);
            i.putExtra("projectname",itemList.get(position));
            i.putExtra("path",pathsend);startActivity(i);
            i.putExtra("fpath",uid+"/"+category + "/" + itemList.get(position));
            startActivity(i);
        }
    }

    @Override
    public void deleteClick(int position) {

        to_delete = position;
        foldername.setText(itemList.get(to_delete));
        delete_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        delete_dialog.show();

    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }



    public void loadfolders(){

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users/"+uid+"/Projects/"+ category);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds :  dataSnapshot.getChildren()){
                    itemList.add((String) ds.getValue());
                }

                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
