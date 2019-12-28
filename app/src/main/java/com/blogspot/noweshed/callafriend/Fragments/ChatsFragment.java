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

import com.blogspot.noweshed.callafriend.ChatActivity;
import com.blogspot.noweshed.callafriend.Notification.Token;
import com.blogspot.noweshed.callafriend.R;
import com.blogspot.noweshed.callafriend.UserAttribute;
import com.blogspot.noweshed.callafriend.ViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private RecyclerView mChatList;
    private LinearLayout noChatLayout;

    private DatabaseReference mFriendDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    FirebaseRecyclerOptions<UserAttribute> options;
    FirebaseRecyclerAdapter<UserAttribute, ViewHolder> adapter;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_page, container, false);
        mChatList = mMainView.findViewById(R.id.fragmentPageId);
        noChatLayout = mMainView.findViewById(R.id.noChatLay);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);
        mFriendDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);

        noChatLayout.setVisibility(View.VISIBLE);

        mChatList.setHasFixedSize(true);
        mChatList.setLayoutManager(new LinearLayoutManager(getContext()));

        options = new FirebaseRecyclerOptions.Builder<UserAttribute>()
                .setQuery(mFriendDatabase, UserAttribute.class).build();

        updateToken(FirebaseInstanceId.getInstance().getToken());

        return mMainView;

    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token1);
    }


    public void onStart() {
        super.onStart();

        
    }

}
