package it.units.sim.yourtube.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import it.units.sim.yourtube.YourTubeApp;
import it.units.sim.yourtube.model.Playlist;
import it.units.sim.yourtube.model.VideoData;

public class PlaylistViewModel extends AndroidViewModel {

    private final ExecutorService executorService;
    private final PlaylistDAO dao;
    private final String owner;
    private final LiveData<List<Playlist>> playlists;

    public PlaylistViewModel(@NonNull Application application) {
        super(application);
        YourTubeApp app = getApplication();
        executorService = app.getExecutorService();
        LocalDatabase db = Room
                .databaseBuilder(
                        application.getApplicationContext(),
                        LocalDatabase.class,
                        "playlist-db")
                .build();
        dao = db.playlistDAO();
        owner = app.getGoogleCredential().getSelectedAccountName();
        playlists = dao.getAll(owner);
    }

    public LiveData<List<Playlist>> getPlaylists() {
        return playlists;
    }

    public void createPlaylist(String name) {
        Playlist playlist = new Playlist(name, owner, new ArrayList<>());
        executorService.submit(() -> dao.insertAll(playlist));
    }

    public void addToPlaylist(Playlist playlist, VideoData video) {
        playlist.addVideo(video);
        executorService.submit(() -> dao.update(playlist));
    }

    public void deletePlaylist(Playlist playlist) {
        executorService.submit(() -> dao.delete(playlist));
    }

}
