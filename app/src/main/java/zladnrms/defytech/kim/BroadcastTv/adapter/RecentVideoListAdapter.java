package zladnrms.defytech.kim.BroadcastTv.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.adapter.contract.RecentVideoListAdapterContract;
import zladnrms.defytech.kim.BroadcastTv.adapter.model.RecentVideoListDataModel;
import zladnrms.defytech.kim.BroadcastTv.databinding.CardviewRecentVideoListBinding;
import zladnrms.defytech.kim.BroadcastTv.model.domain.VideoInfo;
import zladnrms.defytech.kim.BroadcastTv.view.VideoViewerActivity;

public class RecentVideoListAdapter extends RecyclerView.Adapter<RecentVideoListAdapter.ViewHolder> implements RecentVideoListDataModel, RecentVideoListAdapterContract.View {

    private static final String recentThumbnailUrl = "http://52.79.108.8/thumbnail/";

    private ArrayList<VideoInfo> videoList = new ArrayList<VideoInfo>();
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardviewRecentVideoListBinding binding;

        public ViewHolder(View view) {
            super(view);

            binding = DataBindingUtil.bind(itemView);
        }
    }

    public RecentVideoListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_recent_video_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        VideoInfo videoInfo = videoList.get(position);
        holder.binding.setVideodata(videoInfo);
        holder.binding.setNickname(videoInfo.getStreamerNickname());
        //holder.binding.setSubject(videoInfo.ge);
        holder.binding.setViewercount(videoInfo.getCount());

        String filename = videoInfo.getFilename();
        int videoId = videoInfo.getVideoId();
        String id = videoInfo.getStreamerId();
        String nickname = videoInfo.getStreamerNickname();
        int viewCount = videoInfo.getCount();

        holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, VideoViewerActivity.class);
                    intent.putExtra("videoId", videoId);
                    intent.putExtra("nickname", nickname);
                    intent.putExtra("filename", filename);
                    intent.putExtra("viewCount", viewCount);
                    context.startActivity(intent);
                }
        );

        if (holder.binding.ivRecentVideoThumbnail != null) {
            Glide.with(context)
                    .load(recentThumbnailUrl + filename + ".png")
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile))
                    .apply(RequestOptions.errorOf(R.drawable.ic_profile))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(holder.binding.ivRecentVideoThumbnail);
        }
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