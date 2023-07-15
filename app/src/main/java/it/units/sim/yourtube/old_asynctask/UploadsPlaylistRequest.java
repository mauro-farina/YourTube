package it.units.sim.yourtube.old_asynctask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.util.List;

public class UploadsPlaylistRequest extends RequestTask {

    private String channelId;

    public UploadsPlaylistRequest(GoogleAccountCredential credential, String channelId) {
        super(credential);
        this.channelId = channelId;
    }

    @Override
    protected List<String> getDataFromApi() throws IOException {
        YouTube.Channels.List request = youtubeService.channels().list("contentDetails");
        ChannelListResponse response = request.setId(channelId).execute();

        List<Channel> channelData = response.getItems();
        if (channelData.size() == 0) {
            throw new RuntimeException("Channel not found");
        }

        String uploadsPlaylist = channelData.get(0)
                .getContentDetails()
                .getRelatedPlaylists()
                .getUploads();

        return List.of(uploadsPlaylist);
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
