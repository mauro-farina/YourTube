package it.units.sim.yourtube.video;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.category.CategoriesAdapter;
import it.units.sim.yourtube.data.CategoriesViewModel;
import it.units.sim.yourtube.model.Category;

public class FilterVideosByCategoryDialog extends DialogFragment {

    public static final String TAG = "FILTER_VIDEOS_BY_CATEGORY_DIALOG";
    public static final String REQUEST_KEY = "updateCategoryFilter";
    public static final String RESULT_KEY = "category";
    private Bundle result;

    public static FilterVideosByCategoryDialog newInstance() {
        return new FilterVideosByCategoryDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        result = new Bundle();

        CategoriesAdapter adapter = new CategoriesAdapter(
                new ArrayList<>(),
                v -> {
                    Category clickedCategory = (Category) v.getTag();
                    result.putParcelable(RESULT_KEY, clickedCategory);
                    dismiss();
                },
                CategoriesAdapter.VIEW_CONTEXT_VIDEOS_FILTER
        );
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_videos_category_filter, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.dialog_videos_category_filter_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        CategoriesViewModel viewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);
        viewModel.getCategoriesList().observe(requireParentFragment(), adapter::setCategoriesList);

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Category filter")
                .setView(dialogView)
                .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Remove filter", (dialog, which) -> {
                    result.putParcelable(RESULT_KEY, null);
                    dialog.dismiss();
                })
                .create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
    }

}
