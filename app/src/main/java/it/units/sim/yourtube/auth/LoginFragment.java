package it.units.sim.yourtube.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import it.units.sim.yourtube.MainActivity;
import it.units.sim.yourtube.R;
import it.units.sim.yourtube.YourTubeApp;

public class LoginFragment extends Fragment {

    private GoogleSignInClient googleSignInClient;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ActivityResultLauncher<Intent> signInActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleSignInResult
        );

        view.findViewById(R.id.googleSignInButton).setOnClickListener(v -> {
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
            login(account);
        } catch (ApiException e) {
            Snackbar.make(
                    requireView(),
                    getString(R.string.authentication_error),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private void login(GoogleSignInAccount account) {
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        YourTubeApp app = (YourTubeApp) requireActivity().getApplication();
                        app.setGoogleCredentialAccount(account.getEmail());
                        openMainActivity();
                    } else {
                        Snackbar.make(
                                requireView(),
                                getString(R.string.authentication_error),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void openMainActivity() {
        requireActivity().finish();
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
    }

}