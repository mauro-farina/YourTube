package it.units.sim.yourtube.api;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionListResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.units.sim.yourtube.model.UserSubscription;

public class SubscriptionListRequest extends YouTubeApiRequest<List<UserSubscription>> {

    public SubscriptionListRequest(GoogleAccountCredential credential) {
        super(credential);
    }

    @Override
    public List<UserSubscription> call() throws Exception {
        YouTube.Subscriptions.List subscriptionsRequest = youtubeService
                .subscriptions()
                .list("snippet");

        String nextPageToken = "";
        List<UserSubscription> userSubscriptions = new ArrayList<>();

        do {
            SubscriptionListResponse response = subscriptionsRequest
                    .setMaxResults(50L)
                    .setMine(true)
                    .setPageToken(nextPageToken)
                    .execute();

            nextPageToken = response.getNextPageToken();
            List<Subscription> subscriptions = response.getItems();

            userSubscriptions.addAll(
                    subscriptions.stream()
                            .map(UserSubscription::new)
                            .collect(Collectors.toList())
            );

        } while(nextPageToken != null);

        return userSubscriptions;
    }
}
