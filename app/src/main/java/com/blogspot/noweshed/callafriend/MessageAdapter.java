package com.blogspot.noweshed.callafriend;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference SendUser;

    public MessageAdapter(List<Message> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @NonNull
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_messages, parent, false);

        return new MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView msgSendText, msgReceiveText;
        public CircleImageView msgReceiveProPic;
        public ImageView sendMark, msgSendImage, msgRcvImage, imageSendMark;

        public MessageViewHolder(View view) {
            super(view);

            msgSendText = view.findViewById(R.id.SendMsgId);
            msgReceiveText = view.findViewById(R.id.ReceiveMsgId);
            msgReceiveProPic = view.findViewById(R.id.RcvMsgProPicId);
            sendMark = view.findViewById(R.id.send);
            imageSendMark = view.findViewById(R.id.imageSend);
            msgSendImage = view.findViewById(R.id.sendImageMsg);
            msgRcvImage = view.findViewById(R.id.recvImageMsg);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {
        mAuth = FirebaseAuth.getInstance();

        String Current_user_id = mAuth.getCurrentUser().getUid();

        SendUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (from_user.equals(Current_user_id)) {
            viewHolder.msgReceiveText.setVisibility(View.GONE);
            viewHolder.msgReceiveProPic.setVisibility(View.GONE);
            viewHolder.msgRcvImage.setVisibility(View.GONE);

            if (msg_type.equals("text")) {
                viewHolder.msgSendText.setVisibility(View.VISIBLE);
                viewHolder.sendMark.setVisibility(View.VISIBLE);
                viewHolder.msgSendImage.setVisibility(View.GONE);
                viewHolder.imageSendMark.setVisibility(View.GONE);

                viewHolder.msgSendText.setText(c.getMessage());
            } else {
                viewHolder.msgSendImage.setVisibility(View.VISIBLE);
                viewHolder.imageSendMark.setVisibility(View.VISIBLE);
                viewHolder.msgSendText.setVisibility(View.GONE);
                viewHolder.sendMark.setVisibility(View.GONE);

                Picasso.get().load(c.getMessage()).placeholder(R.drawable.ic_picbtn).into(viewHolder.msgSendImage);
            }

        } else {
            viewHolder.msgSendText.setVisibility(View.GONE);
            viewHolder.sendMark.setVisibility(View.GONE);
            viewHolder.msgSendImage.setVisibility(View.GONE);

            viewHolder.msgReceiveProPic.setVisibility(View.VISIBLE);

            if (msg_type.equals("text")) {
                viewHolder.msgReceiveText.setVisibility(View.VISIBLE);
                viewHolder.msgRcvImage.setVisibility(View.GONE);

                viewHolder.msgReceiveText.setText(c.getMessage());
            } else {
                viewHolder.msgReceiveText.setVisibility(View.GONE);
                viewHolder.msgRcvImage.setVisibility(View.VISIBLE);

                Picasso.get().load(c.getMessage()).placeholder(R.drawable.ic_picbtn).into(viewHolder.msgRcvImage);
            }

        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
