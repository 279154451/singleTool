package com.single.code.tool.widget.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;



/**
 * NormalAdapter adapter = new NormalAdapter(data);
 NormalAdapterWrapper newAdapter = new NormalAdapterWrapper(adapter);
 View headerView = LayoutInflater.from(this).inflate(R.layout.item_header, mRecyclerView, false);
 View footerView = LayoutInflater.from(this).inflate(R.layout.item_footer, mRecyclerView, false);
 newAdapter.addFooterView(footerView);
 newAdapter.addHeaderView(headerView);
 mRecyclerView.setAdapter(newAdapter);
 * Created by czf on 2019/2/19.
 */

public class NormalAdapterWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public enum ITEM_TYPE{
        HEADER,
        FOOTER,
        NORMAL
    }

    private NormalAdapter mAdapter;
    private View mHeaderView;
    private View mFooterView;

    public NormalAdapterWrapper(NormalAdapter adapter){
        mAdapter = adapter;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return ITEM_TYPE.HEADER.ordinal();
        } else if(position == mAdapter.getItemCount() + 1){
            return ITEM_TYPE.FOOTER.ordinal();
        } else{
            return ITEM_TYPE.NORMAL.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount() + 2;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if(viewType == ITEM_TYPE.HEADER.ordinal() || viewType == ITEM_TYPE.FOOTER.ordinal()){
            return;
        } else{
            mAdapter.onBindViewHolder(((NormalAdapter.VH)holder), position - 1);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ITEM_TYPE.HEADER.ordinal()){
            return new RecyclerView.ViewHolder(mHeaderView) {};
        } else if(viewType == ITEM_TYPE.FOOTER.ordinal()){
            return new RecyclerView.ViewHolder(mFooterView) {};
        } else{
            return mAdapter.onCreateViewHolder(parent,viewType);
        }
    }

    /**
     * 设置header
     * @param view
     */
    public void addHeaderView(View view){
        this.mHeaderView = view;
    }

    /**
     * 设置footer
     * @param view
     */
    public void addFooterView(View view){
        this.mFooterView = view;
    }
}
