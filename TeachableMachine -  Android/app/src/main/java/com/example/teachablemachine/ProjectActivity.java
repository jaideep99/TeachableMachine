package com.example.teachablemachine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        category = (String) getIntent().getStringExtra("Category");
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                    File del = new File(path + "/" + itemList.get(to_delete));
                    deleteRecursive(del);
                    itemList.remove(to_delete);
                    to_delete = -1;
                    adapter.notifyDataSetChanged();
                    delete_dialog.dismiss();
                }
            }
        });

        loadfolders();

        layoutManager = new LinearLayoutManager(this);
        adapter = new ProjectAdapter(itemList);
        projectlist.setLayoutManager(layoutManager);
        projectlist.setAdapter(adapter);
        adapter.setOnItemClickListener(ProjectActivity.this);

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
                }
                else {
                    Toast.makeText(ProjectActivity.this,"Project Name Already Taken!",Toast.LENGTH_SHORT).show();
                }
                create_dialog.dismiss();
                checkfolders();
            }
        });


    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(ProjectActivity.this,itemList.get(position)+ category,Toast.LENGTH_SHORT).show();
        if(category.equals("Images"))
        {
            Intent i  = new Intent(ProjectActivity.this,ImageActivity.class);
            String pathsend = path+"/"+itemList.get(position);
            i.putExtra("path",pathsend);
            i.putExtra("projectname",itemList.get(position));
            startActivity(i);
        }
        else if(category=="Sounds")
        {
            Intent i  = new Intent(ProjectActivity.this,ImageActivity.class);
            String pathsend = path+"/"+itemList.get(position);
            i.putExtra("projectname",itemList.get(position));
            i.putExtra("path",pathsend);startActivity(i);
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


        adapter.notifyDataSetChanged();
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

    }
}
