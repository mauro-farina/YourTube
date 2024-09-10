package it.units.sim.yourtube.api;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.security.InvalidParameterException;


public class ChannelInfoRequest extends AbstractYouTubeRequest<Channel> {

    private final String channelId;

    public ChannelInfoRequest(GoogleAccountCredential credential,
                              Callback<Channel> callback,
                              String channelId) {
        super(credential, callback);
        this.channelId = channelId;
    }

    @Override
    protected Result<Channel> performRequest() throws IOException {
        YouTube.Channels.List request = youtubeService
                .channels()
                .list("snippet,brandingSettings")
                .setId(channelId)
                .setMaxResults(1L);

        ChannelListResponse response = request.execute();

        if (response.getItems().size() == 0) {
            return new Result.Error<>(new InvalidParameterException("Channel not found"));
        }

        return new Result.Success<>(response.getItems().get(0));
    }


}
