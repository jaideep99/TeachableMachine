package com.example.teachablemachine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.Executor;


public class signup extends Fragment {

    private EditText email,password,username;
    private int visibility = 0;
    private ImageView eye;
    private LinearLayout signup;
    private FirebaseAuth mFirebaseAuth;
    private ViewPager viewPager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_signup, container, false);

        username = (EditText) rootView.findViewById(R.id.name);
        email = (EditText) rootView.findViewById(R.id.email);
        password = (EditText) rootView.findViewById(R.id.password);
        eye = (ImageView) rootView.findViewById(R.id.visibility);
        signup = (LinearLayout)rootView.findViewById(R.id.signup_btn);
        viewPager = (ViewPager) getActivity().findViewById(R.id.login_pager);

        mFirebaseAuth = FirebaseAuth.getInstance();

        Typeface font = Typeface.createFromAsset(getContext().getAssets(),"poppins.ttf");
        email.setTypeface(font);
        password.setTypeface(font);
        username.setTypeface(font);

        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(visibility == 0){
                    visibility = 1;
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    eye.setImageResource(R.drawable.eye);
                }
                else{
                    visibility = 0;
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eye.setImageResource(R.drawable.invisible);
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = username.getText().toString();
                final String emailid = email.getText().toString();
                String pass = password.getText().toString();

                if(emailid.isEmpty()){
//                    email.setError("Enter Email Id !!");
                    email.requestFocus();
                }
                else if(pass.isEmpty()){
//                    password.setError("Enter Password !!");
                    password.requestFocus();
                }
                else if(emailid.isEmpty() && pass.isEmpty()){
                    Toast.makeText(getActivity().getApplicationContext(),"Required Fields Are Empty!",Toast.LENGTH_SHORT).show();
                }
                else if(!(emailid.isEmpty() && !(pass.isEmpty()))){
                    mFirebaseAuth.createUserWithEmailAndPassword(emailid,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(!task.isSuccessful()){
                                email.requestFocus();
                                email.setError("Email Id already Exists!");
                            }
                            else{

                                user users = new user(name,emailid);
                                FirebaseDatabase.getInstance()
                                        .getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Log.d("database","inside oncomplete");
                                        if(task.isSuccessful()){
                                            Log.d("database","inside oncomplete task success");
                                            Toast.makeText(getActivity().getApplicationContext(),"Registration Successful!",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Log.d("database","inside oncomplete not success");
                                            Toast.makeText(getActivity().getApplicationContext(),"UnSuccessful!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                viewPager.setCurrentItem(0,true);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(),"Error Occurred!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }


}
