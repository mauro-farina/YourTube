package it.units.sim.yourtube.playlist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.category.CategoryOptionsOnClickDialog;
import it.units.sim.yourtube.data.PlaylistViewModel;
import it.units.sim.yourtube.model.Playlist;


public class PlaylistFragment extends Fragment {

    private PlaylistAdapter adapter;
    private PlaylistViewModel viewModel;

    public PlaylistFragment() {
        super(R.layout.fragment_categories);
    }

    public static PlaylistFragment newInstance() {
        return new PlaylistFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ViewModelProvider.AndroidViewModelFactory factory =
//                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
//        viewModel = new ViewModelProvider(requireActivity(), factory).get(PlaylistViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(PlaylistViewModel.class);
        adapter = new PlaylistAdapter(
                new ArrayList<>(),
                v -> {
                    // open PlaylistElementsFragment
                },
                v -> {
                    Playlist playlist = (Playlist) v.getTag();
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
                                    System.out.println("Edit name");
                                } else if (resultAction == CategoryOptionsOnClickDialog.ACTION_DELETE) {
                                    viewModel.deletePlaylist(playlist);
                                }
                            });
                    PlaylistOptionsDialog
                            .newInstance(playlist)
                            .show(fragmentManager, PlaylistOptionsDialog.TAG);
                    return true;
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.playlist_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        viewModel.getPlaylists().observe(getViewLifecycleOwner(), adapter::setList);

        view.findViewById(R.id.playlist_create_fab).setOnClickListener(v -> {
            viewModel.createPlaylist("New Playlist");
        });

        return view;
    }

}