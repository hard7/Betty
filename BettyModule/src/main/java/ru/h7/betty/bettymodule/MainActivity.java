package ru.h7.betty.bettymodule;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;


class Date {
    static public String DATE_FORMAT = "yyyy.MM.dd";
//    static public String DATE_FORMAT_OUTPUT = "dd.MM.yyyy";
    private Calendar calendar = Calendar.getInstance();
    private DateFormat date_fmt = new SimpleDateFormat(DATE_FORMAT, Locale.US);

    @Override
    public final String toString() {
        return date_fmt.format(calendar.getTime());
    }

    public void next() {
        shift(1);
    }

    public void prev() {
        shift(-1);
    }

    public final void set(String dateFmt) {
        try {
            java.util.Date date = date_fmt.parse(dateFmt);
            calendar.setTime(date);
        }
        catch (ParseException e) {
            System.out.println("Parse Exception: " + e);
        }
    }

    public void shift(int offsetDays) {
        calendar.add(Calendar.DATE, offsetDays);
    }
}



interface ProgressGetter {
    Progress getProgress();
}

public class MainActivity extends FragmentActivity implements ProgressGetter {
    private int PAGE_COUNT = 10;
    private Progress progress;
    ViewPager viewPager;

    @Override
    public Progress getProgress() {
        return progress;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = new Progress(this);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), PAGE_COUNT);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(mSectionsPagerAdapter);
//        viewPager.addOnPageChangeListener(new CircularViewPagerHandler(viewPager));
        viewPager.setCurrentItem(PAGE_COUNT - 1);

//        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
//        String progressStr = sharedPref.getString("PROGRESS_KEY", "");
//        showMessage("PREF: " + progressStr);
    }

    @Override
    protected void onPause() {
        super.onPause();
        progress.save();
    }

    private void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        private int PAGE_COUNT;

        public SectionsPagerAdapter(FragmentManager fm, int PAGE_COUNT_) {
            super(fm);
            PAGE_COUNT = PAGE_COUNT_;
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(PAGE_COUNT - position - 1);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {
        private Date date = new Date();
//        private int colors[] = {Color.parseColor("#cccccc"), Color.RED, Color.GREEN, Color.YELLOW};
        private Map<Integer, Progress.State> buttonID2State = new TreeMap<Integer, Progress.State>();
        ButtonStateUpdater buttonStateUpdater = new BackgroundButtonStateUpdater();
        Progress.DayProgress dayProgress;
        Progress progress;

        private static final String ARG_SECTION_NUMBER = "section_number";
        private int offset = 0;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            int offset = getArguments().getInt(ARG_SECTION_NUMBER);
            date.shift(-offset);

            dayProgress = progress.getDayProgress(date.toString());
            TextView dateText = (TextView) view.findViewById(R.id.dateText);
            dateText.setText(date.toString());

            ImageButton foodButton = (ImageButton) view.findViewById(R.id.foodButton);
            ImageButton sportButton = (ImageButton) view.findViewById(R.id.sportButton);

            buttonID2State.put(foodButton.getId(), dayProgress.getStateByCategory(Progress.Category.Food));
            buttonID2State.put(sportButton.getId(),  dayProgress.getStateByCategory(Progress.Category.Sport));

            foodButton.setOnClickListener(this);
            sportButton.setOnClickListener(this);

            updateButton(foodButton);
            updateButton(sportButton);
        }

        private void showMessage(String msg) {
            Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            try {
                // Instantiate the NoticeDialogListener so we can send events to the host
                ProgressGetter progressGetter = (ProgressGetter) activity;
                progress = progressGetter.getProgress();
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(activity.toString() + " must implement ProgressGetter");
            }
        }

        @Override
        public void onClick(View v) {
            switchToNextEstimate((ImageButton) v);
            updateButton((ImageButton) v);

        }

        private void switchToNextEstimate(ImageButton button) {
            Progress.State state = buttonID2State.get(button.getId());
            state.switchToNextEstimate();
        }

        private void updateButton(ImageButton button) {
            buttonStateUpdater.update(button, buttonID2State.get(button.getId()));
        }
    }

}
