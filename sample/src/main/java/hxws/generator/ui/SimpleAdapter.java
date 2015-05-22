package hxws.generator.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import hxws.generator.R;

public class SimpleAdapter extends BaseAdapter {

    private static final String[] CONTENTS =
            "Generator test very simple".split(" ");

    private final LayoutInflater inflater;

    public SimpleAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override public int getCount() {
        return CONTENTS.length;
    }

    @Override public String getItem(int position) {
        return CONTENTS[position];
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.keyWord = (TextView)view.findViewById(R.id.item);
            view.setTag(holder);
        }
        String word = getItem(position);
        holder.keyWord.setText(word);
        return view;
    }

    static class ViewHolder {
        TextView keyWord;
    }
}
