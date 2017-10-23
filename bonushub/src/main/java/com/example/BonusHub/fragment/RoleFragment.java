package com.example.BonusHub.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.BonusHub.utils.AuthUtils;
import com.example.BonusHub.activity.LogInActivity;
import com.example.timur.BonusHub.R;

public class RoleFragment extends Fragment {
    private LogInActivity logInActivity;
    private Button hostButton;
    private Button staffButton;

    View rootView;
    public RoleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logInActivity = (LogInActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_role, container, false);
        hostButton = (Button) rootView.findViewById(R.id.host_role_btn);
        staffButton = (Button) rootView.findViewById(R.id.staff_role_btn);

        hostButton.setOnClickListener(onHostButtonClickListener);
        staffButton.setOnClickListener(onStaffButtonClickListener);

        return rootView;
    }

    private View.OnClickListener onHostButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AuthUtils.setHostRole(getActivity().getApplicationContext());
            goToLoginFragment();
        }
    };

    private View.OnClickListener onStaffButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AuthUtils.setStaffRole(getActivity().getApplicationContext());
            goToLoginFragment();
        }
    };

    private void goToLoginFragment() {
        logInActivity.pushFragment(new LogInFragment(), true);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
