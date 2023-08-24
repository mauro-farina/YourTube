package it.units.sim.yourtube;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import it.units.sim.yourtube.auth.AuthenticationActivity;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        YouTubeDataViewModel viewModel = new ViewModelProvider(this, factory).get(YouTubeDataViewModel.class);
        viewModel.fetchUserSubscriptions();
        viewModel.getMissingYouTubeDataAuthorization().observe(this, isMissing -> {
            if (isMissing) {
                new MaterialAlertDialogBuilder(this)
                        .setMessage(getString(R.string.missing_youtube_authorization))
                        .setNeutralButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss())
                        .setOnDismissListener(dialog -> logoutViaAuthenticationActivity())
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
            logoutViaAuthenticationActivity();
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

    public void logoutViaAuthenticationActivity() {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.putExtra(AuthenticationActivity.INTENT_LOGOUT_FLAG, true);
        finish();
        startActivity(intent);
    }

}