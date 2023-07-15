package it.units.sim.yourtube;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.ArrayList;
import java.util.List;

import it.units.sim.yourtube.api.RequestCallback;
import it.units.sim.yourtube.api.RequestThread;
import it.units.sim.yourtube.api.SubscriptionListRequest;
import it.units.sim.yourtube.api.YouTubeApiRequest;
import it.units.sim.yourtube.model.UserSubscription;

public class SubscriptionsFragment extends Fragment {

    private List<UserSubscription> userSubscriptionsList;
    private RecyclerView recyclerView;
    private SubscriptionsAdapter adapter;

    public SubscriptionsFragment() {
        super(R.layout.fragment_subscriptions);
        this.userSubscriptionsList = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subscriptions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.subscriptions_recycler_view);
        adapter = new SubscriptionsAdapter(userSubscriptionsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fetchUserSubscriptions();
    }

    private void fetchUserSubscriptions() {
        GoogleAccountCredential credential = GoogleCredentialManager.getInstance().getCredential();
        YouTubeApiRequest<List<UserSubscription>> subscriptionRequest =
                new SubscriptionListRequest(credential);
        RequestCallback<List<UserSubscription>> subscriptionListCallback = subscriptionList -> {
            this.userSubscriptionsList = subscriptionList;
            adapter.setSubscriptionsList(subscriptionList);
        };
        RequestThread<List<UserSubscription>> rThread =
                new RequestThread<>(subscriptionRequest, subscriptionListCallback);
        rThread.start();
    }

}