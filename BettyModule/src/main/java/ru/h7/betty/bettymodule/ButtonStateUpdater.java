package ru.h7.betty.bettymodule;

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
