package it.units.sim.yourtube;

import static it.units.sim.yourtube.AuthenticationActivity.INTENT_ALREADY_LOGGED_FLAG;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleAccountCredential credential = GoogleCredentialManager.getInstance().getCredential();
        if (credential.getSelectedAccountName() == null) {
            openAuthenticationActivity();
        }

        if (getIntent().getExtras() != null
                && !getIntent().getExtras().getBoolean(INTENT_ALREADY_LOGGED_FLAG)) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            CollectionReference userCategoriesBackupCollection = firestore.collection(uid);
            userCategoriesBackupCollection.get()
                    .addOnSuccessListener(result -> {
                        if (result.getDocuments().size() != 0)
                            System.out.println("+++++ found backup +++++");
                    });
        }

        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        networkChangeReceiver.getNetworkAvailability().observe(this, isNetworkAvailable -> {
            View noNetworkView = findViewById(R.id.no_network_message);
            if (isNetworkAvailable)
                noNetworkView.setVisibility(View.GONE);
            else {
                noNetworkView.setVisibility(View.VISIBLE);
            }
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }

        Toolbar toolbar = findViewById(R.id.top_app_bar);
        setSupportActionBar(toolbar);

        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        MainViewModel viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
        viewModel.fetchUserSubscriptions();
        viewModel.getMissingYouTubeDataAuthorization().observe(this, isMissing -> {
            if (isMissing) {
                new MaterialAlertDialogBuilder(this)
                        .setMessage(getString(R.string.missing_youtube_authorization))
                        .setNeutralButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss())
                        .setOnDismissListener(dialog -> logout())
                        .show();
            }
        });
        viewModel.getQuotaExceeded().observe(this, isQuotaExceeded -> {
            if (isQuotaExceeded) {
                findViewById(R.id.quota_exceeded_message).setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logoutButton) {
            logout();
            return true;
        } else if (id == R.id.settingsButton) {
            if (navController != null) {
                navController.navigate(R.id.action_to_settings);
            }
            return true;
        } else if (id == android.R.id.home) {
            if (navController != null)
                navController.popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.remove("accountName");
        editor.apply();
        openAuthenticationActivity();
    }

    private void openAuthenticationActivity() {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.putExtra(AuthenticationActivity.INTENT_LOGOUT_FLAG, true);
        finish();
        startActivity(intent);
    }

}