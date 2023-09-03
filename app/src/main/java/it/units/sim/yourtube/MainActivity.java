package it.units.sim.yourtube;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import it.units.sim.yourtube.auth.AuthenticationActivity;

public class MainActivity extends AppCompatActivity implements MenuProvider {

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
        addMenuProvider(this);

        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        YouTubeDataViewModel viewModel = new ViewModelProvider(this, factory).get(YouTubeDataViewModel.class);
        viewModel.fetchUserSubscriptions();
        viewModel.getMissingYouTubeDataAuthorization().observe(this, isMissing -> {
            if (isMissing) {
                NoYouTubeAuthorizationDialog
                        .newInstance()
                        .show(getSupportFragmentManager(), NoYouTubeAuthorizationDialog.TAG);
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
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.top_bar_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
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
        return false;
    }

    public void logoutViaAuthenticationActivity() {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.putExtra(AuthenticationActivity.INTENT_LOGOUT_FLAG, true);
        finish();
        startActivity(intent);
    }

}