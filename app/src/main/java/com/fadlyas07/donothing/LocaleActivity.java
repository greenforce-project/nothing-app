package com.fadlyas07.donothing;

import android.app.Activity;
import android.content.Context;

abstract class LocaleActivity extends Activity {

    @Override
    protected void attachBaseContext(
            Context newBase
    ) {
        super.attachBaseContext(
                AppLocale.wrap(newBase)
        );
    }
}
