package zladnrms.defytech.kim.BroadcastTv.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.adapter.contract.RoomListAdapterContract;
import zladnrms.defytech.kim.BroadcastTv.databinding.CardviewStreamingListBinding;
import zladnrms.defytech.kim.BroadcastTv.networking.CheckNetworkStatus;
import zladnrms.defytech.kim.BroadcastTv.view.ViewerActivity;
import zladnrms.defytech.kim.BroadcastTv.adapter.model.RoomListDataModel;
import zladnrms.defytech.kim.BroadcastTv.model.domain.RoomInfo;

public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.ViewHolder> implements RoomListDataModel, RoomListAdapterContract.View {

    private static final String thumbnailUrl = "http://52.79.108.8/thumbnail/";

    private ArrayList<RoomInfo> roomList = new ArrayList<RoomInfo>();
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardviewStreamingListBinding binding;

        public ViewHolder(View view) {
            super(view);

            binding = DataBindingUtil.bind(itemView);
        }
    }

    public RoomListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_streaming_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        RoomInfo roomInfo = roomList.get(position);
        holder.binding.setRoomdata(roomInfo);

        holder.itemView.setOnClickListener(v -> {
                    if (!CheckNetworkStatus.isConnectedToNetwork(context)) {
                        Toast.makeText(context, "인터넷 연결 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(context, ViewerActivity.class);
                        intent.putExtra("roomId", roomInfo.getRoomId());
                        intent.putExtra("subject", roomInfo.getSubject());
                        intent.putExtra("streamerId", roomInfo.getStreamerId());
                        intent.putExtra("streamerNickname", roomInfo.getStreamerNickname());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(intent);
                    }

                }
        );

        if (holder.binding.ivStreaminglistThumbnail != null) {

            Glide.with(context)
                    .load(thumbnailUrl + roomInfo.getStreamerId() + ".png")
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile))
                    .apply(RequestOptions.fitCenterTransform())
                    .apply(RequestOptions.errorOf(R.drawable.ic_profile))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(holder.binding.ivStreaminglistThumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    @Override
    public void add(RoomInfo roomInfo) {
        roomList.add(roomInfo);
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
        roomList.clear();
    }
}