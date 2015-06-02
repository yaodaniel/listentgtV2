package com.example.android.listentgt;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DanielY on 5/31/2015.
 */
public class SettingsAdapter extends BaseAdapter {

    private ArrayList<String> categories;
    private ArrayList<String> subCategories;
    private LayoutInflater settingInf;
    private Fragment parentFrag;

    public SettingsAdapter(Fragment obj, Context c, ArrayList<String> categories, ArrayList<String> subCategories) {
        this.categories = categories;
        this.subCategories = subCategories;
        settingInf = LayoutInflater.from(c);
        parentFrag = obj;
    }
    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to setting_elements layout
        LinearLayout settingLay = (LinearLayout)settingInf.inflate(R.layout.setting_elements, parent, false);
        settingLay.setOnClickListener((FragmentSettingsPage)parentFrag);
        //Initiate the TextViews for a settings element
        TextView settingHeader = (TextView)settingLay.findViewById(R.id.settingsElementHeader);
        TextView settingSub = (TextView)settingLay.findViewById(R.id.settingsElementSub);

        //Create each list element
        settingHeader.setText(categories.get(position));
        settingSub.setText(subCategories.get(position));
        //set position as tag
        settingLay.setTag(position);
        return settingLay;
    }
}
