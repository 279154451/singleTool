package com.single.code.tool.widget.treelist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.List;

/**
 * 关于TreeView 参考博客
 *
 * @param <T>
 */
public class SimpleTreeAdapter<T> extends TreeListViewAdapter<T> {
    private Context context;
    private static final String TAG = "SimpleTreeAdapter";

    public SimpleTreeAdapter(ListView mTree, Context context, List<T> datas,
                             int defaultExpandLevel) throws IllegalArgumentException,
            IllegalAccessException {
        super(mTree, context, datas, defaultExpandLevel);
        this.context = context;
    }

    @Override
    public View getConvertView(final Node node, final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            //TODO 自定义item布局
//            convertView = mInflater.inflate(R.layout.tree_list_item, parent, false);
            viewHolder = new ViewHolder();
////            viewHolder.icon = (ImageView) convertView.findViewById(R.id.id_treenode_icon);
////            viewHolder.head_icon = (ImageView) convertView.findViewById(R.id.cb_treenode_head_view);
////            viewHolder.label = (TextView) convertView.findViewById(R.id.id_treenode_label);
////            viewHolder.number = (TextView) convertView.findViewById(R.id.id_treenode_number);
//            viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_tree_node);
//            viewHolder.tvGroupName = (TextView) convertView.findViewById(R.id.tv_node_group_name);
//            viewHolder.head_icon = (ImageView) convertView.findViewById(R.id.iv_company_icon);
//            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_company_user_name);
//            viewHolder.tvNumber = (TextView) convertView.findViewById(R.id.tv_company_user_number);
//            viewHolder.rlGroup = (RelativeLayout) convertView.findViewById(R.id.rl_tree_node);
//            viewHolder.rlUser = (RelativeLayout) convertView.findViewById(R.id.rl_tree_list_data);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        //判断显示“箭头”图标or头像
        if (node.getIcon() == -1) {
            viewHolder.rlGroup.setVisibility(View.GONE);
            viewHolder.rlUser.setVisibility(View.VISIBLE);
//            viewHolder.icon.setVisibility(View.INVISIBLE);
//            viewHolder.head_icon.setVisibility(View.VISIBLE);
//            viewHolder.number.setVisibility(View.VISIBLE);
        } else {
            viewHolder.rlGroup.setVisibility(View.VISIBLE);
            viewHolder.rlUser.setVisibility(View.GONE);
            viewHolder.icon.setImageResource(node.getIcon());
//            viewHolder.icon.setVisibility(View.VISIBLE);
//            viewHolder.number.setVisibility(View.GONE);
//            viewHolder.head_icon.setVisibility(View.GONE);
//            viewHolder.icon.setImageResource(node.getIcon());
        }


        viewHolder.tvGroupName.setText(node.getName());
        viewHolder.tvName.setText(node.getName());
        viewHolder.tvNumber.setText(node.getNumber());
        //显示名称
//        viewHolder.label.setText(node.getName());
//        viewHolder.number.setText(node.getNumber());
        return convertView;
    }

    private final class ViewHolder {
        ImageView icon;
        ImageView head_icon;
        TextView label;
        TextView number;
        TextView tvGroupName;
        TextView tvName;
        TextView tvNumber;
        RelativeLayout rlGroup;
        RelativeLayout rlUser;
    }
}
