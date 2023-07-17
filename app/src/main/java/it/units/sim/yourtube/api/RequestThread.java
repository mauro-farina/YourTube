package it.units.sim.yourtube.api;

import android.os.Handler;
import android.os.Looper;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

public class RequestThread<T> extends Thread {

    private final YouTubeApiRequest<T> request;
    private final RequestCallback<T> callback;
    private final MissingAuthorizationCallback authorizationCallback;
    public RequestThread(YouTubeApiRequest<T> request, RequestCallback<T> callback) {
        this(request, callback, null);
    }

    public RequestThread(YouTubeApiRequest<T> request, RequestCallback<T> callback, MissingAuthorizationCallback authorizationCallback) {
        this.request = request;
        this.callback = callback;
        this.authorizationCallback = authorizationCallback;
    }

    @Override
    public void run() {
        try {
            T result = request.call();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                // Perform UI operations in main thread
                callback.onResponse(result);
            });
        } catch (UserRecoverableAuthIOException e) {
            System.out.println("missing authorization to access private data...");
            if (authorizationCallback != null) {
                authorizationCallback.onMissingAuthorization(e.getIntent());
            }
        } catch (Exception e) {
            System.out.println("THREAD | Exception: " + e.getMessage());
        }
    }

}