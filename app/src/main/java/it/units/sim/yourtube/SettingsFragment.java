package it.units.sim.yourtube;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import it.units.sim.yourtube.data.CategoriesViewModel;
import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.CloudBackupObject;

public class SettingsFragment extends PreferenceFragmentCompat {

    private ActionBar toolbar;
    private CategoriesViewModel viewModel;
    private static final String BACKUP_DOCUMENT_PATH = "categoriesBackup";
    private DocumentReference userBackupDocument;
    private Preference importBackupPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
    }

    @Override
    public void onResume() {
        super.onResume();
        toggleBottomNav();
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

        setupCreateBackupPreference(backupPreference);
        setupImportBackupPreference(importBackupPreference);
    }

    private void setupImportBackupPreference(Preference importBackupPreference) {
        if (importBackupPreference == null) {
            return;
        }
        userBackupDocument.get().addOnSuccessListener(doc -> {
            if (doc == null || doc.getData() == null || doc.toObject(CloudBackupObject.class) == null) {
                importBackupPreference.setSummary("No backups found");
            } else {
                CloudBackupObject backupObject = Objects.requireNonNull(doc.toObject(CloudBackupObject.class));
                long backupTimeInMillis =backupObject.getBackupTimeInMilliseconds();
                importBackupPreference.setSummary("Last backup: " + millisecondsToReadableDate(backupTimeInMillis));
            }
        });
        importBackupPreference.setOnPreferenceClickListener(preference -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Import categories from backup")
                    .setMessage("By proceeding you will lose all your locally-stored 'Categories' "
                            + "and the only available 'Categories' will be the ones imported from the cloud. "
                            + "Do you want to continue?")
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Yes", (dialog, which) -> importBackup())
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
            LiveData<List<Category>> categoriesLiveData = viewModel.getCategoriesList();
            categoriesLiveData.observe(getViewLifecycleOwner(), list -> {
                if (list == null) {
                    Toast.makeText(requireContext(), getString(R.string.backup_no_data), Toast.LENGTH_SHORT).show();
                    return;
                }
                CloudBackupObject backupObject = new CloudBackupObject (categoriesLiveData.getValue(), c.getTimeInMillis());
                userBackupDocument
                        .set(backupObject)
                        .addOnSuccessListener(runnable -> {
                                Toast.makeText(requireContext(), getString(R.string.backup_done), Toast.LENGTH_SHORT).show();
                                importBackupPreference.setSummary("Last backup: " + millisecondsToReadableDate(c.getTimeInMillis()));
                            }
                        )
                        .addOnFailureListener(runnable ->
                                Toast.makeText(requireContext(), getString(R.string.backup_failed), Toast.LENGTH_SHORT).show()
                        );
            });
            return true;
        });
    }

    private void importBackup() {
        userBackupDocument.get().addOnSuccessListener(doc -> {
            if (doc == null || doc.getData() == null) {
                return;
            }
            CloudBackupObject backupObject = doc.toObject(CloudBackupObject.class);
            if (backupObject == null) {
                return;
            }
            System.out.println(backupObject.getCategories());
            viewModel.restoreCategoriesFromBackup(backupObject.getCategories());
        })
        .addOnFailureListener(runnable -> System.out.println("*** fail ***"));
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