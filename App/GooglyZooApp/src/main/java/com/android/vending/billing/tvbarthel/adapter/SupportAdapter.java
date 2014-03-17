package com.android.vending.billing.tvbarthel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.vending.billing.SkuDetails;
import com.android.vending.billing.tvbarthel.model.CoffeeEntry;

import java.util.List;

import fr.tvbarthel.attempt.googlyzooapp.R;

/**
 * Created by tbarthel on 02/03/14.
 */
public class SupportAdapter extends ArrayAdapter<CoffeeEntry> {

    /**
     * create an adapter for support purchase list
     *
     * @param context
     * @param objects
     */
    public SupportAdapter(Context context, List<CoffeeEntry> objects) {
        super(context, R.layout.support_entry, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        final CoffeeEntry currentEntry = getItem(position);
        final SkuDetails currentSku = currentEntry.getSkuDetails();
        if (rowView == null) {
            final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.support_entry, parent, false);
        }

        final String title = currentSku.getTitle();
        ((TextView) rowView.findViewById(R.id.support_entry_title)).setText(
                title.substring(0, title.indexOf("(")));
        ((TextView) rowView.findViewById(R.id.support_entry_description)).setText(
                currentSku.getDescription());
        final String price = currentSku.getPrice();
        final String amount = price.substring(0, price.length() - 1);
        final String currency = price.substring(price.length() - 1, price.length());
        ((TextView) rowView.findViewById(R.id.support_entry_price)).setText(amount);
        ((TextView) rowView.findViewById(R.id.support_entry_currency)).setText(currency);

        //set bar value
        ((ProgressBar) rowView.findViewById(R.id.support_detail_coffee_bar))
                .setProgress(currentEntry.getCaffeineRate());
        ((TextView) rowView.findViewById(R.id.support_detail_coffee_value))
                .setText(getContext().getString(R.string.support_bar_text,
                        currentEntry.getCaffeineRate()));
        ((ProgressBar) rowView.findViewById(R.id.support_detail_energy_bar))
                .setProgress(currentEntry.getEnergyRate());
        ((TextView) rowView.findViewById(R.id.support_detail_energy_value))
                .setText(getContext().getString(R.string.support_bar_text,
                        currentEntry.getEnergyRate()));
        ((ProgressBar) rowView.findViewById(R.id.support_detail_candy_bar))
                .setProgress(currentEntry.getCandyRate());
        ((TextView) rowView.findViewById(R.id.support_detail_candy_value))
                .setText(getContext().getString(R.string.support_bar_text,
                        currentEntry.getCandyRate()));

        return rowView;
    }
}
