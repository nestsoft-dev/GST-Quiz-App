package com.ikenna.gstquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateActivity extends AppCompatActivity {
    EditText name, email,password,department;
    Button signup,alreadyhaveaccount;
    FirebaseAuth auth;
    FirebaseFirestore database;
    ProgressDialog dialog;
    long points = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        name = findViewById(R.id.nameBox);
        email = findViewById(R.id.emailBox);
        password = findViewById(R.id.passwordBox);
        department = findViewById(R.id.department);
        signup = findViewById(R.id.createNewBtn);
        alreadyhaveaccount = findViewById(R.id.loginBtn);
        dialog = new ProgressDialog(this);
        dialog.setMessage("We're creating new account...");


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameuser = name.getText().toString();
                String emailuser = email.getText().toString();
                String passuser = password.getText().toString();
                String deptuser = department.getText().toString();

                if (nameuser.isEmpty()){
                    Toast.makeText(CreateActivity.this, "Please input name", Toast.LENGTH_SHORT).show();
                }else if (emailuser.isEmpty()){
                    Toast.makeText(CreateActivity.this, "Please input email", Toast.LENGTH_SHORT).show();
                }
                else if (passuser.isEmpty()){
                    Toast.makeText(CreateActivity.this, "Please input password", Toast.LENGTH_SHORT).show();
                }else if (deptuser.isEmpty()){
                    Toast.makeText(CreateActivity.this, "Please input department", Toast.LENGTH_SHORT).show();
                }else{
                    UserModel userModel = new UserModel(nameuser,emailuser,passuser,deptuser,points);

                    dialog.show();
                    auth.createUserWithEmailAndPassword(emailuser, passuser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                String uid = task.getResult().getUser().getUid();

                                database
                                        .collection("users")
                                        .document(uid)
                                        .set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            dialog.dismiss();
                                            startActivity(new Intent(CreateActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(CreateActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                dialog.dismiss();
                                Toast.makeText(CreateActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }
        });
        alreadyhaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateActivity.this,LoginActivity.class));
            }
        });
    }
}