package com.blogspot.noweshed.callafriend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccount extends AppCompatActivity {

    private DatabaseReference mDatabase, mUserRef;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private StorageReference mProfilePic;

    private CircleImageView ProfilePic;
    private TextView mUsername;
    private TextView mStatus, mPhone;
    private TextView mEmail, mCity, mBirth;

    private ImageButton BackBtn;
    private ImageButton EditBtn;
    private ImageButton Change_PicBtn;

    private static final int GALLERY = 1;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        ProfilePic = findViewById(R.id.ProPicId);
        mUsername = findViewById(R.id.mynameId);
        mStatus = findViewById(R.id.mystatusId);
        mEmail = findViewById(R.id.myAccountMailId);
        mPhone = findViewById(R.id.myAccountPhoneId);
        mCity = findViewById(R.id.myAccountCityId);
        mBirth = findViewById(R.id.myAccountBirthId);

        BackBtn = findViewById(R.id.backbtn);
        EditBtn = findViewById(R.id.edit_profilebtn);
        Change_PicBtn = findViewById(R.id.change_picbtn);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        String user_uid = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_uid);
        mDatabase.keepSynced(true);
        mProfilePic = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mUserRef.keepSynced(true);

        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("image").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String mail = dataSnapshot.child("email").getValue().toString();
                String city = dataSnapshot.child("city").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();
                String birth = dataSnapshot.child("birth").getValue().toString();

                Picasso.get().load(image).placeholder(R.drawable.default_propic).into(ProfilePic);
                mUsername.setText(name);
                mStatus.setText(status);
                mEmail.setText(mail);
                mPhone.setText(phone);
                mCity.setText(city);
                mBirth.setText(birth);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(MyAccount.this, MainActivity.class);
                startActivity(Main);
                finish();
            }
        });

        EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateDialog updateDialog = new UpdateDialog();
                updateDialog.show(getSupportFragmentManager(), "Update Dialog");
            }
        });

        Change_PicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY);
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            mProgressDialog = new ProgressDialog(MyAccount.this);
            mProgressDialog.setTitle("Uploading");
            mProgressDialog.setMessage("Please Wait While Your Profile Image is Uploading...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            Uri imageUri = data.getData();
            String current_user_id = mUser.getUid();

            final StorageReference filepath = mProfilePic.child("profile_images").child(current_user_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                mDatabase.child("image").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MyAccount.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                                            mProgressDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(MyAccount.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                }
            });
        }
    }
}
