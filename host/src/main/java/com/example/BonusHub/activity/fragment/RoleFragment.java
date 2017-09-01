package com.example.BonusHub.activity.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.BonusHub.activity.AuthUtils;
import com.example.BonusHub.activity.activity.LogInActivity;
import com.example.bonuslib.FragmentType;
import com.example.timur.BonusHub.R;

public class RoleFragment extends Fragment {
    private LogInActivity logInActivity;
    private Button hostButton;
    private Button staffButton;
    private Button clientButton;

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
        clientButton= (Button) rootView.findViewById(R.id.client_role_btn);

        hostButton.setOnClickListener(onHostButtonClickListener);
        staffButton.setOnClickListener(onStaffButtonClickListener);
        clientButton.setOnClickListener(onClientButtonClickListener);

        return rootView;
    }

    private View.OnClickListener onHostButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AuthUtils.setHostRole(getActivity());
            goToLoginFragment();
        }
    };

    private View.OnClickListener onStaffButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AuthUtils.setStaffRole(getActivity());
        }
    };

    private View.OnClickListener onClientButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AuthUtils.setClientRole(getActivity());
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