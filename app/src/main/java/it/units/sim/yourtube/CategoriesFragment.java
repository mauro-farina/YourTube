package it.units.sim.yourtube;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import it.units.sim.yourtube.model.Category;

public class CategoriesFragment extends Fragment {

    private CategoriesAdapter adapter;
    private CategoriesViewModel categoriesViewModel;

    public CategoriesFragment() {
        super(R.layout.fragment_categories);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoriesViewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);
        categoriesViewModel.fetchCategories();
        adapter = new CategoriesAdapter(new ArrayList<>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.categories_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        categoriesViewModel.getCategoriesList().observe(getViewLifecycleOwner(), adapter::setCategoriesList);

        FloatingActionButton fab = view.findViewById(R.id.categories_add_category_fab);
        fab.setOnClickListener(v -> {
            // dialog
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.add_category_dialog, null);
            EditText input = dialogView.findViewById(R.id.new_category_dialog_name);
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.new_category_dialog_title)
                    .setView(dialogView)
                    .setPositiveButton("ADD", (dialog, which) -> addCategory(input.getText().toString()))
                    .show();
        });
        return view;
    }

    private void addCategory(String name) {
        Toast.makeText(requireContext(), name, Toast.LENGTH_SHORT).show();
        Category newCategory = new Category(name);
        categoriesViewModel.addCategory(newCategory);
    }
}