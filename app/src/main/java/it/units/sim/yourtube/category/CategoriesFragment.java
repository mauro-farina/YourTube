package it.units.sim.yourtube.category;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import it.units.sim.yourtube.R;
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
        adapter = new CategoriesAdapter(new ArrayList<>(), view -> {
            if (!(view.getTag() instanceof Category)) {
                return;
            }
            Category clickedCategory = (Category) view.getTag();
            DialogCategoryOptions dialog = new DialogCategoryOptions(
                    requireContext(),
                    clickedCategory,
                    categoriesViewModel,
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            );
            dialog.show();
        });
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
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.categoryNewFragment);
        });
        return view;
    }

}