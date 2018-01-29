package zladnrms.defytech.kim.BroadcastTv.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import zladnrms.defytech.kim.BroadcastTv.BuildConfig;
import zladnrms.defytech.kim.BroadcastTv.R;
import zladnrms.defytech.kim.BroadcastTv.adapter.contract.MyBookmarkListAdapterContract;
import zladnrms.defytech.kim.BroadcastTv.adapter.model.MyBookmarkListDataModel;
import zladnrms.defytech.kim.BroadcastTv.adapter.presenter.MyBookmarkListAdapterPresenter;
import zladnrms.defytech.kim.BroadcastTv.databinding.RecyclerviewMyBookmarkBinding;

public class MyBookmarkListAdapter extends RecyclerView.Adapter<MyBookmarkListAdapter.ViewHolder> implements MyBookmarkListDataModel, MyBookmarkListAdapterContract.View {

    private ArrayList<String> bookmarkList = new ArrayList<String>();
    private Context context;
    private MyBookmarkListAdapterPresenter presenter;

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerviewMyBookmarkBinding binding;

        public ViewHolder(View view) {
            super(view);

            binding = DataBindingUtil.bind(itemView);
        }
    }

    public MyBookmarkListAdapter(Context context) {
        this.context = context;
        presenter = new MyBookmarkListAdapterPresenter();
        presenter.attachView(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_streaming_list, parent, false);
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_my_bookmark, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        String nickname = bookmarkList.get(position);
        holder.binding.setNickname(nickname);

        holder.binding.btnDelete.setOnClickListener(v -> {
            // 즐겨찾기 삭제
            presenter.delete(context, nickname);
            remove(position);
        });

    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    @Override
    public void add(String nickname) {
        bookmarkList.add(nickname);
    }

    @Override
    public void refresh() {
        notifyDataSetChanged();
    }

    @Override
    public void remove(int position) {
        bookmarkList.remove(position);
    }

    @Override
    public void clear() {
        bookmarkList.clear();
    }
}