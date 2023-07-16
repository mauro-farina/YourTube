package it.units.sim.yourtube;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    private GoogleAccountCredential googleAccountCredential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleAccountCredential = GoogleCredentialManager.getInstance().getCredential();
        if (googleAccountCredential.getSelectedAccountName() == null) {
            finish();
            Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivity(intent);
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }

    }

}