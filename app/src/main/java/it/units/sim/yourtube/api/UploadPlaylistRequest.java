package it.units.sim.yourtube.api;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.util.List;

public class UploadPlaylistRequest extends YouTubeApiRequest<String> {

    private final String channelId;

    public UploadPlaylistRequest(GoogleAccountCredential credential, String channelId) {
        super(credential);
        this.channelId = channelId;
    }

    @Override
    public String call() throws Exception {
        YouTube.Channels.List request = youtubeService
                .channels()
                .list("contentDetails");

        ChannelListResponse response = request
                .setId(channelId)
                .execute();

        List<Channel> channelData = response.getItems();
        if (channelData.size() == 0) {
            throw new RuntimeException("Channel not found");
        }

        return channelData
                .get(0)
                .getContentDetails()
                .getRelatedPlaylists()
                .getUploads();
    }
}
