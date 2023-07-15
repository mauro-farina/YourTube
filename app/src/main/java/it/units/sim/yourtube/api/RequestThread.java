package it.units.sim.yourtube.api;

import android.os.Handler;
import android.os.Looper;

public class RequestThread<T> extends Thread {

    private final YouTubeApiRequest<T> request;
    private final RequestCallback<T> callback;

    public RequestThread(YouTubeApiRequest<T> request, RequestCallback<T> callback) {
        this.request = request;
        this.callback = callback;
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
        } catch (Exception e) {
            System.out.println("THREAD | Exception: " + e.getMessage());
        }
    }

}