package com.izaron.pepperpied;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class SearchableSimpleAdapter extends SimpleAdapter {

    ArrayList<HashMap<String, String>> arraList;
    ArrayList<HashMap<String, String>> arraList2;
    ArrayList<HashMap<String, String>> mOriginalValues; // Original Values
    LayoutInflater inflater;

    public SearchableSimpleAdapter(Context context,
                                   ArrayList<HashMap<String, String>> data, int resource, String[] from, int[] to) {

        super(context, data, resource, from, to);
        arraList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++)
            arraList.add(data.get(i));
        arraList2 = new ArrayList<>();
        for (int i = 0; i < data.size(); i++)
            arraList2.add(data.get(i));
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arraList.size();
    }

    @Override
    public Object getItem(int position) {
        //return position;
        return arraList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView textView1;
        TextView textView2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = inflater.inflate(android.R.layout.simple_list_item_2, null);
            holder.textView1 = (TextView) convertView.findViewById(android.R.id.text1);
            holder.textView2 = (TextView) convertView.findViewById(android.R.id.text2);
            holder.textView1.setTextSize(18);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //holder.textView.setText(arrayList.get(position));
        holder.textView1.setText(arraList2.get(position).get("Name"));
        holder.textView2.setText(Html.fromHtml(arraList2.get(position).get("Tel")));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                arraList = (ArrayList<HashMap<String, String>>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<HashMap<String, String>> FilteredArrList = new ArrayList<>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<HashMap<String, String>>(arraList); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;

                    FilteredArrList = mOriginalValues;
                    arraList2 = FilteredArrList;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        //String data = mOriginalValues.get(i);

                        String name = mOriginalValues.get(i).get("Name");
                        String tel = mOriginalValues.get(i).get("Tel");

                        if (name.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(mOriginalValues.get(i));
                        } else if (tel.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(mOriginalValues.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                    arraList2 = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }
}
