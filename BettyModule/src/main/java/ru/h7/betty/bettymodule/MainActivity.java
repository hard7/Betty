package ru.h7.betty.bettymodule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.SparseArray;
import android.view.*;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static android.os.Environment.getExternalStorageDirectory;


class Date {
    static public String DATE_FORMAT = "yyyy.MM.dd";
    static public String DATE_FORMAT_OUTPUT = "EEEE, dd MMMM yyyy";
    private Calendar calendar = Calendar.getInstance();
    private DateFormat date_fmt = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    private DateFormat date_fmt_out = new SimpleDateFormat(DATE_FORMAT_OUTPUT, Locale.US);

    @Override
    public final String toString() {
        return date_fmt.format(calendar.getTime());
    }

    public final String toStringOut() {
        return date_fmt_out.format(calendar.getTime());
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

    public int getDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH);
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
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

    ChartLoader chartLoader;
    ProgressTextManager progressTextManager;
    SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    public Progress getProgress() {
        return progress;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = new Progress(this);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), PAGE_COUNT);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);
//        viewPager.addOnPageChangeListener(new CircularViewPagerHandler(viewPager));
        viewPager.setCurrentItem(PAGE_COUNT - 1);
        progressTextManager = new ProgressTextManager(this);
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

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_some:
                makeSomeNoise();
                return true;
            case R.id.action_save_config:
                progressTextManager.save(progress.toString());
                return true;
            case R.id.action_load_config:
                progressTextManager.load(new ProgressTextHandler() {
                    @Override
                    public void progressTextHandle(String progressText) {
//                        progressTextManager.save(progress.toString());
                        progress.reinitialize(progressText);
                    }
                });
                return true;


            case R.id.action_unblock:
                Fragment page = sectionsPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
                ((PlaceholderFragment)page).unlockButtons();
                showMessage("Unlocked");

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void makeSomeNoise() {
        chartLoader = new ChartLoader((WebView) findViewById(R.id.webView), progress.makeProgressPrimeAccessor());
        chartLoader.handleBitmapResponse(new ChartLoader.BitmapHandler() {
            @Override
            public void handleBitmap(Bitmap bitmap) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Tittle");
                builder.setPositiveButton(" ", null);
                final AlertDialog dialog = builder.create();

                final Bitmap bitmap_ = bitmap;

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackground(
                                new BitmapDrawable(getResources(), bitmap_));
                    }
                });
                dialog.show();

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(getExternalStorageDirectory() + "/BettyProgress/Yeah.png");
                    if (fos != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();
                    }
                }
                catch( Exception e ) {

                }
            }
        });
    }

    static private void setupImages(final AlertDialog dialog) {
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundResource(R.mipmap.positive);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundResource(R.mipmap.negative);
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setBackgroundResource(R.mipmap.free_256);
            }
        });
    }

    static public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
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

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {
        private Date date = new Date();
//        private int colors[] = {Color.parseColor("#cccccc"), Color.RED, Color.GREEN, Color.YELLOW};
        private Map<Integer, Progress.State> buttonID2State = new TreeMap<Integer, Progress.State>();
        ButtonStateUpdater buttonStateUpdater = new ImageButtonStateUpdater();
        Progress.DayProgress dayProgress;
        Progress progress;
        ImageButton foodButton, sportButton;

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

        public int getOffset() {return offset;}

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
            this.offset = offset;
            date.shift(-offset);

            dayProgress = progress.getDayProgress(date.toString());
            TextView dateText = (TextView) view.findViewById(R.id.dateText);
            dateText.setText(date.toStringOut());

            foodButton = (ImageButton) view.findViewById(R.id.foodButton);
            sportButton = (ImageButton) view.findViewById(R.id.sportButton);

            buttonID2State.put(foodButton.getId(), dayProgress.getStateByCategory(Progress.Category.Food));
            buttonID2State.put(sportButton.getId(),  dayProgress.getStateByCategory(Progress.Category.Sport));

            foodButton.setOnClickListener(this);
            sportButton.setOnClickListener(this);

            updateButton(foodButton);
            updateButton(sportButton);

            if(offset >= 2 || buttonID2State.get(foodButton.getId()).getEstimate() != Progress.Estimate.Undefined) {
                foodButton.setEnabled(false);
            }

            if(offset >= 2 || buttonID2State.get(sportButton.getId()).getEstimate() != Progress.Estimate.Undefined) {
                sportButton.setEnabled(false);
            }
        }

        private void showMessage(String msg) {
            Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }

        public void unlockButtons() {
            foodButton.setEnabled(true);
            sportButton.setEnabled(true);
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
