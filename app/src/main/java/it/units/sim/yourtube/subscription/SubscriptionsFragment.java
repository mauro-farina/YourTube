package it.units.sim.yourtube.subscription;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.units.sim.yourtube.YouTubeDataViewModel;
import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.UserSubscription;

public class SubscriptionsFragment extends Fragment {

    private YouTubeDataViewModel viewModel;
    private SubscriptionsAdapter adapter;

    public SubscriptionsFragment() {
        super(R.layout.fragment_subscriptions);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(YouTubeDataViewModel.class);
        viewModel.fetchUserSubscriptions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscriptions, container, false);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        RecyclerView recyclerView = view.findViewById(R.id.subscriptions_recycler_view);
        adapter = new SubscriptionsAdapter(
                viewModel.getSubscriptionsList().getValue(),
                v -> {
                    Bundle args = new Bundle();
                    args.putParcelable("channel", (UserSubscription) v.getTag());
                    navController.navigate(R.id.channelVideosFragment, args);
                }
//                v -> SubscriptionInfoDialog
//                            .newInstance((UserSubscription) v.getTag())
//                            .show(getChildFragmentManager(), SubscriptionInfoDialog.TAG)
        );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        viewModel.getSubscriptionsList().observe(getViewLifecycleOwner(), adapter::setSubscriptionsList);
    }

}