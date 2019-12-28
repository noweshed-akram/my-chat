package com.blogspot.noweshed.callafriend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class Allusers extends AppCompatActivity {

    private DatabaseReference mUsersData;
    private RecyclerView mUserList;
    private FirebaseUser mCurrentUser;

    private ImageButton backBtn, searchBtn;
    private EditText searchFrnd;

    FirebaseRecyclerOptions<UserAttribute> options;
    FirebaseRecyclerAdapter<UserAttribute, ViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allusers);

        backBtn = findViewById(R.id.fromUserBackId);
        searchBtn = findViewById(R.id.searchFriendId);
        searchFrnd = findViewById(R.id.findfriendsId);

        mUserList = findViewById(R.id.userListId);
        mUserList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mUserList.setLayoutManager(linearLayoutManager);

        mUsersData = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersData.keepSynced(true);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        final String UserUid = mCurrentUser.getUid();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(Allusers.this, MainActivity.class);
                startActivity(Main);
                finish();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchFrnd.getText().toString();
                SearchResult(searchText);
            }
        });

    }

    private void SearchResult(String searchText) {

        Query searchQuery = mUsersData.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        options = new FirebaseRecyclerOptions.Builder<UserAttribute>()
                .setQuery(searchQuery, UserAttribute.class).build();

        adapter = new FirebaseRecyclerAdapter<UserAttribute, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull UserAttribute model) {
                final String user_id = getRef(position).getKey();

                holder.Name.setText(model.getName());
                holder.Status.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.default_propic).into(holder.Propic);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent userprofile = new Intent(Allusers.this, UserProfile.class);
                        userprofile.putExtra("user_id", user_id);
                        startActivity(userprofile);
                        finish();
                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_profile_list, viewGroup, false);
                return new ViewHolder(view);

            }
        };

        adapter.startListening();
        mUserList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        if (adapter != null)
            adapter.stopListening();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }
}
