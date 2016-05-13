package sk.sustr.michal.mob_expense;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EXP";
    private EditText mExpense;
    private Spinner mCategory;
    private ListView mListView;
    private ArrayList<Item> mList;
    private MyListAdapter mListAdapter;
    private ArrayList<Item> history;
    private TextView mToday;
    private TextView mOverall;

    private float DAILY_MAX = 20.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCategory = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(adapter);

        mExpense = (EditText) findViewById(R.id.editText);
        mListView = (ListView) findViewById(R.id.listView);
        mList = new ArrayList<>();
        mListAdapter = new MyListAdapter(getApplicationContext(), mList);
        mListView.setAdapter(mListAdapter);

        mToday = (TextView) findViewById(R.id.today);
        mOverall = (TextView) findViewById(R.id.overall);
    }

    @Override
    protected void onPostResume() {
        super.onResume();
        refreshData();
    }

    public void addRecord(View view) {
        Date now = new Date();
        String data = now.getTime() + ";" + mExpense.getText() + ";" + mCategory.getSelectedItem() + "\n";

        try {
            File externalStorageDir = Environment.getExternalStorageDirectory();
            File f = new File(externalStorageDir , "expenses.csv");
            if(!f.exists()) f.createNewFile();

            FileOutputStream fOut = new FileOutputStream(f, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fOut);
            outputStreamWriter.append(data);
            outputStreamWriter.close();
            fOut.close();
            Toast.makeText(getApplicationContext(), "Record saved", Toast.LENGTH_SHORT).show();

            refreshData();
        } catch (Exception e) {
            Log.e("Exception", "Write failed " + e);
            Toast.makeText(getApplicationContext(), "Write failed", Toast.LENGTH_SHORT).show();
        }

    }

    public void refreshData() {
        mList.clear();
        history = getValues();
        calcStats();
        if(history.size() > 0) {
            mList.addAll(history.subList(0, Math.min(10, history.size()) ));
        }
        mListAdapter.notifyDataSetChanged();
    }

    public ArrayList<Item> getValues() {
        ArrayList<Item> values = new ArrayList<>();
        File externalStorageDir = Environment.getExternalStorageDirectory();
        File f = new File(externalStorageDir , "expenses.csv");
        if(!f.exists()) return values;

        try {
            List<String> list =new ArrayList<String>();
            FileInputStream in = new FileInputStream(externalStorageDir + "/expenses.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String tmp;
            while ((tmp = br.readLine()) != null){
                list.add(tmp);
            }

            for (int i=list.size()-1; i >= 0; i--) {
                String line = list.get(i);
                String[] parts = line.split(";");
                Item it = new Item(
                        new java.util.Date(Long.parseLong(parts[0])),
                        Float.parseFloat(parts[1]), parts[2]);
                values.add(it);
            }
        } catch (Exception e) {
            Log.e("Exception", "Read failed " + e);
            Toast.makeText(getApplicationContext(), "Read failed", Toast.LENGTH_SHORT).show();
        }
        return  values;
    }

    protected void calcStats() {
        if(history == null || history.size() < 1) return;
        Date first = history.get(history.size()-1).date;
        Date now = new Date();
        long milis1 = first.getTime();
        long milis2 = now.getTime();
        long diff = Math.abs(milis2 - milis1);
        int days = (int)(diff / (24 * 60 * 60 * 1000));
        Log.d(TAG, "calcStats "+days);
        double expenses = 0.0;
        double todayExpenses = 0.0;
        for(Item i : history) {
            expenses += i.amount;
            if(isSameDay(i.date, now)) {
                todayExpenses += i.amount;
            }
        }
        mOverall.setText(String.format("%.2f",(DAILY_MAX*days - expenses)));
        mToday.setText(String.format("%.2f",(DAILY_MAX - todayExpenses)));
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
}