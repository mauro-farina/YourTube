package it.units.sim.yourtube;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

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
    private FirebaseAuth mAuth;
    private GoogleAccountCredential credential;
    private GoogleCredentialManager credentialManager;
    private GoogleSignInClient googleSignInClient;

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
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        if (getIntent().getExtras() != null
                && getIntent().getExtras().getBoolean(INTENT_LOGOUT_FLAG)) {
            forceLogout();
        }

        credentialManager = GoogleCredentialManager.getInstance();
        credential = GoogleAccountCredential
                .usingOAuth2(getApplicationContext(), Arrays.asList(YOUTUBE_API_SCOPES))
                .setBackOff(new ExponentialBackOff());

    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null && googleAccount != null) {
            String accountName = googleAccount.getEmail();
            credential.setSelectedAccountName(accountName);
            credentialManager.setCredential(credential);
            openMainActivity();
        } else if (firebaseUser != null || googleAccount != null) {
            forceLogout();
        }
    }

    private void forceLogout() {
        googleSignInClient.signOut()
                .addOnSuccessListener(runnable -> {
                    mAuth.signOut();
                    Toast.makeText(
                            getBaseContext(),
                            "Log out successfully",
                            Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(runnable ->
                    Toast.makeText(
                            getBaseContext(),
                            "Logged out failed",
                            Toast.LENGTH_LONG).show()
                );
    }

    private void openMainActivity() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}