package com.example.xchange;

import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import android.content.Intent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SignUp_Fragment extends Fragment implements View.OnClickListener {
    private static View view;
    private static EditText emailId,
            password;
    private static TextView login;
    private static Button signUpButton;
    private static CheckBox terms_conditions;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    public SignUp_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_layout, container, false);
        initViews();
        setListeners();

        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        // grd.setBackgroundColor(Color.CYAN);
        return view;



    }

    // Initialize all views
    private void initViews() {
        emailId = (EditText)view.findViewById(R.id.userEmailId);
        password = (EditText)view.findViewById(R.id.password);
        signUpButton = (Button) view.findViewById(R.id.signUpBtn);
        login = (TextView) view.findViewById(R.id.already_user);
        terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);

        // Setting text selector over textviews
        XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            login.setTextColor(csl);
            terms_conditions.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpBtn:

                // Call checkValidation method
                checkValidation();
                break;

            case R.id.already_user:

                // Replace login fragment
                new MainActivity().replaceLoginFragment();
                break;
        }

    }

    // Check Validation Method

       private void checkValidation(){
            auth = FirebaseAuth.getInstance();

            String getEmail = emailId.getText().toString().trim();
            String getPassword = password.getText().toString();

            //validation
            if(!getEmail.isEmpty()){

                if(!getPassword.isEmpty())
                    //register
                    {
                        auth.createUserWithEmailAndPassword(
                                getEmail, getPassword
                        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    Toast.makeText(getActivity(), "Account created",
                                            Toast.LENGTH_SHORT).show();

                                    //redirect user for principe screen app

                                }else{

                                    String errorException = "";

                                    try{
                                        throw task.getException();
                                    }catch (FirebaseAuthWeakPasswordException e){
                                        errorException = "password is too weak!";
                                    }catch (FirebaseAuthInvalidCredentialsException e){
                                        errorException = "enter valid email";
                                    }catch (FirebaseAuthUserCollisionException e){
                                        errorException = "This account has already been registered.";
                                    } catch (Exception e) {
                                        errorException = "when registering user: "  + e.getMessage();
                                        e.printStackTrace();
                                    }

                                    Toast.makeText(getActivity(),errorException ,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }


                else{

                    Toast.makeText(getActivity(), "Fill in the password!",
                            Toast.LENGTH_SHORT).show();
                }

            }else{

                Toast.makeText(getActivity(), "Fill in the E-mail",
                        Toast.LENGTH_SHORT).show();
            }
        }



            @Override

            public void onResume() {
                super.onResume();
                progressBar.setVisibility(View.GONE);
            }


}

