package it.units.sim.yourtube.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.services.youtube.YouTubeScopes;

import it.units.sim.yourtube.utils.EmptyMenuProvider;
import it.units.sim.yourtube.MainActivity;
import it.units.sim.yourtube.R;
import it.units.sim.yourtube.data.CategoriesViewModel;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ActionBar toolbar;
    private CategoriesViewModel viewModel;
    private MenuProvider menuProvider;
    private FragmentManager fragmentManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuProvider = new EmptyMenuProvider();
        PreferenceManager.getDefaultSharedPreferences(requireContext()).registerOnSharedPreferenceChangeListener(this);
        fragmentManager = getChildFragmentManager();
        fragmentManager.setFragmentResultListener(
                ConfirmPreferenceDialog.REQUEST_KEY,
                this,
                (requestKey, result) -> {
                    if (!requestKey.equals(ConfirmPreferenceDialog.REQUEST_KEY))
                        return;
                    if (result.keySet().size() == 0)
                        return;
                    int preference = result.getInt(ConfirmPreferenceDialog.RESULT_KEY);
                    if (preference == ConfirmPreferenceDialog.PREFERENCE_DELETE_ACCOUNT) {
                        deleteAccount();
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(requireContext()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        toggleBottomNav();
        toolbar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(getString(R.string.settings));
            requireActivity().addMenuProvider(menuProvider);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        toggleBottomNav();
        if (toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(false);
            toolbar.setTitle(R.string.app_name);
            requireActivity().removeMenuProvider(menuProvider);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        viewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);

        Preference logoutPreference = findPreference("logout_preference");
        Preference deleteAccountPreference = findPreference("delete_account");
        setupLogoutPreference(logoutPreference);
        setupDeleteAccountPreference(deleteAccountPreference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingsManager.PREFERENCE_THEME)) {
            String newTheme = sharedPreferences.getString(key, SettingsManager.PREFERENCE_THEME_DEFAULT);
            SettingsManager.setTheme(newTheme);
        } else if(key.equals(SettingsManager.PREFERENCE_LANGUAGE)) {
            String newLanguage = sharedPreferences.getString(key, SettingsManager.PREFERENCE_LANGUAGE_DEFAULT);
            SettingsManager.setLanguage(getResources(), newLanguage);
            requireActivity().recreate();
        }
    }

    private void setupLogoutPreference(Preference logoutPreference) {
        if (logoutPreference == null){
            return;
        }
        logoutPreference.setOnPreferenceClickListener(preference -> {
            logout();
            return true;
        });
    }

    private void logout() {
        ((MainActivity) requireActivity()).logoutViaAuthenticationActivity();
    }

    private void setupDeleteAccountPreference(Preference deleteAccountPreference) {
        if (deleteAccountPreference == null) {
            return;
        }
        deleteAccountPreference.setOnPreferenceClickListener(preference -> {
            ConfirmPreferenceDialog
                    .newInstance(ConfirmPreferenceDialog.PREFERENCE_DELETE_ACCOUNT)
                    .show(fragmentManager, ConfirmPreferenceDialog.TAG);
            return true;
        });
    }

    private void deleteAccount() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestScopes(new Scope(YouTubeScopes.YOUTUBE_READONLY))
                .build();
        GoogleSignInClient signInClient = GoogleSignIn.getClient(requireActivity(), gso);
        signInClient.revokeAccess().addOnSuccessListener(runnable1 -> {
            viewModel.deleteAll();
            logout();
        }).addOnFailureListener(runnable1 ->
                Snackbar.make(requireView(), getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG).show()
        );
    }

    private void toggleBottomNav() {
        View bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav.getVisibility() == View.VISIBLE) {
            bottomNav.setVisibility(View.GONE);
        } else {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }

}