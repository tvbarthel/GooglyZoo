package fr.tvbarthel.attempt.googlyzooapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fr.tvbarthel.attempt.googlyzooapp.R;
import fr.tvbarthel.attempt.googlyzooapp.model.GooglyPetEntry;

/**
 * Created by tbarthel on 18/02/14.
 */
public class GooglyPetEntryAdapter extends ArrayAdapter<GooglyPetEntry> {

    public GooglyPetEntryAdapter(Context context, ArrayList<GooglyPetEntry> petsAvailable) {
        super(context, R.layout.googly_drawer_entry, petsAvailable);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GooglyPetEntry petEntry = getItem(position);
        final Context context = getContext();
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = convertView;

        if (rowView == null) {
            rowView = inflater.inflate(R.layout.googly_drawer_entry, parent, false);
            //set pet name
            ((TextView) rowView.findViewById(R.id.googly_drawer_pet_name)).setText(
                    context.getResources().getString(petEntry.getName()));
        }

        //can't reuse old view since activated state could have changed
        final boolean isSelected = petEntry.isSelected();
        final int ic = isSelected ? petEntry.getColoredIcon() : petEntry.getBlackAndWhiteIcon();
        //set blackandwhite icon if not selected else colored one
        ((ImageView) rowView.findViewById(R.id.googly_drawer_pet_ic)).setImageDrawable(
                context.getResources().getDrawable(ic));


        return rowView;
    }

}
