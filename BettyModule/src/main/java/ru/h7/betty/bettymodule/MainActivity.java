package ru.h7.betty.bettymodule;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


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
//    private DialogFragment foodDialog, sportDialog;
//    private Date date = new Date();
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
//        foodDialog = new h7Dialog();
//        sportDialog = new h7Dialog();
//
//        TextView dateText = (TextView) findViewById(R.id.dateText);
////        date.set("2016.02.01");
//        dateText.setText(date.toString());
//
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), progress);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(mSectionsPagerAdapter);
//        viewPager.addOnPageChangeListener(new CircularViewPagerHandler(viewPager));
        viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
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
        Progress progress;

        public SectionsPagerAdapter(FragmentManager fm, Progress progress_) {
            super(fm);
            progress = progress_;
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(getCount() - position - 1);
        }

        @Override
        public int getCount() {
            return 10;
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {
        private Date date = new Date();
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

            ImageButton foodButton = (ImageButton) rootView.findViewById(R.id.foodButton);
            ImageButton sportButton = (ImageButton) rootView.findViewById(R.id.sportButton);
            foodButton.setOnClickListener(this);
            sportButton.setOnClickListener(this);

            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            int offset = getArguments().getInt(ARG_SECTION_NUMBER);
            date.shift(-offset);
            TextView dateText = (TextView) view.findViewById(R.id.dateText);
            dateText.setText(date.toString());

//            showMessage("PlaceholderFragment.onViewCreated " + date.toString());
        }

        private void showMessage(String msg) {
            Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.sportButton:
                    showMessage("sportButton");
//                    sportDialog.show(getActivity().getFragmentManager(), "sport");
                    break;
                case R.id.foodButton:
                    showMessage("foodButton");
//                    foodDialog.show(getActivity().getFragmentManager(), "food");
                    break;
                default:
                    throw new Error();
            }
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
    }

}
