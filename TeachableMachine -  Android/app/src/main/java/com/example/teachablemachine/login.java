package com.example.teachablemachine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.ContactsContract;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.Context.MODE_PRIVATE;


public class login extends Fragment {

    private EditText email,password;
    private int visibility = 0;
    private ImageView eye;
    private CheckBox remember;
    private LinearLayout login;
    private TextView forgot;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    Context context;

    login(Context cont)
    {
        this.context = cont;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        email = (EditText) rootView.findViewById(R.id.email);
        password = (EditText) rootView.findViewById(R.id.password);
        forgot = (TextView) rootView.findViewById(R.id.forgotpassword);
        eye = (ImageView) rootView.findViewById(R.id.visibility);
        remember = (CheckBox) rootView.findViewById(R.id.remember);
        login = (LinearLayout)rootView.findViewById(R.id.login_btn);


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

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = mFirebaseAuth.getCurrentUser();

                if(mUser != null)
                {
//                    Toast.makeText(getActivity().getApplicationContext(),"Login Successful!",Toast.LENGTH_SHORT).show();

                    Activity a = getActivity();
                    if(getActivity()!=null)
                    {
                        Intent i = new Intent(getActivity(),MainActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    }
                    else
                    {
//                        Toast.makeText(context,"Activity is Null!!",Toast.LENGTH_SHORT).show();
                    }

                }
                else{
//                    Toast.makeText(getActivity().getApplicationContext(),"Please Login!!",Toast.LENGTH_SHORT).show();
                }

            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailid = email.getText().toString();
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
                    Toast.makeText(context,"Required Fields Are Empty!",Toast.LENGTH_SHORT).show();
                }
                else if(!(emailid.isEmpty() && !(pass.isEmpty()))){

                    mFirebaseAuth.signInWithEmailAndPassword(emailid,pass).addOnCompleteListener((Activity) context,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            Log.d("login","inside Successfull");
                            if(!task.isSuccessful()){

                                Toast.makeText(context,"Login Unsuccessful!",Toast.LENGTH_SHORT).show();

                            }
                            else{

//                                Toast.makeText(getActivity().getApplicationContext(),"Login Successful!",Toast.LENGTH_SHORT).show();
                                setLogin(true);
                                Intent i = new Intent(context,MainActivity.class);
                                startActivity(i);
                                getActivity().finish();

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,"Failed Auth",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else {
                    Toast.makeText(context,"Error Occurred!",Toast.LENGTH_SHORT).show();
                }
            }
        });



        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(loggedin()){
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        }

    }

    void setLogin(boolean status){
        SharedPreferences ref = getActivity().getSharedPreferences("TM",MODE_PRIVATE);
        SharedPreferences.Editor editor = ref.edit();
        editor.putBoolean("loggedin",status);
        editor.commit();
    }

    private boolean loggedin()
    {
        SharedPreferences prefs = getActivity().getSharedPreferences("TM",MODE_PRIVATE);
        return prefs.getBoolean("loggedin",false);
    }
}
