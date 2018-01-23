package com.example.sushant.olemissnepal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static java.lang.System.in;

public class NepaliActivity extends AppCompatActivity {
    Button mButton;
    TextView mTextView;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    GoogleSignInClient mGoogleSignInClient;
    public final String OLEMISS_WEBPAGE_URL ="https://www.olemiss.edu";
    public final String OLEMISS_ADDRESS_STRING="UNIVERSITY, MS";
    private final String CREATOR_EMAIL ="sushant2fotball@gmail.com";
    String[] addresses= {CREATOR_EMAIL};

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nepali);
        mButton = findViewById(R.id.button2);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                startActivity(new Intent(NepaliActivity.this,MainActivity.class));
                }

            }
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        String personName,personGivenName,personFamilyName,personEmail,personId;
        Uri personPhotoUri;
        mAuth = FirebaseAuth.getInstance();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        personName ="Ram";
        personPhotoUri =null;
        if (acct != null) {
            personName = acct.getDisplayName();
           personGivenName = acct.getGivenName();
           personFamilyName = acct.getFamilyName();
           personEmail = acct.getEmail();
           personId = acct.getId();
           personPhotoUri = acct.getPhotoUrl();
        }

        TextView textView = (TextView)findViewById(R.id.textView3);
        textView.setText(personName);
        ImageView imageView = findViewById(R.id.imageView2);

        Picasso.with(this).load(personPhotoUri).into(imageView);


    }
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       // updateUI(null);

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nepali_acitivity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.next_bus_menu:
                startActivity(new Intent(NepaliActivity.this,NextBusActivity.class));
                break;
            case R.id.log_out_menu:
                signOut();
                break;
            case R.id.olemiss_website_menu:
                openWebpage(OLEMISS_WEBPAGE_URL);
                break;
            case R.id.olemiss_map_menu:
                openOlemissMap();
                break;
            case R.id.share_content_menu:
                shareText("There is a problem with the app");
                break;
            case R.id.send_email:
                composeEmail(addresses,"Error within the app");
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void openWebpage(String url){
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent (Intent.ACTION_VIEW,webpage);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }
    }

    public void openOlemissMap(){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("geo")
                .path("0,0")
                .query(OLEMISS_ADDRESS_STRING);
        Uri addressUri = builder.build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(addressUri);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }
    }

    public void shareText(String textToShare){
        String mediaType ="text/plain";
        String Title="Error Log";


        ShareCompat.IntentBuilder.from(this)
                .setChooserTitle(Title)
                .setType(mediaType)
                .setText(textToShare)
                .startChooser();
    }
    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        String emailMessage = "There is a problem with the app. Specifically, ";
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT,emailMessage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }



}
