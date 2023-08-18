package it.units.sim.yourtube;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.units.sim.yourtube.data.CategoriesViewModel;
import it.units.sim.yourtube.model.Category;

public class SettingsFragment extends PreferenceFragmentCompat {

    private ActionBar toolbar;

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
            // TODO: back navigation
            toolbar.setTitle("Settings");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        toggleBottomNav();
        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        androidx.preference.Preference backupPreference = findPreference("backup");
        if (backupPreference != null) {
            backupPreference.setOnPreferenceClickListener(preference -> {
                CategoriesViewModel viewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                LiveData<List<Category>> categoriesLiveData = viewModel.getCategoriesList();

                categoriesLiveData.observe(getViewLifecycleOwner(), list -> {
                    if (list == null) {
                        Toast.makeText(requireContext(), "No data to backup", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Map<String, List<Category>> backup = new HashMap<>();
                    backup.put("categories", categoriesLiveData.getValue());
                    CollectionReference userCategoriesBackupCollection = firestore.collection(uid);
                    userCategoriesBackupCollection
                            .add(backup)
                            .addOnSuccessListener(runnable -> {
                                Toast.makeText(requireContext(), "Done!", Toast.LENGTH_SHORT).show();
                                // TODO: Remove old backup
                            });
                });
                return true;
            });
        }
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