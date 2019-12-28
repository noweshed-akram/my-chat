package com.blogspot.noweshed.callafriend;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateDialog extends AppCompatDialogFragment {
    //firebase Database
    private DatabaseReference UpdateDatabase;
    private FirebaseUser mfirebaseUser;


    private EditText statusUpdateText, nameUpdateText, cityUpdateText, phoneUpdateText;
    private DatePicker birthUpdate;
    private EditText MyEmail;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.update_dialog, null);

        statusUpdateText = view.findViewById(R.id.updatestatusId);
        nameUpdateText = view.findViewById(R.id.updateNameId);
        MyEmail = view.findViewById(R.id.updateEmailId);
        cityUpdateText = view.findViewById(R.id.updateCityId);
        phoneUpdateText = view.findViewById(R.id.updatePhoneId);
        birthUpdate = view.findViewById(R.id.updateBirthId);

        mfirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String user_uid = mfirebaseUser.getUid();

        UpdateDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_uid);

        UpdateDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String city = dataSnapshot.child("city").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();
                //int birth = (int) dataSnapshot.child("birth").getValue();

                nameUpdateText.setText(name);
                MyEmail.setText(email);
                statusUpdateText.setText(status);
                cityUpdateText.setText(city);
                phoneUpdateText.setText(phone);
                //birthUpdate.getDayOfMonth();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        builder.setView(view)
                .setTitle("Update Profile")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String status = statusUpdateText.getText().toString();
                        String name = nameUpdateText.getText().toString();
                        String city = cityUpdateText.getText().toString();
                        String email = MyEmail.getText().toString();
                        String phone = phoneUpdateText.getText().toString();

                        int day = birthUpdate.getDayOfMonth();
                        int month = birthUpdate.getMonth();
                        int year = birthUpdate.getYear();

                        SimpleDateFormat dateFormatter = new SimpleDateFormat("d MMM YYYY");
                        Date d = new Date(year, month, day);
                        String strDate = dateFormatter.format(d);

                        UpdateDatabase.child("status").setValue(status);
                        UpdateDatabase.child("name").setValue(name);
                        UpdateDatabase.child("city").setValue(city);
                        UpdateDatabase.child("email").setValue(email);
                        UpdateDatabase.child("phone").setValue(phone);
                        UpdateDatabase.child("birth").setValue(strDate);
                    }
                });

        return builder.create();
    }
}
