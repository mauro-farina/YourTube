package it.units.sim.yourtube.old_asynctask;

import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.util.List;

import it.units.sim.yourtube.R;

public abstract class RequestTask extends AsyncTask<Void, Void, List<String>> {
    protected final YouTube youtubeService;
    protected Exception mLastError;

    public RequestTask(GoogleAccountCredential credential) {
        NetHttpTransport netTransport = new NetHttpTransport();
        //HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        youtubeService = new com.google.api.services.youtube.YouTube.Builder(
                netTransport, jsonFactory, credential)
                .setApplicationName(String.valueOf(R.string.app_name))
                .build();
    }

    /**
     * Background task to call YouTube Data API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<String> doInBackground(Void... params) {
        try {
            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    /**
     * @return List of Strings containing information about the channel.
     * @throws IOException
     */
    protected abstract List<String> getDataFromApi() throws IOException;

    @Override
    protected abstract void onPreExecute();

    @Override
    protected abstract void onPostExecute(List<String> output);

    @Override
    protected abstract void onCancelled();
//        mProgress.hide();
//        if (mLastError != null) {
//            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
//                showGooglePlayServicesAvailabilityErrorDialog(
//                        ((GooglePlayServicesAvailabilityIOException) mLastError)
//                                .getConnectionStatusCode());
//            } else if (mLastError instanceof UserRecoverableAuthIOException) {
//                startActivityForResult(
//                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
//                        MainActivity.REQUEST_AUTHORIZATION);
//            } else {
//                mOutputText.setText("The following error occurred:\n" + mLastError.getMessage());
//            }
//        } else {
//            mOutputText.setText("Request cancelled.");
//        }

}
