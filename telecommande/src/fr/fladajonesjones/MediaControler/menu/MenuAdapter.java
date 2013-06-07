package fr.fladajonesjones.MediaControler.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fr.fladajonesjones.MediaControler.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MenuAdapter extends BaseAdapter {

    private List<Object> mItems = new ArrayList<Object>();

    MenuAdapter(List<Object> items) {
        mItems.addAll(items);
    }

    public void addAll(Collection<? extends Object> objects) {
        mItems.addAll(objects);
    }

    public void clear() {
        mItems.clear();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position) instanceof Item ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position) instanceof Item;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

//    public View renduCategory(Category cateogory, View v, ViewGroup parent) {
//        if (v == null) {
//            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
//                    Context.LAYOUT_INFLATER_SERVICE);
//            v = inflater.inflate(R.layout.menu_row_category, parent, false);
//        }
//        ((TextView) v).setText(cateogory.mTitle);
//        return v;
//    }

    public View renduItem(Item item, View v, ViewGroup parent) {
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.menu_row_item, parent, false);
        }

        TextView tv = (TextView) v;
        tv.setText(((Item) item).mTitle);
        Drawable draw = tv.getResources().getDrawable(((Item) item).mIconRes);
        draw.setBounds(0, 0, 100, 100);

        tv.setCompoundDrawables(null, draw, null, null);
        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Object item = getItem(position);

        if (item instanceof Item) {
            v = renduItem((Item) item, v, parent);
        }

        v.setTag(R.id.mdActiveViewPosition, position);

        if (position == MenuDrawerUtil.mActivePosition) {
            MenuDrawerUtil.mMenuDrawer.setActiveView(v, position);
        }

        return v;
    }
}
