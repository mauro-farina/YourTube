package it.units.sim.yourtube.api;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.units.sim.yourtube.AbstractYouTubeRequest;
import it.units.sim.yourtube.model.UserSubscription;

public class SubscriptionListRequest extends AbstractYouTubeRequest<List<UserSubscription>> {

    public SubscriptionListRequest(GoogleAccountCredential credential,
                                   Callback<List<UserSubscription>> callback) {
        super(credential, callback);
    }

    @Override
    protected Result<List<UserSubscription>> performRequest() throws IOException {
        List<UserSubscription> userSubscriptions = new ArrayList<>();

        YouTube.Subscriptions.List subscriptionsRequest = youtubeService
                .subscriptions()
                .list("snippet")
                .setMaxResults(50L)
                .setMine(true);

        String nextPageToken = "";
        do {
            SubscriptionListResponse response = subscriptionsRequest
                    .setPageToken(nextPageToken)
                    .execute();

            nextPageToken = response.getNextPageToken();
            List<Subscription> subscriptions = response.getItems();

            userSubscriptions.addAll(
                    subscriptions
                            .stream()
                            .map(UserSubscription::new)
                            .collect(Collectors.toList())
            );
        } while(nextPageToken != null);

        return new Result.Success<>(userSubscriptions);
    }
}
