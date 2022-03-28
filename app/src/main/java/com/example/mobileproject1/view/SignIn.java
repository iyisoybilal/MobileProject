package com.example.mobileproject1.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mobileproject1.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth=FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            Intent intent = new Intent(SignIn.this,FeedActivity.class);
            startActivity(intent);
            finish();
        }
    }
    public  void SignInClicked(View view){
        {
            String email= binding.EmailText.getText().toString();
            String password = binding.PasswordText.getText().toString();
            if(email.equals("")||password.equals("")){
                Toast.makeText(this,"Please enter email and password",Toast.LENGTH_LONG).show();
            }
            else{
                auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Intent intent = new Intent(SignIn.this,FeedActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignIn.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }


        }
    }
    public  void SignUpActivity(View view){
        Intent intent = new Intent(SignIn.this,SignUp.class);
        startActivity(intent);
    }
}