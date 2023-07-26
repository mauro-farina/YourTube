package it.units.sim.yourtube;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import it.units.sim.yourtube.data.CategoryDAO;
import it.units.sim.yourtube.data.LocalDatabase;
import it.units.sim.yourtube.model.Category;

public class CategoriesFragment extends Fragment {

    private CategoriesAdapter adapter;
    private List<Category> categories;

    public CategoriesFragment() {
        super(R.layout.fragment_categories);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalDatabase db = Room
                .databaseBuilder(
                    requireActivity().getApplicationContext(),
                    LocalDatabase.class,
                    "categories-db")
                .build();
        CategoryDAO categoryDao = db.categoryDao();
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<List<Category>> future = executor.submit(categoryDao::getAll);
        try {
            categories = future.get();
            System.out.println(categories);
        } catch (ExecutionException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.categories_recycler_view);
        categories = new ArrayList<>();
        adapter = new CategoriesAdapter(categories);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
}