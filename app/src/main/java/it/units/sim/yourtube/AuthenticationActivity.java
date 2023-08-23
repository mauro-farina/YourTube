package it.units.sim.yourtube;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class AuthenticationActivity extends AppCompatActivity {

    private static final String[] YOUTUBE_API_SCOPES = { YouTubeScopes.YOUTUBE_READONLY };
    public static final String INTENT_LOGOUT_FLAG = "logout";
    public static final String INTENT_ALREADY_LOGGED_FLAG = "alreadyLogged";
    private FirebaseAuth mAuth;
    private GoogleAccountCredential credential;
    private GoogleCredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String lang = sharedPreferences.getString(SettingsManager.PREFERENCE_LANGUAGE, SettingsManager.PREFERENCE_LANGUAGE_DEFAULT);
        SettingsManager.setLanguage(getBaseContext(), lang);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestScopes(new Scope(YouTubeScopes.YOUTUBE_READONLY))
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (getIntent().getExtras() != null
                && getIntent().getExtras().getBoolean(INTENT_LOGOUT_FLAG)) {
            mAuth.signOut();
            mGoogleSignInClient.signOut().addOnFailureListener(runnable -> {

            });
        }

        credentialManager = GoogleCredentialManager.getInstance();
        credential = GoogleAccountCredential
                .usingOAuth2(getApplicationContext(), Arrays.asList(YOUTUBE_API_SCOPES))
                .setBackOff(new ExponentialBackOff());

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing GoogleSignInAccount (already logged user)
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();
        if (currentFirebaseUser != null) {
            System.out.println("firebase logged");
            System.out.println(currentFirebaseUser.getEmail());
        }
        if (account != null) {
            System.out.println(" already logged in ");
            String accountName = account.getEmail();
            credential.setSelectedAccountName(accountName);
            credentialManager.setCredential(credential);
            openMainActivity(true);
        }
    }

    private void openMainActivity(boolean alreadyLogged) {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(INTENT_ALREADY_LOGGED_FLAG, alreadyLogged);
        startActivity(intent);
    }

}