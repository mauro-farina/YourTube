package it.units.sim.yourtube.utils;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;

import it.units.sim.yourtube.R;

public class EmptyMenuProvider implements MenuProvider {
    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.setGroupVisible(R.id.toolbar_items_group, false);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
