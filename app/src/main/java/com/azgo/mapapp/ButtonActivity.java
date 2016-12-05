package com.azgo.mapapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;



import com.google.android.gms.wallet.fragment.SupportWalletFragment;


public class ButtonActivity extends Button implements View.OnClickListener {

    private static final int[] mStates = { R.attr.settings_on, R.attr.settings_off };
    private int mStateIndex = 0; // first state is "on"

    private SupportWalletFragment.OnStateChangedListener mListener;
    public ButtonActivity(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        changeState();
    }

    public void changeState() {
        mStateIndex = (mStateIndex+1) % mStates.length;

        // notify listener
        if(mListener != null) {
            mListener.onStateChanged(mStates[mStateIndex]);
        }
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {

        final int[] drawableState = super.onCreateDrawableState(extraSpace+1);

        int [] state = { mStates[mStateIndex] };

        mergeDrawableStates(drawableState, state);

        return drawableState;
    }

    public void setOnStateChangedListener(SupportWalletFragment.OnStateChangedListener l) {
        this.mListener = l;
    }
}

