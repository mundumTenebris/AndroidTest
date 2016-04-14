package ru.androidtest.handlers.impl.CRUDCommand;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.ResultReceiver;

import ru.androidtest.R;
import ru.androidtest.dataBaseLayer.ContractClass.LinkedListTable;
import ru.androidtest.handlers.SFBaseCommand;


public class UPDATECommand extends SFBaseCommand {
    public static final Creator<UPDATECommand> CREATOR = new Creator<UPDATECommand>() {
        public UPDATECommand createFromParcel(Parcel in) {
            return new UPDATECommand(in);
        }

        public UPDATECommand[] newArray(int size) {
            return new UPDATECommand[size];
        }
    };
    private int           itemId;
    private ContentValues values;
    private Bundle        data;

    public UPDATECommand(int _itemId, ContentValues _values) {
        itemId = _itemId;
        values = _values;
    }

    private UPDATECommand(Parcel in) {
        itemId = in.readInt();
        values = in.readParcelable(ClassLoader.getSystemClassLoader());
    }

    @Override
    protected void doExecute(Intent intent, Context context, ResultReceiver callback) {
        int numRows;
        data = new Bundle();

        numRows = context.getContentResolver().update(Uri.withAppendedPath(LinkedListTable.CONTENT_URI,
                                                                           Integer.toString(itemId)),
                                                      values, null, null);
        if (numRows != 0) {
            data.putInt(context.getString(R.string.list_item_id), itemId);
            notifySuccess(data);
        } else {
            data.putInt(context.getString(R.string.list_item_id), itemId);
            notifyFailure(data);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(itemId);
        dest.writeParcelable(values, PARCELABLE_WRITE_RETURN_VALUE);
    }
}