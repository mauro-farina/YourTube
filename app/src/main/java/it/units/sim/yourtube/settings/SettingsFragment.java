package it.units.sim.yourtube.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import it.units.sim.yourtube.MainActivity;
import it.units.sim.yourtube.R;
import it.units.sim.yourtube.data.CategoriesViewModel;
import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.CloudBackupObject;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ActionBar toolbar;
    private CategoriesViewModel viewModel;
    private List<Category> categories;
    private static final String BACKUP_DOCUMENT_PATH = "categoriesBackup";
    private DocumentReference userBackupDocument;
    private Preference importBackupPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(requireContext()).registerOnSharedPreferenceChangeListener(this);
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
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        toggleBottomNav();
        if (toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(false);
            toolbar.setTitle(R.string.app_name);
        }
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel.getCategoriesList().observe(getViewLifecycleOwner(), list -> categories = list);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        viewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userBackupDocument = FirebaseFirestore
                .getInstance()
                .collection(uid)
                .document(BACKUP_DOCUMENT_PATH);

        Preference backupPreference = findPreference("create_backup");
        importBackupPreference = findPreference("import_backup");
        Preference logoutPreference = findPreference("logout_preference");
        Preference deleteAccountPreference = findPreference("delete_account");

        setupCreateBackupPreference(backupPreference);
        setupImportBackupPreference(importBackupPreference);
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
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestScopes(new Scope(YouTubeScopes.YOUTUBE_READONLY))
                    .build();
            GoogleSignInClient signInClient = GoogleSignIn.getClient(requireActivity(), gso);
            userBackupDocument.delete().addOnSuccessListener(runnable ->
                signInClient.revokeAccess().addOnSuccessListener(runnable1 -> {
                    viewModel.deleteAll();
                    Snackbar.make(requireView(), "Account successfully deleted", Snackbar.LENGTH_LONG).show();
                    logout();
                }).addOnFailureListener(runnable1 ->
                    Snackbar.make(requireView(), getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG).show()
                )
            ).addOnFailureListener(runnable ->
                Snackbar.make(requireView(), getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG).show()
            );
            return true;
        });
    }

    private void setupImportBackupPreference(Preference importBackupPreference) {
        if (importBackupPreference == null) {
            return;
        }
        userBackupDocument.get().addOnSuccessListener(doc -> {
            if (doc == null || doc.getData() == null || doc.toObject(CloudBackupObject.class) == null) {
                importBackupPreference.setSummary(getString(R.string.no_backup_found));
            } else {
                CloudBackupObject backupObject = Objects.requireNonNull(doc.toObject(CloudBackupObject.class));
                long backupTimeInMillis =backupObject.getBackupTimeInMilliseconds();
                importBackupPreference.setSummary(
                        getString(R.string.last_backup_date,
                        millisecondsToReadableDate(backupTimeInMillis))
                );
            }
        });
        importBackupPreference.setOnPreferenceClickListener(preference -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.dialog_import_categories_title))
                    .setMessage(getString(R.string.dialog_import_categories_warning))
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> importBackup())
                    .show();
            return true;
        });
    }

    private void setupCreateBackupPreference(Preference backupPreference) {
        if (backupPreference == null) {
            return;
        }
        backupPreference.setOnPreferenceClickListener(preference -> {
            Calendar c = Calendar.getInstance();
            CloudBackupObject backupObject = new CloudBackupObject (categories, c.getTimeInMillis());
            userBackupDocument
                    .set(backupObject)
                    .addOnSuccessListener(runnable -> {
                        Toast.makeText(requireContext(), getString(R.string.backup_done), Toast.LENGTH_SHORT).show();
                        importBackupPreference.setSummary(
                                getString(R.string.last_backup_date,
                                millisecondsToReadableDate(c.getTimeInMillis())));
                        })
                    .addOnFailureListener(runnable ->
                        Toast.makeText(requireContext(), getString(R.string.backup_failed), Toast.LENGTH_SHORT).show()
                    );
            return true;
        });
    }

    private void importBackup() {
        userBackupDocument.get()
                .addOnSuccessListener(doc -> {
                    if (doc == null || doc.getData() == null) {
                        return;
                    }
                    CloudBackupObject backupObject = doc.toObject(CloudBackupObject.class);
                    if (backupObject == null) {
                        return;
                    }
                    viewModel.restoreCategoriesFromBackup(backupObject.getCategories());
                    Snackbar.make(
                            requireView(),
                            getString(R.string.backup_import_categories_failed),
                            Snackbar.LENGTH_SHORT).show();
                })
                .addOnFailureListener(runnable -> Snackbar
                        .make(requireView(), getString(R.string.backup_import_categories_failed), Snackbar.LENGTH_SHORT)
                        .show()
                );
    }

    private static String millisecondsToReadableDate(long timeInMilliseconds) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMilliseconds);
        return c.get(Calendar.YEAR) +
                "/" +
                (c.get(Calendar.MONTH) + 1) +
                "/" +
                c.get(Calendar.DAY_OF_MONTH) +
                ", " +
                c.get(Calendar.HOUR_OF_DAY) +
                ":" +
                c.get(Calendar.MINUTE);
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