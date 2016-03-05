package ru.h7.betty.bettymodule;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.ImageButton;

interface ButtonStateUpdater {
    void update(ImageButton imageButton, Progress.State state);
}

class BackgroundButtonStateUpdater implements ButtonStateUpdater {

    @Override
    public void update(ImageButton imageButton, Progress.State state) {
        imageButton.setBackgroundColor(getColorByEstimate(state.getEstimate()));
    }

    private int getColorByEstimate(Progress.Estimate estimate) {
        switch (estimate) {
            case Undefined: return Color.parseColor("#cccccc");
            case Bad: return Color.RED;
            case Good: return Color.GREEN;
            case Planned: return Color.YELLOW;
        }
        return Color.BLACK;
    }
}

class ImageButtonStateUpdater implements ButtonStateUpdater {
    static private String URL = "file:///android_res/square/food_good.png";

    @Override
    public void update(ImageButton imageButton, Progress.State state) {
//        imageButton.setBackgroundColor(getColorByEstimate(state.getEstimate()));
//        imageButton.setImageBitmap(BitmapFactory.decodeFile(URL));
        imageButton.setImageResource(getResource(state));
        imageButton.setBackground(null);
        imageButton.setScaleX(1.2f);
    }

    //FIXME using map <Category, Estimate, id> for avoid double switch
    // FIXME caching
    private int getResource(Progress.State state) {
        switch (state.getCategory()) {
            case Food:
                switch (state.getEstimate()) {
                    case Undefined: return R.mipmap.square_food_undefined;
                    case Bad: return R.mipmap.square_food_bad;
                    case Good:  return R.mipmap.square_food_good;
                    case Planned:  return R.mipmap.square_food_planned;
                }
            case Sport:
                switch (state.getEstimate()) {
                    case Undefined: return R.mipmap.square_sport_undefined;
                    case Bad: return R.mipmap.square_sport_bad;
                    case Good: return R.mipmap.square_sport_good;
                    case Planned:  return R.mipmap.square_sport_planned;
                }
        }
        return 0;
    }
}
