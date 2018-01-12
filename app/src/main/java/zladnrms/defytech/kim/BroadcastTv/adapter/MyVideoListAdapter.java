package zladnrms.defytech.kim.BroadcastTv.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.adapter.contract.MyVideoListAdapterContract;
import zladnrms.defytech.kim.BroadcastTv.adapter.model.MyVideoListDataModel;
import zladnrms.defytech.kim.BroadcastTv.adapter.presenter.MyVideoListAdapterPresenter;
import zladnrms.defytech.kim.BroadcastTv.databinding.DialogAdjustSubjectBinding;
import zladnrms.defytech.kim.BroadcastTv.databinding.RecyclerviewMyVideoBinding;
import zladnrms.defytech.kim.BroadcastTv.model.domain.VideoInfo;
import zladnrms.defytech.kim.BroadcastTv.view.VideoViewerActivity;

public class MyVideoListAdapter extends RecyclerView.Adapter<MyVideoListAdapter.ViewHolder> implements MyVideoListDataModel, MyVideoListAdapterContract.View {

    private static final String recentThumbnailUrl = "http://52.79.108.8/thumbnail/";

    private ArrayList<VideoInfo> videoList = new ArrayList<VideoInfo>();
    private Context context;
    private MyVideoListAdapterPresenter presenter;

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerviewMyVideoBinding binding;

        public ViewHolder(View view) {
            super(view);

            binding = DataBindingUtil.bind(itemView);
        }
    }

    public MyVideoListAdapter(Context context) {
        this.context = context;
        presenter = new MyVideoListAdapterPresenter();
        presenter.attachView(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_my_video, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        VideoInfo videoInfo = videoList.get(position);
        holder.binding.setVideodata(videoInfo);

        String filename = videoInfo.getFilename();
        String subject = videoInfo.getSubject();
        int videoId = videoInfo.getVideoId();
        String id = videoInfo.getStreamerId();
        String nickname = videoInfo.getStreamerNickname();
        int viewCount = videoInfo.getCount();

        holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, VideoViewerActivity.class);
                    intent.putExtra("videoId", videoId);
                    intent.putExtra("nickname", nickname);
                    intent.putExtra("subject", subject);
                    intent.putExtra("filename", filename);
                    intent.putExtra("viewCount", viewCount);
                    context.startActivity(intent);
                }
        );

        if (holder.binding.ivMyVideoThumbnail != null) {
            Glide.with(context)
                    .load(recentThumbnailUrl + filename + ".png")
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile))
                    .apply(RequestOptions.errorOf(R.drawable.ic_profile))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(holder.binding.ivMyVideoThumbnail);
        }

        holder.binding.btnAdjust.setOnClickListener(v -> {
            DialogAdjustSubjectBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_adjust_subject, null, false);
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(binding.getRoot());
            binding.btnSubmit.setOnClickListener(view->{
                dialog.dismiss();
                String adjSubject = binding.etSubject.getText().toString();
                if(adjSubject.trim().equals("")) {
                    Toast.makeText(context, "빈 제목은 허용되지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    presenter.adjust(context, videoId, adjSubject);
                    clear();
                    presenter.refresh(context);
                }
            });
            binding.btnCancel.setOnClickListener(view->{
                dialog.dismiss();
            });
            dialog.show();
        });

        holder.binding.btnDelete.setOnClickListener(v -> {
            // 즐겨찾기 삭제
            presenter.delete(context, videoId, filename);
            remove(position);
        });
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
        videoList.remove(position);
    }

    @Override
    public void clear() {
        videoList.clear();
    }

    @Override
    public void getList(ArrayList<VideoInfo> video) {
        for(int i = 0; i < video.size(); i ++) {
            VideoInfo videoInfo = video.get(i);
            add(videoInfo);
            refresh();
        }
    }

}