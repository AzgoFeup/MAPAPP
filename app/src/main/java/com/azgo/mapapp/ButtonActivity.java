package com.azgo.mapapp;

import android.widget.CompoundButton;
import android.widget.ToggleButton;

/**
 * Created by catar on 05/12/2016.
 */

public class Switch extends CompoundButton {

    ToggleButton toggle = (ToggleButton) findViewById(R.id.togglebutton);
    toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                // The toggle is enabled
            } else {
                // The toggle is disabled
            }
        }
    });
}
