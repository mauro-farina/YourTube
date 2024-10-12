package it.units.sim.yourtube.utils;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import it.units.sim.yourtube.R;

public abstract class NoNavFragment extends Fragment {


    private ActionBar toolbar;

    @Override
    public void onResume() {
        super.onResume();
        toggleBottomNav();
        toolbar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(getToolbarTitle());
            requireActivity().addMenuProvider(new EmptyMenuProvider());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        toggleBottomNav();
        requireActivity().removeMenuProvider(new EmptyMenuProvider());
        if (toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(false);
            toolbar.setTitle(R.string.app_name);
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

    protected abstract String getToolbarTitle();

}
