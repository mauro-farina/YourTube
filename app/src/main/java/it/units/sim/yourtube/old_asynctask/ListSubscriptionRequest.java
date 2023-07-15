package it.units.sim.yourtube.old_asynctask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.google.api.services.youtube.model.SubscriptionSnippet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListSubscriptionRequest extends RequestTask {

    public ListSubscriptionRequest(GoogleAccountCredential credential) {
        super(credential);
    }

    @Override
    protected List<String> getDataFromApi() throws IOException {
        // Get a list of up to 10 files.
        YouTube.Subscriptions.List subscriptionsRequest = youtubeService
                .subscriptions()
                .list("snippet");

        String nextPageToken = "";
        List<String> channelNames = new ArrayList<>();
        do {
            SubscriptionListResponse response = subscriptionsRequest
                    .setMaxResults(50L)
                    .setMine(true)
                    .setPageToken(nextPageToken)
                    .execute();

            nextPageToken = response.getNextPageToken();
            List<Subscription> subscriptions = response.getItems();

            for (Subscription sub : subscriptions) {
                SubscriptionSnippet subData = sub.getSnippet();
                String channelName = subData.getTitle();
                ResourceId channelResourceId = subData.getResourceId();
                channelNames.add(channelName);
            }

        } while(nextPageToken != null);

        return channelNames;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(List<String> output) {
    }

    @Override
    protected void onCancelled() {
    }
}
