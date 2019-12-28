package com.blogspot.noweshed.callafriend;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    private CircleImageView userImage;
    private TextView userName, userStatus, usermail, userPhone, userCity, userBirth;
    private Button requestbtn, cancelbtn;

    private FirebaseUser mCurrentUser;
    private String mCurrent_state;

    private DatabaseReference mRootdatabaseReference;

    private ImageButton MsgBtn, CallBtn, VideoCallBtn, userBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mRootdatabaseReference = FirebaseDatabase.getInstance().getReference();
        mRootdatabaseReference.keepSynced(true);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        userImage = (CircleImageView) findViewById(R.id.userprofile_imageid);
        userName = (TextView) findViewById(R.id.userprofile_nameid);
        userStatus = (TextView) findViewById(R.id.userprofile_statusid);
        requestbtn = (Button) findViewById(R.id.userprofile_requestid);
        cancelbtn = (Button) findViewById(R.id.userprofile_cancelid);
        usermail = (TextView) findViewById(R.id.userprofile_emailId);
        userPhone = (TextView) findViewById(R.id.userprofile_phoneId);
        userCity = (TextView) findViewById(R.id.userprofile_cityId);
        userBirth = (TextView) findViewById(R.id.userprofile_birthId);

        MsgBtn = (ImageButton) findViewById(R.id.userMsgId);
        CallBtn = (ImageButton) findViewById(R.id.userCallId);
        VideoCallBtn = (ImageButton) findViewById(R.id.userVideocallId);
        userBackBtn = (ImageButton) findViewById(R.id.user_backbtn);

        mCurrent_state = "not_friend";

        cancelbtn.setVisibility(View.GONE);
        cancelbtn.setEnabled(false);

        MsgBtn.setVisibility(View.GONE);
        MsgBtn.setEnabled(false);
        CallBtn.setVisibility(View.GONE);
        CallBtn.setEnabled(false);
        VideoCallBtn.setVisibility(View.GONE);
        VideoCallBtn.setEnabled(false);

        userBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent UserList = new Intent(UserProfile.this, MainActivity.class);
                startActivity(UserList);
                finish();
            }
        });

        MsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userprofile = new Intent(UserProfile.this, ChatActivity.class);
                userprofile.putExtra("user_id", user_id);
                startActivity(userprofile);
                finish();
            }
        });

        mRootdatabaseReference.child("Users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("image").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String mail = dataSnapshot.child("email").getValue().toString();
                String city = dataSnapshot.child("city").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();
                String birth = dataSnapshot.child("birth").getValue().toString();

                Picasso.get().load(image).placeholder(R.drawable.default_propic).into(userImage);
                userName.setText(name);
                userStatus.setText(status);
                usermail.setText(mail);
                userCity.setText(city);
                userPhone.setText(phone);
                userBirth.setText(birth);


                // Friend list / request
                mRootdatabaseReference.child("Friend_req").child(mCurrentUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(user_id)) {
                                    String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                                    if (req_type.equals("received")) {
                                       


                                    } else if (req_type.equals("sent")) {
                                        
                                    }
                                } else {
                                    mRootdatabaseReference.child("Friends").child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(user_id)) {
                                                
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCurrent_state.equals("req_received")) {
                    mRootdatabaseReference.child("Friend_req").child(mCurrentUser.getUid()).child(user_id).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mRootdatabaseReference.child("Friend_req").child(user_id).child(mCurrentUser.getUid()).removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        mRootdatabaseReference.child("Notifications").child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                //remove notification
                                                            }
                                                        });

                                                        

                                                        Toast.makeText(UserProfile.this, "Decline Request.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(UserProfile.this, "Failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        requestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send Request
                requestbtn.setEnabled(false);
                // not a friend
                if (mCurrent_state.equals("not_friend")) {
                    mRootdatabaseReference.child("Friend_req").child(mCurrentUser.getUid()).child(user_id).child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mRootdatabaseReference.child("Friend_req").child(user_id).child(mCurrentUser.getUid()).child("request_type").setValue("received")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        //notification
                                                        HashMap<String, String> notificationData = new HashMap<>();
                                                        notificationData.put("from", mCurrentUser.getUid());
                                                        notificationData.put("type", "request");

                                                        mRootdatabaseReference.child("Notifications").child(user_id).push().setValue(notificationData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    

                                                                    Toast.makeText(UserProfile.this, "Request Sent Successfully .", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(UserProfile.this, "Request Failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                // Cancel Request
                if (mCurrent_state.equals("req_sent")) {
                    mRootdatabaseReference.child("Friend_req").child(mCurrentUser.getUid()).child(user_id).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        mRootdatabaseReference.child("Notifications").child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //remove notification
                                            }
                                        });

                                        mRootdatabaseReference.child("Friend_req").child(user_id).child(mCurrentUser.getUid()).removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        

                                                        Toast.makeText(UserProfile.this, "Request Canceled.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(UserProfile.this, "Failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                // Req received
                if (mCurrent_state.equals("req_received")) {
                    final String CurrentDate = DateFormat.getDateTimeInstance().format(new Date());

                    mRootdatabaseReference.child("Friends").child(mCurrentUser.getUid()).child(user_id).child("CurrentDate").setValue(CurrentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mRootdatabaseReference.child("Friends").child(user_id).child(mCurrentUser.getUid()).child("CurrentDate").setValue(CurrentDate)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mRootdatabaseReference.child("Friend_req").child(mCurrentUser.getUid()).child(user_id).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {

                                                                        mRootdatabaseReference.child("Friend_req").child(user_id).child(mCurrentUser.getUid()).removeValue()
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {

                                                                                        mRootdatabaseReference.child("Notifications").child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                //remove notification
                                                                                            }
                                                                                        });

                                                                                         Toast.makeText(UserProfile.this, "Request Received.", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                    } else {
                                                                        Toast.makeText(UserProfile.this, "Failed.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            });

                                }
                            });
                }
                // friend state
                if (mCurrent_state.equals("friend")) {

                    mRootdatabaseReference.child("Message").child(mCurrentUser.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mRootdatabaseReference.child("Message").child(user_id).child(mCurrentUser.getUid()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    // data removed!
                                                }
                                            });
                                }
                            });

                    mRootdatabaseReference.child("Chat").child(mCurrentUser.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mRootdatabaseReference.child("Chat").child(user_id).child(mCurrentUser.getUid()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    // data removed!
                                                }
                                            });
                                }
                            });

                    mRootdatabaseReference.child("Friends").child(mCurrentUser.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mRootdatabaseReference.child("Friends").child(user_id).child(mCurrentUser.getUid()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    

                                                    Toast.makeText(UserProfile.this, "Unfriend.", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            });
                }
            }
        });
    }
}
