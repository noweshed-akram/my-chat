package com.blogspot.noweshed.callafriend;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView Propic;
    public TextView Name, CallerName, ChatName, RequesterName;
    public TextView Status, RequesterStatus;

    public ImageButton ReqAcptBtn, ReqCnclBtn;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        Propic = (CircleImageView) itemView.findViewById(R.id.PicID);
        Name = (TextView)itemView.findViewById(R.id.NameID);
        Status = (TextView)itemView.findViewById(R.id.StatusID);

        CallerName = (TextView) itemView.findViewById(R.id.userCallNameID);

        ChatName = (TextView)itemView.findViewById(R.id.ChatListNameID);

        RequesterName = (TextView)itemView.findViewById(R.id.friendreqNameID);
        RequesterStatus = (TextView)itemView.findViewById(R.id.friendreqStatusID);

        ReqAcptBtn = (ImageButton) itemView.findViewById(R.id.req_acceptId);
        ReqCnclBtn = (ImageButton) itemView.findViewById(R.id.req_cancelId);
    }
}
