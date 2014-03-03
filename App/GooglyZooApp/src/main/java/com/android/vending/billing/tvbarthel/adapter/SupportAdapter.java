package com.android.vending.billing.tvbarthel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.vending.billing.SkuDetails;

import java.util.List;

import fr.tvbarthel.attempt.googlyzooapp.R;

/**
 * Created by tbarthel on 02/03/14.
 */
public class SupportAdapter extends ArrayAdapter<SkuDetails> {

    /**
     * create an adapter for support purchase list
     *
     * @param context
     * @param objects
     */
    public SupportAdapter(Context context, List<SkuDetails> objects) {
        super(context, R.layout.support_entry, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        final SkuDetails currentSku = getItem(position);
        if (rowView == null) {
            final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.support_entry, parent, false);
        }

        ((TextView) rowView.findViewById(R.id.support_entry_title)).setText(currentSku.getTitle());
        ((TextView) rowView.findViewById(R.id.support_entry_description)).setText(currentSku.getDescription());
        ((TextView) rowView.findViewById(R.id.support_entry_price)).setText(currentSku.getPrice());

        return rowView;
    }
}
