package ru.androidtest.sfClasses;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.SparseArray;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import ru.androidtest.handlers.SFBaseCommand;
import ru.androidtest.handlers.impl.CRUDCommand.QUERYCommand;
import ru.androidtest.handlers.impl.CRUDCommand.UPDATECommand;
import ru.androidtest.service.SFCommandExecutorService;


public class SFServiceHelper {

    private HashSet<SFServiceCallbackListener> currentListeners = new HashSet<>();

    private AtomicInteger idCounter = new AtomicInteger();

    private SparseArray<Intent> pendingActivities = new SparseArray<>();

    private Application application;

    SFServiceHelper(Application app) {
        this.application = app;
    }

    public void addListener(SFServiceCallbackListener currentListener) {
        currentListeners.add(currentListener);
    }

    public void removeListener(SFServiceCallbackListener currentListener) {
        currentListeners.remove(currentListener);
    }

    // =========================================

    //Загрузка данных
    public int queryCommand() {
        final int requestId = createId();

        Intent i = createIntent(application, new QUERYCommand(), requestId);
        return runRequest(requestId, i);
    }

    public int updateCommand(int _itemId, ContentValues _values) {
        final int requestId = createId();

        Intent i = createIntent(application, new UPDATECommand(_itemId, _values), requestId);
        return runRequest(requestId, i);
    }
    // =========================================

    public void cancelCommand(int requestId) {
        Intent i = new Intent(application, SFCommandExecutorService.class);
        i.setAction(SFCommandExecutorService.ACTION_CANCEL_COMMAND);
        i.putExtra(SFCommandExecutorService.EXTRA_REQUEST_ID, requestId);

        application.startService(i);
        pendingActivities.remove(requestId);
    }

    public boolean isPending(int requestId) {
        return pendingActivities.get(requestId) != null;
    }

    public boolean check(Intent intent, Class<? extends SFBaseCommand> clazz) {
        Parcelable commandExtra = intent.getParcelableExtra(SFCommandExecutorService.EXTRA_COMMAND);
        return commandExtra != null && commandExtra.getClass().equals(clazz);
    }

    private int createId() {
        return idCounter.getAndIncrement();
    }

    private int runRequest(final int requestId, Intent i) {
        pendingActivities.append(requestId, i);
        application.startService(i);
        return requestId;
    }

    private Intent createIntent(final Context context, SFBaseCommand command, final int requestId) {
        Intent i = new Intent(context, SFCommandExecutorService.class);
        i.setAction(SFCommandExecutorService.ACTION_EXECUTE_COMMAND);

        i.putExtra(SFCommandExecutorService.EXTRA_COMMAND, command);
        i.putExtra(SFCommandExecutorService.EXTRA_REQUEST_ID, requestId);
        i.putExtra(SFCommandExecutorService.EXTRA_STATUS_RECEIVER,
                   new ResultReceiver(new Handler()) {
                       @Override
                       protected void onReceiveResult(int resultCode, Bundle resultData) {
                           Intent originalIntent = pendingActivities.get(requestId);

                           if (isPending(requestId)) {
                               if (resultCode != SFBaseCommand.RESPONSE_PROGRESS) {
                                   pendingActivities.remove(requestId);
                               }

                               for (SFServiceCallbackListener currentListener : currentListeners) {
                                   if (currentListener != null) {
                                       currentListener.onServiceCallback(requestId, originalIntent, resultCode, resultData);
                                   }
                               }
                           }
                       }
                   });

        return i;
    }

}
