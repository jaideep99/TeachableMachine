package com.example.teachablemachine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.File;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggler;
    String name = "user";
    NavigationView navigationView;

    private LinearLayout images,sounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Toolbar tools = findViewById(R.id.toolbar);
        setSupportActionBar(tools);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        for (int i = 0; i < tools.getChildCount(); i++) {
            if(tools.getChildAt(i) instanceof ImageButton){
                tools.getChildAt(i).setScaleX(1.5f);
                tools.getChildAt(i).setScaleY(1.5f);
            }
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer);

        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        toggler = new ActionBarDrawerToggle(this,drawer,R.string.open,R.string.close);
        drawer.addDrawerListener(toggler);
        toggler.syncState();

        images = (LinearLayout)findViewById(R.id.images);
        sounds = (LinearLayout)findViewById(R.id.sounds);

        images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,ProjectActivity.class);
                i.putExtra("Category","Images");
                startActivity(i);
            }
        });

        sounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,ProjectActivity.class);
                i.putExtra("Category","Sounds");
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.about)
        {
            drawer.closeDrawers();
        }
        else if(id == R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            setLogin(false);
            Intent i = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(toggler.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void setLogin(boolean status){
        SharedPreferences ref = getApplicationContext().getSharedPreferences("PLAY_API",MODE_PRIVATE);
        SharedPreferences.Editor editor = ref.edit();
        editor.putBoolean("loggedin",status);
        editor.commit();
    }

    void LoadUserName(){

        DatabaseReference user = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username");
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                View header = navigationView.getHeaderView(0);
                TextView usern = (TextView) header.findViewById(R.id.name);
                usern.setText(dataSnapshot.getValue(String.class)+" !");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadUserName();
        createFoldersuser();
        createFolders();
    }

    public void createFolders()
    {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/TeachableMachine/"+uid+"/Images");
        if(!root.exists()){
            root.mkdir();
        }

        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/TeachableMachine/"+uid+"/Sounds");
        if(!root.exists()){
            root.mkdir();
        }
    }

    public void createFoldersuser()
    {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/TeachableMachine/"+uid);
        if(!root.exists()){
            root.mkdir();
        }
    }
}
