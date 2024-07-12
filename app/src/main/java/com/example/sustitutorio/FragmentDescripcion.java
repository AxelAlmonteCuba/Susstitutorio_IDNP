package com.example.sustitutorio;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentDescripcion extends Fragment {

    private static final String ARG_ROOM_NAME = "room_name";
    private String roomName;

    public static FragmentDescripcion newInstance(String roomName) {
        FragmentDescripcion fragment = new FragmentDescripcion();
        Bundle args = new Bundle();
        args.putString(ARG_ROOM_NAME, roomName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomName = getArguments().getString(ARG_ROOM_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_descfripcion, container, false);
        TextView textView = view.findViewById(R.id.room_name);
        textView.setText(roomName);
        return view;
    }
}
