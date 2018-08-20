package com.rsons.application.connecting_thoughts.NavigationDrawer;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rsons.application.connecting_thoughts.Const;
import com.rsons.application.connecting_thoughts.R;

import java.util.List;
import java.util.Map;

/**
 * Created by ankit on 9/15/2017.
 */

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> mExpandableListTitle;
    private Map<String, List<String>> mExpandableListDetail;
    private LayoutInflater mLayoutInflater;

    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                       Map<String, List<String>> expandableListDetail) {
        mContext = context;
        mExpandableListTitle = expandableListTitle;
        mExpandableListDetail = expandableListDetail;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return mExpandableListDetail.get(mExpandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.nav_list_item, null);
        }
        Log.d(" expandedListText ", expandedListText);

        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.expandedListItem);
        if(expandedListText!=null && expandedListText.length()!=0 && !expandedListText.equalsIgnoreCase(null)){

            /**Yahan hum sub list daal rahe hain
             * yahna par do string hain first strin key hai and second is value
             * first key string agar generId array jo ki const class me define hai
             * wahan pe present hai to uski key yahan jo di jayegi wo print ho jayegi*/

            Const.GenresMap.put("children", "Children");
            Const.GenresMap.put("comedy", "Cmdy");
            Const.GenresMap.put("drama", "Crime");

            Const.GenresMap.put("childr", "New Children");
            Const.GenresMap.put("come", "New Cmdy");
            Const.GenresMap.put("dra", "New Crime");

            expandedListTextView.setText(Const.GenresMap.get(expandedListText));
//            // if languages
//            if(listPosition==0) {
//                expandedListTextView.setText(Const.LanguagesMap.get(expandedListText));
//            }else{ // if genres
//                expandedListTextView.setText(Const.GenresMap.get(expandedListText));
//            }
        }else{
            expandedListTextView.setText("select your preferences in Settings page");
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return mExpandableListDetail.get(mExpandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return mExpandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return mExpandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    //    int icons[]={R.drawable.ic_white_language_36dp,R.drawable.ic_white_genre_special_36dp,R.drawable.ic_white_rating_36dp,
//    R.drawable.ic_white_share_24dp,R.drawable.ic_white_info_filled_24dp,R.drawable.ic_white_info_filled_24dp
//    };
    int icons[]={R.drawable.ic_global,
            R.drawable.ic_add_male_user,
            R.drawable.ic_bookshelf,
            R.drawable.ic_history,
            /*R.drawable.ic_settings,*/
            R.drawable.ic_partnership,
            R.drawable.ic_help,
            R.drawable.ic_group,
            R.drawable.ic_share,
            R.drawable.ic_rate_star_button,

    };
    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.nav_list_group, null);
        }
        TextView groupTitle = (TextView) convertView.findViewById(R.id.listTitle);
        ImageView img = (ImageView) convertView.findViewById(R.id.grp_status);
        ImageView icon = (ImageView) convertView.findViewById(R.id.grp_icon);
        View line= convertView.findViewById(R.id.peripherals_separator);
        icon.setImageResource(icons[listPosition]);
        //icon.setColorFilter(ContextCompat.getColor(mContext, R.color.grey_400), PorterDuff.Mode.SRC_IN);
        groupTitle.setText(listTitle);
        if (isExpanded) {
            img.setImageResource(R.drawable.ic_up_arrow);
        } else {
            img.setImageResource(R.drawable.ic_angle_arrow_down);
        }
        img.setColorFilter(ContextCompat.getColor(mContext, R.color.grey_400), PorterDuff.Mode.SRC_IN);

        /*
        * to make sure only genre and language are dropdowns
        * and reduce non-list item sizes
        */

        /**Yahan apne hisab se jis navigation menu item ki expandable icon set kar rahe hain */

        if(listPosition==0){
            img.setVisibility(View.VISIBLE);
            groupTitle.setTextSize(14);
        }else{
            groupTitle.setTextSize(14);
            img.setVisibility(View.GONE);
        }
        if(listPosition==1||listPosition==2||listPosition==4 ||listPosition==7|| listPosition==9){
            line.setVisibility(View.VISIBLE);
        }else{
            line.setVisibility(View.GONE);
        }
        return convertView;
    }




    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
