package ru.h7.betty.bettymodule;

import android.graphics.Color;
import android.widget.ImageButton;

interface ButtonStateUpdater {
    void update(ImageButton imageButton, Progress.State state);
}

class BackgroundButtonStateUpdater implements ButtonStateUpdater {
    private int colors[] = {Color.parseColor("#cccccc"), Color.RED, Color.GREEN, Color.YELLOW};

    @Override
    public void update(ImageButton imageButton, Progress.State state) {
        imageButton.setBackgroundColor(colors[state.getEstimate().ordinal()]);
    }
}
