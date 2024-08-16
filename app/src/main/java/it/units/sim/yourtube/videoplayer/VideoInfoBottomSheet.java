package it.units.sim.yourtube.videoplayer;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.VideoData;


public class VideoInfoBottomSheet extends BottomSheetDialogFragment {

    private final static String VIDEO_ARG = "videoData";
    private VideoData mVideoData;

    public VideoInfoBottomSheet() {
        // Required empty public constructor
    }


    public static VideoInfoBottomSheet newInstance(VideoData videoData) {
        VideoInfoBottomSheet fragment = new VideoInfoBottomSheet();
        Bundle args = new Bundle();
        args.putParcelable(VIDEO_ARG, videoData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVideoData = getArguments().getParcelable(VIDEO_ARG);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_info_bottom_sheet, container, false);
        TextView title = view.findViewById(R.id.video_info_bottom_sheet_title);
        TextView description = view.findViewById(R.id.video_info_bottom_sheet_description);
        if (mVideoData != null) {
            title.setText(mVideoData.getTitle());
            description.setText(mVideoData.getDescription());
        } else {
            title.setText(R.string.error);
        }
        return view;
    }
}