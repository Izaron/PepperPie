package com.izaron.pepperpied;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchableAdapter<T> extends ArrayAdapter<T> implements Filterable {

    private List<String> originalData;
    private List<String> lowerCaseData;
    private List<String> filteredData;
    private ItemFilter filter;

    @SuppressWarnings("unchecked")
    public SearchableAdapter(Context context, int resource, T[] objects) {
        super(context, resource, objects);
        filter = new ItemFilter();
        filteredData = (List<String>) Arrays.asList(objects);
        originalData = (List<String>) Arrays.asList(objects);

        lowerCaseData = new ArrayList<>();
        for (int i = 0; i < originalData.size(); i++)
            lowerCaseData.add(originalData.get(i).toLowerCase());
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getItem(int position) {
        return (T) filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Filter getFilter() {
        return filter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            ArrayList<String> nlist = new ArrayList<>();

            for (int i = 0; i < originalData.size(); i++) {
                String filterableString = lowerCaseData.get(i);
                if (isFilteredLowerCase(filterString, filterableString))
                    nlist.add(originalData.get(i));
            }

            FilterResults results = new FilterResults();
            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        private boolean isFilteredLowerCase(String filterString, String filterableString) {
            return filterableString.contains(filterString);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }
    }
}