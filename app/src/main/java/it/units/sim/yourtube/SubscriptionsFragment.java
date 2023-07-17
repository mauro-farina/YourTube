package it.units.sim.yourtube;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.List;

import it.units.sim.yourtube.api.RequestCallback;
import it.units.sim.yourtube.api.RequestThread;
import it.units.sim.yourtube.api.SubscriptionListRequest;
import it.units.sim.yourtube.api.YouTubeApiRequest;
import it.units.sim.yourtube.model.UserSubscription;

public class SubscriptionsFragment extends Fragment {

    private SubscriptionsAdapter adapter;
    private final MutableLiveData<List<UserSubscription>> myData = new MutableLiveData<>();

    public SubscriptionsFragment() {
        super(R.layout.fragment_subscriptions);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscriptions, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.subscriptions_recycler_view);
        adapter = new SubscriptionsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        myData.observe(getViewLifecycleOwner(), subscriptionList -> {
            // onChanged(): Update the adapter with the new List<UserSubscription>
            adapter.setSubscriptionsList(subscriptionList);
        });
        fetchUserSubscriptions();
    }

    private void fetchUserSubscriptions() {
        GoogleAccountCredential credential = GoogleCredentialManager.getInstance().getCredential();
        YouTubeApiRequest<List<UserSubscription>> subscriptionRequest =
                new SubscriptionListRequest(credential);
        RequestCallback<List<UserSubscription>> subscriptionListCallback = myData::setValue;

        RequestThread<List<UserSubscription>> rThread =
                new RequestThread<>(subscriptionRequest, subscriptionListCallback);
        rThread.start();
    }

}