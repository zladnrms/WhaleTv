package zladnrms.defytech.kim.BroadcastTv.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.adapter.contract.ChatListAdapterContract;
import zladnrms.defytech.kim.BroadcastTv.adapter.model.ChatListDataModel;
import zladnrms.defytech.kim.BroadcastTv.databinding.RecyclerviewChatListBinding;
import zladnrms.defytech.kim.BroadcastTv.model.domain.ChatInfo;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> implements ChatListDataModel, ChatListAdapterContract.View {

    private ArrayList<ChatInfo> chatList = new ArrayList<ChatInfo>();
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerviewChatListBinding binding;

        public ViewHolder(View view) {
            super(view);

            binding = DataBindingUtil.bind(itemView);
        }
    }

    public ChatListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_chat_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        ChatInfo chatInfo = chatList.get(position);
        holder.binding.setChatdata(chatInfo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public void add(ChatInfo chatInfo) {
        chatList.add(chatInfo);
    }

    @Override
    public void refresh() {
        notifyDataSetChanged();
    }

    @Override
    public void remove(int position) {

    }

    @Override
    public void clear() {
        chatList.clear();
    }
}