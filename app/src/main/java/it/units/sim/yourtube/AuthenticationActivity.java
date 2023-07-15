package it.units.sim.yourtube;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;

import java.util.Arrays;

public class AuthenticationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] YOUTUBE_API_SCOPES = { YouTubeScopes.YOUTUBE_READONLY };

    private GoogleAccountCredential googleAccountCredential;
    private SharedPreferences defaultSharedPreferences;
    private ActivityResultLaunchers activityResultLaunchers;
    private GoogleCredentialManager googleCredentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        this.defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        googleCredentialManager = GoogleCredentialManager.getInstance();

        // Initialize credentials and service object.
        googleAccountCredential = GoogleAccountCredential
                .usingOAuth2(getApplicationContext(), Arrays.asList(YOUTUBE_API_SCOPES))
                .setBackOff(new ExponentialBackOff());

        if (isUserLoggedIn()) {
            googleCredentialManager.setCredential(googleAccountCredential);
            openMainActivity();
        }

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        // register for activities results
        activityResultLaunchers = new ActivityResultLaunchers();
    }

    /**
     * Opens MainActivity, assumes user's authentication is verified.
     */
    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("googleAccount", googleAccountCredential.getSelectedAccountName());
        startActivity(intent);
    }

    /**
     * Checks whether a 'userName' preference has previously been set in the preferences
     * @return true if preferences contain 'userName', false otherwise
     */
    private boolean isUserLoggedIn() {
        String accountName = defaultSharedPreferences.getString(PREF_ACCOUNT_NAME, null);
        return accountName != null;
    }

    /**
     * Checks whether the permissionName is granted to the app or not.
     * @return true if Manifest.permission.PERMISSION_NAME is granted, false otherwise
     */
    private boolean isGetAccountsPermissionGranted() {
        int accountsPermissionGranted = ContextCompat
                .checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        return accountsPermissionGranted ==  PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Launches a request for GET_ACCOUNTS at runtime
     */
    private void requestGetAccountsPermission() {
        activityResultLaunchers.requestPermission.launch(Manifest.permission.GET_ACCOUNTS);
    }

    /**
     * Opens a dialog to pick the Google account to use for the login.
     * Requires GET_ACCOUNTS permission.
     */
    private void chooseGoogleAccount() {
        Intent chooseGoogleAccountIntent = googleAccountCredential.newChooseAccountIntent();
        activityResultLaunchers.chooseGoogleAccount(chooseGoogleAccountIntent);
    }

    /**
     * Updates activity's preferences with the new value for PREF_ACCOUNT_NAME.
     * @param accountName the account name to set in the preferences
     */
    private void updateAccountName(String accountName) {
        googleAccountCredential.setSelectedAccountName(accountName);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.apply();
    }

    private void login(String accountName) {
        googleAccountCredential.setSelectedAccountName(accountName);
        googleCredentialManager.setCredential(googleAccountCredential);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.apply();
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google Play Services
     * installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing or out of date.
     * @param connectionStatusCode code describing the presence (or lack of) Google Play
     *     Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        // works, but requires a manual dialog
        Intent intent = apiAvailability.getErrorResolutionIntent(this, connectionStatusCode, "str");
        activityResultLaunchers.googlePlayServicesAvailability.launch(intent);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() != R.id.loginButton) {
             return;
        }
        if (!isDeviceOnline()) {
            Toast.makeText(this, getString(R.string.no_network_connection), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (!isGetAccountsPermissionGranted()) {
            requestGetAccountsPermission();
        } else {
            chooseGoogleAccount();
        }
    }

    private class ActivityResultLaunchers {
        private final ActivityResultLauncher<Intent> chooseAccount;
        private final ActivityResultLauncher<String> requestPermission;
        private final ActivityResultLauncher<Intent> googlePlayServicesAvailability;

        private ActivityResultLaunchers() {
            chooseAccount = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK
                            && result.getData() != null
                            && result.getData().getExtras() != null) {

                        String chosenAccount = result.getData()
                                .getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                        if (chosenAccount != null) {
                            updateAccountName(chosenAccount);
                            googleCredentialManager.setCredential(googleAccountCredential);
                            openMainActivity();
                        }
                    }
                }
            );

            requestPermission =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) {
                                // FEEDBACK
                                System.out.println("GET_ACCOUNTS granted");
                            } else {
                                // FEEDBACK
                                System.out.println
                                        (getString(R.string.app_requires_google_account_access));
                            }
                        }
                );

            googlePlayServicesAvailability =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (isGooglePlayServicesAvailable()) {
                                System.out.println("Google Play Services now available");
                                // FEEDBACK
                            } else {
                                // The user did not resolve the issue with Google Play services.
                                // Handle the error or take appropriate action.
                                System.out.println
                                        ("Can't use this app without Google Play Services...");
                            }
                        }
                );
        }

        private void chooseGoogleAccount(Intent intent) {
            chooseAccount.launch(intent);
        }
    }
}