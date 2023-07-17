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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;

import java.util.Arrays;

import it.units.sim.yourtube.api.ChannelInfoRequest;
import it.units.sim.yourtube.api.MissingAuthorizationCallback;
import it.units.sim.yourtube.api.RequestCallback;
import it.units.sim.yourtube.api.RequestThread;
import it.units.sim.yourtube.api.YouTubeApiRequest;

public class AuthenticationActivity extends AppCompatActivity
        implements View.OnClickListener, MissingAuthorizationCallback {

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] YOUTUBE_API_SCOPES = { YouTubeScopes.YOUTUBE_READONLY };

    private GoogleAccountCredential credential;
    private SharedPreferences defaultSharedPreferences;
    private ActivityResultLaunchers activityResultLaunchers;
    private GoogleCredentialManager credentialManager;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        credentialManager = GoogleCredentialManager.getInstance();

        // Initialize credentials and service object.
        credential = GoogleAccountCredential
                .usingOAuth2(getApplicationContext(), Arrays.asList(YOUTUBE_API_SCOPES))
                .setBackOff(new ExponentialBackOff());

        if (isUserLoggedIn()) {
            String accountName = getAccountName();
            credential.setSelectedAccountName(accountName);
            credentialManager.setCredential(credential);
            openMainActivity();
        }

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        // register for activities results
        activityResultLaunchers = new ActivityResultLaunchers();
    }

    /**
     * Opens MainActivity. Assumes user's authentication is verified.
     */
    private void openMainActivity() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Checks whether a 'accountName' preference has previously been set in the preferences
     * @return true if preferences contain 'accountName', false otherwise
     */
    private boolean isUserLoggedIn() {
        return getAccountName() != null;
    }

    private String getAccountName() {
        return defaultSharedPreferences.getString(PREF_ACCOUNT_NAME, null);
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
        Intent chooseGoogleAccountIntent = credential.newChooseAccountIntent();
        activityResultLaunchers.chooseGoogleAccount(chooseGoogleAccountIntent);
    }

    private void getAuthorizationIfMissing() {
        YouTubeApiRequest<String> request =
                new ChannelInfoRequest(credential);
        RequestCallback<String> callback = subscriptionList -> {
            // empty
        };
        RequestThread<String> rThread = new RequestThread<>(request, callback, this);
        rThread.start();
    }

    @Override
    public void onMissingAuthorization(Intent intent) {
        activityResultLaunchers.getAuthorizationActivity.launch(intent);
    }

    private void login(String accountName) {
        credential.setSelectedAccountName(accountName);
        credentialManager.setCredential(credential);
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
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.app_requires_google_play_services))
                .setPositiveButton(("OK"), (dialog, which) -> {
                    Intent intent = apiAvailability.getErrorResolutionIntent(this, connectionStatusCode, "str");
                    activityResultLaunchers.googlePlayServicesAvailability.launch(intent);
                })
                .setNegativeButton("NO", (dialog, which) -> Toast
                        .makeText(this, getString(R.string.app_requires_google_play_services), Toast.LENGTH_SHORT)
                        .show())
                .show();
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
        private final ActivityResultLauncher<Intent> getAuthorizationActivity;

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
                            login(chosenAccount);
                            getAuthorizationIfMissing();
                        }
                    }
                }
            );

            requestPermission =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) {
                                onClick(loginButton);
                            } else {
                                // FEEDBACK
                                new MaterialAlertDialogBuilder(AuthenticationActivity.this)
                                        .setTitle(getString(R.string.app_name))
                                        .setMessage(getString(R.string.app_requires_google_account_access))
//                                        .setPositiveButton("OK", (dialog, which) -> {
//                                            requestGetAccountsPermission();
//                                        })
                                        .setNeutralButton("CLOSE", (dialog, which) -> {
                                            // no actions required
                                        })
                                        .show();
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

            getAuthorizationActivity = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            openMainActivity();
                        } else {
                            System.out.println("oh man :c");
                            // TODO: Dialog
                        }
                    }
            );
        }

        private void chooseGoogleAccount(Intent intent) {
            chooseAccount.launch(intent);
        }
        private void requestGetAccountsPermission() {
            requestPermission.launch("GET_ACCOUNTS");
        }
    }
}