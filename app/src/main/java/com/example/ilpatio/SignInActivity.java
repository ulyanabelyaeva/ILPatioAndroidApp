package com.example.ilpatio;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import Models.User;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "llllllllllll";

    private FirebaseAuth mAuth;
    private EditText phoneEditText;
    private Button btnAuth;
    private String phoneNumber;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private EditText editText;
    private TextView text_error;
    private TextView codcountry;
    private boolean flagButton = true;//если true - ПРОДОЛЖИТЬ, если false - ВОЙТИ
    private String newVerificationId;
    private PhoneAuthProvider.ForceResendingToken newResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        init();
    }
    public void init(){
        textView1 = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        editText = findViewById(R.id.phoneEditText);
        codcountry = findViewById(R.id.text_codcountry);
    }
    @Override
    public void onStart() {
        super.onStart();
        phoneEditText = findViewById(R.id.phoneEditText);
        btnAuth = findViewById(R.id.btnAuth);
        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flagButton){
                    btnAuth.setEnabled(false);
                    phoneNumber = phoneEditText.getText().toString();
                    //отправка SMS с кодом
                    if (!phoneNumber.equals("")) {
                        phoneNumber = "+7" + phoneNumber;
                                PhoneAuthOptions options =
                                PhoneAuthOptions.newBuilder(mAuth)
                                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(SignInActivity.this)                 // Activity (for callback binding)
                                        .setCallbacks(mCallBacks)          // OnVerificationStateChangedCallbacks
                                        .build();
                        Log.i(TAG, "запрос отправлен");
                        PhoneAuthProvider.verifyPhoneNumber(options);
                    } else {
                        text_error = findViewById(R.id.text_error);
                        text_error.setText("Введите номер телефона");
                    }
                } else {
                    btnAuth.setEnabled(false);
                    String verificationCode = phoneEditText.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(newVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);

                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                //вызыватся в случаях:
                //1. В некоторых случаях номер телефона может быть мгновенно подтвержден
                // без необходимости отправки или ввода проверочного кода.
                //2. Автоматическое извлечение. На некоторых устройствах сервисы Google Play
                // могут автоматически обнаруживать входящие подтверждающие SMS-сообщения и
                // выполнять проверку без действий пользователя.
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                //вызывается, если пользователь неправильно ввел телефон
                //или телефон заблокирован
                text_error = findViewById(R.id.text_error);
                text_error.setText("Неверный формат ввода телефона");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                //код подтверждения отправлен на указанный номер
                //надо попросить пользователя ввести код
                //и создать новые ученые данные
                textView1.setText("");
                textView2.setText("Подтвердите телефон");
                textView3.setText("Введите код из SMS, отправленного на ваш номер");
                editText.setText("");
                editText.setHint("Код");
                codcountry.setVisibility(View.INVISIBLE);
                btnAuth.setText("Войти");
                btnAuth.setEnabled(true);
                flagButton = false;

                newVerificationId = verificationId;
                newResendToken = token;
            }

        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        //вызывается, когда код верификации верный
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Успешный вход в систему
                            FirebaseUser user2 = task.getResult().getUser();
                            // Обновление данных о пользователе
                        } else {
                            // Вход по каким то причинам выполнить не удалось
                            text_error = findViewById(R.id.text_error);
                            text_error.setText("Не удалось выполнить вход");
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // Введенный код оказался недействительным
                            }
                        }
                    }
                });
    }

    public void goBack(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
}