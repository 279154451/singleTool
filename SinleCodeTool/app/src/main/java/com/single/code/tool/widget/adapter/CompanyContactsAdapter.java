package com.single.code.tool.widget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.github.promeg.pinyinhelper.Pinyin;
import com.single.code.tool.R;

import java.util.ArrayList;
import java.util.List;

/**分级显示列表
 * Created by XuPin on 2017/2/27.
 */
public class CompanyContactsAdapter extends BaseAdapter {

    private Context context;
    private List<CurriculumVitaeMan.CurriculumVitaeInfo> list;
    private List<CurriculumVitaeMan.CurriculumVitaeInfo> selectOptions = new ArrayList<>();
    private boolean canShowFirstLetter;

    public CompanyContactsAdapter(Context context, List<CurriculumVitaeMan.CurriculumVitaeInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CurriculumVitaeMan.CurriculumVitaeInfo curriculumVitaeInfo = (CurriculumVitaeMan.CurriculumVitaeInfo) getItem(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_company_contacts, null);
            viewHolder = new ViewHolder();
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_company_icon);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_company_user_name);
            viewHolder.tvNumber = (TextView) convertView.findViewById(R.id.tv_company_user_number);
            viewHolder.tvFirstLetter = (TextView) convertView.findViewById(R.id.tv_all_contacts_first_letters);
            viewHolder.firstLetterView = convertView.findViewById(R.id.view_contacts_first_letter);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        String firstLetter = Pinyin.toPinyin(curriculumVitaeInfo.getName(), "").substring(0, 1).toUpperCase();//获取首字母大写拼音
        String sortLetter = "";
        if (firstLetter.matches("[A-Z]")) {
            sortLetter = firstLetter;
        } else {
            sortLetter = "#";
        }
        int selection = sortLetter.charAt(0);
        if (position > 0) {
            CurriculumVitaeMan.CurriculumVitaeInfo frontContact = list.get(position - 1);
            String firstFrontLetter = Pinyin.toPinyin(frontContact.getName(), "").substring(0, 1).toUpperCase();
            String sortFrontLetter = "";
            if (firstFrontLetter.matches("[A-Z]")) {
                sortFrontLetter = firstFrontLetter;
            } else {
                sortFrontLetter = "#";
            }
            int frontSelection = sortFrontLetter.charAt(0);
            if (selection == frontSelection) {//判断当前首字母索引和前一条记录的首字母是否相同
                hideFirstLetter(viewHolder);
            } else {
                showFirstLetter(viewHolder, firstLetter);
            }
        } else {
            showFirstLetter(viewHolder, firstLetter);
        }
        viewHolder.tvName.setText(curriculumVitaeInfo.getName());
        viewHolder.tvNumber.setText(curriculumVitaeInfo.getNumber());
        if (position == 0) {
            convertView.setActivated(true);
        } else {
            convertView.setActivated(false);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvNumber;
        TextView tvFirstLetter;
        View firstLetterView;
    }

    private void showFirstLetter(ViewHolder viewHolder, String firstLetter) {
        viewHolder.tvFirstLetter.setVisibility(View.VISIBLE);
        viewHolder.firstLetterView.setVisibility(View.VISIBLE);
        if (firstLetter.matches("[A-Z]")) {
            viewHolder.tvFirstLetter.setText(firstLetter);
        } else {
            viewHolder.tvFirstLetter.setText("#");
        }
    }

    private void hideFirstLetter(ViewHolder viewHolder) {
        viewHolder.firstLetterView.setVisibility(View.GONE);
        viewHolder.tvFirstLetter.setVisibility(View.GONE);
    }


    public int getPositionForSelection(int selection) {
        for (int i = 0; i < list.size(); i++) {
            String sortStr = "";
            String fPinyin = Pinyin.toPinyin(list.get(i).getName(), "").substring(0, 1).toUpperCase();
            if (fPinyin.matches("[A-Z]")) {
                sortStr = fPinyin;
            } else {
                sortStr = "#";
            }
            char first = sortStr.toUpperCase().charAt(0);
            if (first == selection) {
                return i;
            }
        }
        return -1;
    }

    public List<CurriculumVitaeMan.CurriculumVitaeInfo> getSelectOptions() {
        return selectOptions;
    }

    public void setSelectOptions(List<CurriculumVitaeMan.CurriculumVitaeInfo> selectOptions) {
        this.selectOptions = selectOptions;
    }

    public boolean isCanShowFirstLetter() {
        return canShowFirstLetter;
    }

    public void setCanShowFirstLetter(boolean canShowFirstLetter) {
        this.canShowFirstLetter = canShowFirstLetter;
    }

    public List<CurriculumVitaeMan.CurriculumVitaeInfo> getList() {
        return list;
    }

    public void setList(List<CurriculumVitaeMan.CurriculumVitaeInfo> list) {
        this.list = list;
    }
}
