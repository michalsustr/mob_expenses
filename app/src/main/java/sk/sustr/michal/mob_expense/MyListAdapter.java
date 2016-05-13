package sk.sustr.michal.mob_expense;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by Michal Sustr [michal.sustr@gmail.com] on 3/2/16.
 */
public class MyListAdapter extends ArrayAdapter<Item> {

    private final Context context;
    private final List<Item> values;

    public MyListAdapter(Context context, List<Item> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(values.get(position) == null)  {
            return null;
        }
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);
        TextView date = (TextView) rowView.findViewById(R.id.date);
        TextView category = (TextView) rowView.findViewById(R.id.category);
        TextView amount = (TextView) rowView.findViewById(R.id.amount);

        date.setText(
                DateFormat.getDateInstance().format(values.get(position).date)
        );
        category.setText(values.get(position).category);
        amount.setText(String.format("%.2f", values.get(position).amount));

        return rowView;
    }
}
