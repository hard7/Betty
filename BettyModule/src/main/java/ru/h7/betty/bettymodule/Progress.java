package ru.h7.betty.bettymodule;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import java.util.*;

class Progress {

    public enum Category { Food, Sport; }
    public enum Estimate { Undefined, Bad, Good, Planned; }
    static public class State {
        private Category category;
        private Estimate estimate;
        private static final Estimate[] estimateValues = Estimate.values();
        private static final Category[] categoryValues = Category.values();

        public State(Category category_, Estimate estimate_) {
            category = category_;
            estimate = estimate_;
        }
        public State(Category category) {
            this(category, Estimate.Undefined);
        }

        public void switchToNextEstimate() {
            estimate = estimateValues[(estimate.ordinal() + 1) % estimateValues.length];
        }

        public Estimate getEstimate() {
            return estimate;
        }

        public Category getCategory() {
            return category;
        }

        public char estimateToChar() {
            return estimate.name().charAt(0);
        }

        static public Estimate getEstimateByChar(char c) {
            switch (c) {
                case 'u': case 'U': return Estimate.Undefined;
                case 'b': case 'B': return Estimate.Bad;
                case 'g': case 'G': return Estimate.Good;
                case 'p': case 'P': return Estimate.Planned;
                default: throw new Error();
            }
        }
    }

    ProgressPrimeAccessor makeProgressPrimeAccessor() {
        final int count = progressMap.size();
        System.out.println(">>>> " + count);
        final Vector<Integer> days = new Vector<Integer>();
        final Vector<Integer> months = new Vector<Integer>();
        final Vector<Integer> years = new Vector<Integer>();
        final Vector<Integer> foodEstimate = new Vector<Integer>();
        final Vector<Integer> sportEstimate = new Vector<Integer>();
        Date date = new Date();
        for( Map.Entry<String, DayProgress> entry : progressMap.entrySet() ) {
            date.set(entry.getKey());
            DayProgress dp = entry.getValue();
            days.add(date.getDay());
            months.add(date.getMonth());
            years.add(date.getYear());
            switch (dp.food.getEstimate()) {
                case Bad: foodEstimate.add(-3); break;
                case Planned: foodEstimate.add(-1); break;
                case Undefined: foodEstimate.add(0); break;
                case Good: foodEstimate.add(3); break;
            }

            switch (dp.sport.getEstimate()) {
                case Bad: sportEstimate.add(-3); break;
                case Planned: sportEstimate.add(-1); break;
                case Undefined: sportEstimate.add(0); break;
                case Good: sportEstimate.add(3); break;
            }
        }

        return new ProgressPrimeAccessor() {
            @Override
            public int getCount() {
                return count;
            }

            @Override
            public int getDay(int index) {
                return days.get(index);
            }

            @Override
            public int getMonth(int index) {
                return months.get(index);
            }

            @Override
            public int getYear(int index) {
                return years.get(index);
            }

            @Override
            public int getFoodEstimate(int index) {
                return foodEstimate.get(index);
            }

            @Override
            public int getSportEstimate(int index) {
                return sportEstimate.get(index);
            }
        };
    }

    public class DayProgress {

        private State food, sport;
        public DayProgress(char foodChar, char sportChar) {
            food = new State(Category.Food, State.getEstimateByChar(foodChar));
            sport = new State(Category.Sport, State.getEstimateByChar(sportChar));
        }

        public State getStateByCategory(Category category) {
            State result = null;
            switch(category) {
            case Food: result = food; break;
            case Sport: result = sport; break;
            }
            return result;
        }

        public DayProgress() {
            this('U', 'U');
        }

        public void switchToNextFoodState() {
            food.switchToNextEstimate();
        }

        public void switchToNextSportState() {
            sport.switchToNextEstimate();
        }
    }

    static private String PROGRESS_KEY = "PROGRESS_KEY";
    private SharedPreferences sharedPref;
    private Map<String, DayProgress> progressMap = new TreeMap<String, DayProgress>();


    public Progress(Activity activity) {
        sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        String progressStr = sharedPref.getString(PROGRESS_KEY, "");
        if(!sharedPref.contains(PROGRESS_KEY)) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(PROGRESS_KEY, progressStr);
            editor.apply();
        }
        parse(progressStr);
    }

    public void reinitialize(String progressText) {
        parse(progressText);
        save();
    }

    private void fillRandomData(int N) {
        final Random random = new Random();
        String states = "UGBP";
        Date date = new Date();
        date.shift(-2 * N);
        while(N > 0) {
            date.next();
            if(random.nextInt(2) % 2 == 0) continue;
            DayProgress p = new DayProgress(states.charAt(random.nextInt(3)), states.charAt(random.nextInt(3)));
            progressMap.put(date.toString(), p);
            N--;
        }
    }

    public DayProgress getDayProgress(String dayKey) {
        if(!progressMap.containsKey(dayKey)) {
            progressMap.put(dayKey, new DayProgress());
        }
        return progressMap.get(dayKey);
    }

    @Override
    public String toString() {
        String result = "";
        for( Map.Entry<String, DayProgress> entry : progressMap.entrySet() ) {
            DayProgress dp = entry.getValue();
            result += entry.getKey() + " " + dp.food.estimateToChar() + dp.sport.estimateToChar() + "\n";
        }
        return result;
    }

    public void save() {
        savePreference();
    }

    private void savePreference() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PROGRESS_KEY, toString());
        editor.apply();
    }

    private void parse(String str) {
        progressMap.clear();
        String lines[] = str.split("[\\r?\\n]");
        for(String line : lines) {
            if(line.trim().length() == 0) continue;
            String comp[] = line.split("\\s+", 2);
            progressMap.put(comp[0], new DayProgress(comp[1].charAt(0), comp[1].charAt(1)));
        }
    }
}