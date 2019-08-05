package com.mcdenny.alcapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class SignInActivity extends AppCompatActivity {
    private LinearLayout btnGoogle, btnEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnEmail = (LinearLayout) findViewById(R.id.sign_in_with_email);
        btnGoogle = (LinearLayout) findViewById(R.id.sign_in_with_google);

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddTravelDeal.class));
            }
        });
    }
}
