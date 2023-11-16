package com.example.googleauth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        Button logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(view -> logOut());

        TextView nameTextView = findViewById(R.id.text_name);
        TextView emailTextView = findViewById(R.id.text_email);
        ImageView profileImageView = findViewById(R.id.image_profile);

        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String profileImageUrl = getIntent().getStringExtra("profileImageUrl");
        nameTextView.setText("Name: " + name);
        emailTextView.setText("Email: " + email);
        Picasso.get().load(profileImageUrl).into(profileImageView);
    }
    private void logOut() {
        mAuth.signOut();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {
                if (status.isSuccess()) {
                    revokeAccessAndRedirect();
                } else {
                    Toast.makeText(this, "Failed to sign out from Google", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            revokeAccessAndRedirect();
        }
    }
    private void revokeAccessAndRedirect() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, gso);
        signInClient.revokeAccess().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
            } else {
                Toast.makeText(this, "Failed to revoke access", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("GoogleSignIn", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
