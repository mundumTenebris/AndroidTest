package ru.androidtest.sfClasses;

import android.content.Intent;
import android.os.Bundle;

public interface SFServiceCallbackListener {

    void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle data);

}