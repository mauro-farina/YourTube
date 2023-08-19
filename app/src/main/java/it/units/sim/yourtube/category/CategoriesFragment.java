package it.units.sim.yourtube.category;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.data.CategoriesViewModel;
import it.units.sim.yourtube.model.Category;

public class CategoriesFragment extends Fragment {

    private CategoriesAdapter adapter;
    private CategoriesViewModel categoriesViewModel;
    private NavController navController;

    public CategoriesFragment() {
        super(R.layout.fragment_categories);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        categoriesViewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);
        adapter = new CategoriesAdapter(
                new ArrayList<>(),
                view -> {
                    Category category = (Category) view.getTag();
                    FragmentManager fragmentManager = getChildFragmentManager();
                    fragmentManager.setFragmentResultListener(
                            CategoryOptionsOnClickDialog.REQUEST_KEY,
                            getViewLifecycleOwner(),
                            (requestKey, result) -> {
                                if (!requestKey.equals(CategoryOptionsOnClickDialog.REQUEST_KEY))
                                    return;
                                if (result.keySet().size() == 0)
                                    return;

                                int resultAction = result.getInt(CategoryOptionsOnClickDialog.RESULT_KEY);
                                if (resultAction == CategoryOptionsOnClickDialog.ACTION_EDIT) {
                                    openCategoryEditor(category);
                                } else if (resultAction == CategoryOptionsOnClickDialog.ACTION_DELETE) {
                                    deleteCategory(category);
                                }
                            });
                    CategoryOptionsOnClickDialog
                            .newInstance(category)
                            .show(fragmentManager, CategoryOptionsOnClickDialog.TAG);
                },
                CategoriesAdapter.VIEW_CONTEXT_CATEGORIES_LIST
        );
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
        fab.setOnClickListener(v -> navController.navigate(R.id.categoryNewFragment));
        return view;
    }

    private void openCategoryEditor(Category category) {
        Bundle extras = new Bundle();
        extras.putInt("categoryId", category.getId());
        extras.putString("categoryName", category.getName());
        extras.putInt("categoryIcon", category.getDrawableIconResId());
        extras.putStringArrayList("categoryChannels", new ArrayList<>(category.getChannelIds()));
        navController.navigate(R.id.categoryEditFragment, extras);
    }

    private void deleteCategory(Category category) {
        categoriesViewModel.deleteCategory(category);
        Snackbar.make(
                    requireView(),
                    getString(R.string.category_deleted, category.getName()),
                    Snackbar.LENGTH_SHORT)
                .show();
    }

}