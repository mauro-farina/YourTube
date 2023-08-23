package it.units.sim.yourtube;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class LoginFragment extends Fragment {

    public static final String INTENT_ALREADY_LOGGED_FLAG = "alreadyLogged";
    private GoogleSignInClient googleSignInClient;
    private GoogleCredentialManager credentialManager;
    private GoogleAccountCredential credential;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] YOUTUBE_API_SCOPES = { YouTubeScopes.YOUTUBE_READONLY };
    private SharedPreferences defaultSharedPreferences;
    private FirebaseAuth mAuth;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestScopes(new Scope(YouTubeScopes.YOUTUBE_READONLY))
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        mAuth = FirebaseAuth.getInstance();

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        credentialManager = GoogleCredentialManager.getInstance();
        credential = GoogleAccountCredential
                .usingOAuth2(requireContext(), Arrays.asList(YOUTUBE_API_SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ActivityResultLauncher<Intent> signInActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleSignInResult
        );

        view.findViewById(R.id.newGoogleSignInButton).setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            signInActivityLauncher.launch(signInIntent);
        });

        return view;
    }

    private void handleSignInResult(ActivityResult result) {
        Intent data = result.getData();
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            String idToken = account.getIdToken();
            localLogin(account.getEmail());
            remoteLoginAndOpenMainActivity(idToken);
        } catch (ApiException e) {
            // GoogleSignInStatusCodes class for more info.
            Log.w(TAG, "signInResult: failed code = " + e.getStatusCode());
            switch (e.getStatusCode()) {
                case CommonStatusCodes.CANCELED:
                    Log.d(TAG, "Sign In canceled");
                    break;
                case CommonStatusCodes.NETWORK_ERROR:
                    Log.d(TAG, "Network error.");
                    break;
                default:
                    Log.d(TAG, "Couldn't get credential from result." + e.getLocalizedMessage());
                    break;
            }
        }
    }

    private void localLogin(String accountName) {
        credential.setSelectedAccountName(accountName);
        credentialManager.setCredential(credential);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.apply();
    }

    private void remoteLoginAndOpenMainActivity(String idToken) {
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        openMainActivity(false);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signIn : failure", task.getException());
                        Toast.makeText(
                                requireContext(),
                                "Authentication process encountered an error",
                                Toast.LENGTH_SHORT).show();
                        // TODO: Snackbar
                    }
                });
    }

    private void openMainActivity(boolean alreadyLogged) {
        requireActivity().finish();
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.putExtra(INTENT_ALREADY_LOGGED_FLAG, alreadyLogged);
        startActivity(intent);
    }

}