package it.units.sim.yourtube.api;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.api.Result;

public abstract class AbstractYouTubeRequest<T> implements Runnable {

    public interface Callback<T> {
        void onComplete(Result<T> result);
    }

    protected final YouTube youtubeService;
    private final Callback<T> callback;

    protected AbstractYouTubeRequest(GoogleAccountCredential credential,
                                   Callback<T> callback) {
        this.callback = callback;
        NetHttpTransport netTransport = new NetHttpTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        youtubeService = new YouTube
                .Builder(netTransport, jsonFactory, credential)
                .setApplicationName(String.valueOf(R.string.app_name))
                .build();
    }

    @Override
    public void run() {
        Result<T> result;
        try {
            result = performRequest();
        } catch (IOException e) {
            result = new Result.Error<>(e);
        }
        callback.onComplete(result);
    }

    protected abstract Result<T> performRequest() throws IOException;

}
