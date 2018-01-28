package com.example.candor.youthapp.CHAT;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.candor.youthapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by Mohammad Faisal on 12/3/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List <Messages> mMessageList;
    private FirebaseAuth mAuth;
    private static final int MESSAGE_SENT = 1;
    private static final int MESSAGE_RECEIVED = 2;

    public MessageAdapter(List<Messages> mMessageList){
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType==MESSAGE_SENT){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_sent, parent , false);
        }
        else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_receieved , parent , false);
        }
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {

        Messages c = mMessageList.get(position);
        holder.messageText.setText(c.getMessage());

        /*mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();  //ke chat kortese
        String from_user = c.getFrom();    //kar message amra show korbo ?  jodi duita same hoy tar mane eita amr e message ar na hole eita onnojoner
        if(from_user!=null){
            if(from_user.equals(current_user_id)){

                Log.d("kam hoy  " , "same");
                final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.messageText.getLayoutParams();
                params.gravity = GravityCompat.START; // or GravityCompat.END
                holder.messageText.setLayoutParams(params);
                holder.messageText.setBackgroundColor(Color.WHITE);
                holder.messageText.setTextColor(Color.BLACK);
                holder.messageText.setText(c.getMessage());
            }
            else{
                Log.d("kam hoy  " , "alada");
                final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.messageText.getLayoutParams();
                params.gravity = GravityCompat.END; // or GravityCompat.END
                holder.messageText.setLayoutParams(params);
                holder.messageText.setBackgroundColor(Color.BLACK);
                holder.messageText.setTextColor(Color.WHITE);
                holder.messageText.setText(c.getMessage());
            }
        }*/
    }


    @Override public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        if (mMessageList.get(position).getFrom().equals(current_user_id)) return MESSAGE_SENT;
        return MESSAGE_RECEIVED;
    }



    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder  extends RecyclerView.ViewHolder{

        public TextView messageText;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_item_text);
        }
    }


}