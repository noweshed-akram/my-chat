package com.blogspot.noweshed.callafriend.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.blogspot.noweshed.callafriend.R;
import com.blogspot.noweshed.callafriend.UserAttribute;
import com.blogspot.noweshed.callafriend.UserProfile;
import com.blogspot.noweshed.callafriend.ViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private RecyclerView mReqList;
    private LinearLayout noReqLayout;

    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    FirebaseRecyclerOptions<UserAttribute> options;
    FirebaseRecyclerAdapter<UserAttribute, ViewHolder> adapter;

    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_page, container, false);
        mReqList = mMainView.findViewById(R.id.fragmentPageId);
        noReqLayout = mMainView.findViewById(R.id.noReqLay);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        mFriendReqDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);

        noReqLayout.setVisibility(View.VISIBLE);

        mReqList.setHasFixedSize(true);
        mReqList.setLayoutManager(new LinearLayoutManager(getContext()));

        options = new FirebaseRecyclerOptions.Builder<UserAttribute>()
                .setQuery(mFriendReqDatabase, UserAttribute.class).build();

        return mMainView;

    }

    public void onStart() {
        super.onStart();
}

}
