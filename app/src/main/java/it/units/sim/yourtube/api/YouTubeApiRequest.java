package it.units.sim.yourtube.api;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;

import java.util.concurrent.Callable;

import it.units.sim.yourtube.R;

public abstract class YouTubeApiRequest<T> implements Callable<T> {

    protected final YouTube youtubeService;

    public YouTubeApiRequest(GoogleAccountCredential credential) {
        NetHttpTransport netTransport = new NetHttpTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        youtubeService = new YouTube
                .Builder(netTransport, jsonFactory, credential)
                .setApplicationName(String.valueOf(R.string.app_name))
                .build();
    }

    @Override
    public abstract T call() throws Exception;

}
