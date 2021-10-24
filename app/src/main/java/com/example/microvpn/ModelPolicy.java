package com.example.microvpn;

public enum  ModelPolicy {
    RED(R.string.red, R.layout.policy_browsing),
    BLUE(R.string.blue, R.layout.policy_streaming),
    GREEN(R.string.green, R.layout.policy_malwre);


    private int mTitleResId;
    private int mLayoutResId;

    ModelPolicy(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }
}

