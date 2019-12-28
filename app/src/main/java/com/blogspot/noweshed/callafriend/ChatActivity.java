package com.blogspot.noweshed.callafriend;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.blogspot.noweshed.callafriend.Fragments.APIService;
import com.blogspot.noweshed.callafriend.Notification.Client;
import com.blogspot.noweshed.callafriend.Notification.Data;
import com.blogspot.noweshed.callafriend.Notification.MyResponse;
import com.blogspot.noweshed.callafriend.Notification.Sender;
import com.blogspot.noweshed.callafriend.Notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private String mChatUser;
    private Toolbar mChatToolbar;

    private TextView ChatUsername, ChatOnline;
    private CircleImageView ChatProPic;
    private ImageButton ChatToBack;

    private DatabaseReference mRootDatabase, mUserRef;
    private StorageReference mImageStorage;
    private FirebaseAuth mAuth;

    private ImageButton SendMsg, SendImage;
    private EditText MsgText;
    private RecyclerView MessageList;
    private SwipeRefreshLayout refreshLayout;

    private final List<Message> msgList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;

    private MessageAdapter messageAdapter;

    private static final int TOTAL_MSG_LOAD = 12;
    public static final int GALLERY_PICK = 1;
    private int mCurrentpage = 1;
    private int itemPosition = 0;
    private String lastKey = "";
    private String prevKey = "";

    private String CurrentUserId;

    APIService apiService;
    boolean notify = false;

//    // sinch voice call
//    SinchClient sinchClient;
//    Call call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        ChatOnline = findViewById(R.id.onlineStatusID);
        ChatUsername = findViewById(R.id.chatUsernameId);
        ChatProPic = findViewById(R.id.chatProPicId);
        ChatToBack = findViewById(R.id.chatbackbtn);

        SendMsg = findViewById(R.id.chatSendId);
        SendImage = findViewById(R.id.sendImageId);
        MsgText = findViewById(R.id.chatTextId);

        messageAdapter = new MessageAdapter(msgList);

        refreshLayout = findViewById(R.id.swipLayoutId);
        MessageList = findViewById(R.id.messageListRecycle);
        mLinearLayout = new LinearLayoutManager(this);
        MessageList.setHasFixedSize(true);
        MessageList.setLayoutManager(mLinearLayout);

        MessageList.setAdapter(messageAdapter);

        mChatToolbar = (Toolbar) findViewById(R.id.chatappbarId);
        setSupportActionBar(mChatToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        mRootDatabase = FirebaseDatabase.getInstance().getReference();
        mRootDatabase.keepSynced(true);
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mUserRef.keepSynced(true);

        mChatUser = getIntent().getStringExtra("user_id");

        ChatToBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(Main);
                finish();
            }
        });

        mRootDatabase.child("Users").child(mChatUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String chat_user_name = dataSnapshot.child("name").getValue().toString();

                Picasso.get().load(image).placeholder(R.drawable.default_propic).into(ChatProPic);
                ChatUsername.setText(chat_user_name);

                if (online.equals("true")) {
                    ChatOnline.setText("Active Now");
                } else {
                    GetLastTime getLastTime = new GetLastTime();
                    long lastTime = Long.parseLong(online);
                    String lastSeenTime = getLastTime.getLastTime(lastTime);

                    ChatOnline.setText(lastSeenTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        mRootDatabase.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mChatUser)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + CurrentUserId + "/" + mChatUser, chatAddMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + CurrentUserId, chatAddMap);

                    mRootDatabase.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("Chat_Log", databaseError.getDetails().toLowerCase());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                sendMessage();
            }
        });

        SendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendImage = new Intent();
                sendImage.setType("image/*");
                sendImage.setAction(Intent.ACTION_GET_CONTENT);
                notify = true;
                startActivityForResult(Intent.createChooser(sendImage, "SELECT IMAGE"), GALLERY_PICK);
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentpage++;
                itemPosition = 0;

                loadMoreMessage();
            }
        });

        loadMessage();

        // sinch voice call

//        sinchClient = Sinch.getSinchClientBuilder()
//                .context(this)
//                .userId(mChatUser)
//                .applicationKey("b5813b40-85b0-40e6-a5d3-5ff5eb946f7a")
//                .applicationSecret("DPgzAirBUEyNdWKejeZTUQ==")
//                .environmentHost("clientapi.sinch.com")
//                .build();
//
//        sinchClient.setSupportCalling(true);
//        sinchClient.startListeningOnActiveConnection();
//
//        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener(){
//
//        });
//
//        sinchClient.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserRef.child("online").setValue(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

        }
    }

    private void loadMoreMessage() {

        DatabaseReference messageRef = mRootDatabase.child("Message").child(CurrentUserId).child(mChatUser);

        Query msgQuery = messageRef.orderByKey().endAt(lastKey).limitToLast(10);
        msgQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message msg = dataSnapshot.getValue(Message.class);
                String msgKey = dataSnapshot.getKey();

                
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadMessage() {

        DatabaseReference messageRef = mRootDatabase.child("Message").child(CurrentUserId).child(mChatUser);

        Query msgQuery = messageRef.limitToLast(mCurrentpage * TOTAL_MSG_LOAD);

        msgQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String message = MsgText.getText().toString();

        if (!TextUtils.isEmpty(message)) {

            

            mRootDatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("Chat_Log", databaseError.getDetails().toLowerCase());
                    }
                }
            });
        }

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chat")
                .child(mChatUser)
                .child(CurrentUserId);
        chatRefReceiver.child("id").setValue(CurrentUserId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(CurrentUserId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserAttribute user = dataSnapshot.getValue(UserAttribute.class);
                if (notify) {
                    sendNotifiaction(mChatUser, user.getName(), message);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.ChatInfoId) {
            Intent userprofile = new Intent(ChatActivity.this, UserProfile.class);
            userprofile.putExtra("user_id", mChatUser);
            startActivity(userprofile);
            finish();
        }
        if (item.getItemId() == R.id.ChatCallId) {
//            callUser();
            Toast.makeText(ChatActivity.this, "Calling..!", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.ChatVideoCall) {

            Toast.makeText(ChatActivity.this, "Calling..!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

}