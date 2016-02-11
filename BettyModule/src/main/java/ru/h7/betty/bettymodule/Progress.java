package ru.h7.betty.bettymodule;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

import static android.os.Environment.getExternalStorageDirectory;

class Progress {
    public enum State {Undefined, Bad, Good, Planned};

    public class DayProgress {

        public State food, sport;
        public DayProgress(char foodChar, char sportChar) {
            food = charToState(foodChar);
            sport = charToState(sportChar);
        }

        public DayProgress() {
            this('U', 'U');
        }

        public State charToState(char stateChar) {
            switch (stateChar) {
                case 'u': case 'U': return State.Undefined;
                case 'b': case 'B': return State.Bad;
                case 'g': case 'G': return State.Good;
                case 'p': case 'P': return State.Planned;
                default: throw new Error();
            }
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

//        if(progressMap.isEmpty()) {
//            fillRandomData(700);
//            savePreference();

//            FileOutputStream out = new FileOutputStream(getExternalStorageDirectory() + "/yahoo.png");
//            outputStreamWriter.write(data);
//            outputStreamWriter.close();
//        }
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
            result += entry.getKey() + " " + dp.food.name().charAt(0) + dp.sport.name().charAt(0) + "\n";
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
        String lines[] = str.split("[\\r?\\n]");
        for(String line : lines) {
            if(line.isEmpty()) continue;
            String comp[] = line.split("\\s+", 2);
            progressMap.put(comp[0], new DayProgress(comp[1].charAt(0), comp[1].charAt(1)));
        }
    }

}