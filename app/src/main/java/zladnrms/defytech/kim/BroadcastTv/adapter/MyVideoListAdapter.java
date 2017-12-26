package zladnrms.defytech.kim.BroadcastTv.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.adapter.contract.MyVideoListAdapterContract;
import zladnrms.defytech.kim.BroadcastTv.adapter.model.MyVideoListDataModel;
import zladnrms.defytech.kim.BroadcastTv.databinding.RecyclerviewMyVideoBinding;
import zladnrms.defytech.kim.BroadcastTv.model.domain.VideoInfo;

public class MyVideoListAdapter extends RecyclerView.Adapter<MyVideoListAdapter.ViewHolder> implements MyVideoListDataModel, MyVideoListAdapterContract.View {

    private ArrayList<VideoInfo> videoList = new ArrayList<VideoInfo>();
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerviewMyVideoBinding binding;

        public ViewHolder(View view) {
            super(view);

            binding = DataBindingUtil.bind(itemView);
        }
    }

    public MyVideoListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_my_video, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        VideoInfo videoInfo = videoList.get(position);

        holder.itemView.setOnClickListener(v -> {

                }
        );

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    @Override
    public void add(VideoInfo videoInfo) {
        videoList.add(videoInfo);
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
        videoList.clear();
    }
}