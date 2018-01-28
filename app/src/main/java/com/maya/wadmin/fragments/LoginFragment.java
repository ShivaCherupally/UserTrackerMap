package com.maya.wadmin.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.maya.wadmin.R;
import com.maya.wadmin.activities.MainActivity;
import com.maya.wadmin.constants.Constants;
import com.maya.wadmin.interfaces.IFragment;
import com.maya.wadmin.utilities.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements IFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    CoordinatorLayout coordinatorLayout;
    EditText etUserName,etPassword;
    LinearLayout llLogin;
    SharedPreferences sharedPreferences;


    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        etUserName = view.findViewById(R.id.etUserName);
        etPassword = view.findViewById(R.id.etPassword);
        llLogin = view.findViewById(R.id.llLogin);
        sharedPreferences = activity().getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);


        llLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                    verifyUser();

            }
        });



        return view;
    }



    private void verifyUser()
    {
        String username = ""+etUserName.getText();
        String password = ""+etPassword.getText();

        if(username.trim().length()>0&&password.trim().length()>0)
        {
            final ProgressDialog progressDialog = Utility.generateProgressDialog(activity());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    Utility.closeProgressDialog(progressDialog);
                    Utility.setBoolen(sharedPreferences,Constants.LOGIN,true);
                    ((MainActivity)getActivity()).changeView();
                }
            },2500);
        }
        else
        {
            showSnackBar("Please fill the fields",0);
        }
    }

    @Override
    public void changeTitle(String title) {

    }

    @Override
    public void showSnackBar(String snackBarText, int type) {
        Utility.showSnackBar(activity(),coordinatorLayout,snackBarText,0);
    }

    @Override
    public Activity activity() {
        return getActivity();
    }
}
