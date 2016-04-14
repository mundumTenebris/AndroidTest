package ru.androidtest.handlers.impl.CRUDCommand;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import java.util.ArrayList;

import ru.androidtest.dataBaseLayer.ContractClass.LinkedListTable;
import ru.androidtest.handlers.SFBaseCommand;
import ru.androidtest.objects.LinkedListObject;


public class QUERYCommand extends SFBaseCommand {
    private Bundle             data;
//    private LinkedListObject[] linkedListObjects;

    public QUERYCommand() {
    }

    private QUERYCommand(Parcel in) {
    }

    @Override
    protected void doExecute(Intent intent, Context context, ResultReceiver callback) {
        LinkedListObject[] linkedListObjects;
        data = new Bundle();
        ArrayList<LinkedListObject> arrayListObjects = new ArrayList<>();
        int                         counter          = 0;

        Cursor cursor = context.getContentResolver().query(LinkedListTable.CONTENT_URI,
                                                           LinkedListTable.DEFAULT_PROJECTION,
                                                           null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(LinkedListTable._ID));
                String data = cursor.getString(cursor.getColumnIndex(LinkedListTable.DATA_FIELD));
                int next = cursor.getInt(cursor.getColumnIndex(LinkedListTable.NEXT_ELEMENT));
                int prev = cursor.getInt(cursor.getColumnIndex(LinkedListTable.PREV_ELEMENT));
//                arrayListObjects
                Log.d("debug_l", "---------------------------------------------------------------");
                Log.d("debug_l", String.valueOf(id));
                Log.d("debug_l", String.valueOf(data));
                Log.d("debug_l", "prev " + String.valueOf(prev));
                Log.d("debug_l", "next " + String.valueOf(next));
                Log.d("debug_l", "---------------------------------------------------------------");
                arrayListObjects.add(new LinkedListObject(id, data, next, prev));
                counter++;
            }
            cursor.close();
        }
        linkedListObjects = new LinkedListObject[arrayListObjects.size()];
        arrayListObjects.trimToSize();
//        Collections.sort(arrayListObjects);
        arrayListObjects = LinkedListObject.sort(arrayListObjects);
        arrayListObjects.toArray(linkedListObjects);

        if (counter > 0) {
            data.putParcelableArray("QUERY", linkedListObjects);
            data.putString("Данные чтения", "Ляляляля");
            notifySuccess(data);
        } else {
            data.putString("Данные чтения", "Нененене");
            notifyFailure(data);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public static final Parcelable.Creator<QUERYCommand> CREATOR = new Parcelable.Creator<QUERYCommand>() {
        public QUERYCommand createFromParcel(Parcel in) {
            return new QUERYCommand(in);
        }

        public QUERYCommand[] newArray(int size) {
            return new QUERYCommand[size];
        }
    };
}