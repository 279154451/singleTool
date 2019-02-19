package com.single.code.tool.widget.view;

import android.support.v7.widget.RecyclerView;

/**
 * 这里给出ListView实现局部更新的方法：
 public void updateItemView(ListView listview, int position, Data data){
 int firstPos = listview.getFirstVisiblePosition();
 int lastPos = listview.getLastVisiblePosition();
 if(position >= firstPos && position <= lastPos){  //可见才更新，不可见则在getView()时更新
 //listview.getChildAt(i)获得的是当前可见的第i个item的view
 View view = listview.getChildAt(position - firstPos);
 VH vh = (VH)view.getTag();
 vh.text.setText(data.text);
 }
 }
 * Created by czf on 2019/2/19.
 */

public class SgRecyclerView {
}
