package it.units.sim.yourtube;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.youtube.YouTubeScopes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private GoogleAccountCredential googleAccountCredential;
    private SharedPreferences defaultSharedPreferences;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] YOUTUBE_API_SCOPES = { YouTubeScopes.YOUTUBE_READONLY };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Initialize credentials and service object.
        googleAccountCredential = GoogleAccountCredential
                .usingOAuth2(getApplicationContext(), Arrays.asList(YOUTUBE_API_SCOPES))
                .setBackOff(new ExponentialBackOff());
        googleAccountCredential.setSelectedAccountName(defaultSharedPreferences.getString(PREF_ACCOUNT_NAME, null));
        // if null go to auth activity

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }

    }

}