package ru.androidtest.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

import ru.androidtest.dataProviders.AbstractDataProvider;

/**
 * Created by blank on 02.01.2016.
 */
public class LinkedListObject extends AbstractDataProvider.Data implements Parcelable, Comparable<LinkedListObject> {
    private int    id;
    private String data;
    private int next;
    private int prev;

    public LinkedListObject(int _id, String _data, int _next, int _prev) {
        id = _id;
        data = _data;
        next = _next;
        prev = _prev;
    }

    public int getNext() {
        return next;
    }

    public int getPrev() {
        return prev;
    }
    public void setNext(int next) {
        this.next = next;
    }

    public void setPrev(int prev) {
        this.prev = prev;
    }

    protected LinkedListObject(Parcel in) {
        id = in.readInt();
        data = in.readString();
        next = in.readInt();
        prev = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(data);
        dest.writeInt(next);
        dest.writeInt(prev);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isSectionHeader() {
        return false;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public String getText() {
        return data;
    }

    @Override
    public boolean isPinned() {
        return false;
    }

    @Override
    public void setPinned(boolean pinned) {

    }

    @Override
    public int compareTo(@NonNull LinkedListObject another) {
        if (prev == another.id) {
            return 1;
        }

        if (next == another.id) {
            return -1;
        }

        if (prev == 0) {
            return -1;
        }

        if (next == 0) {
            return 1;
        }

        if (next == another.prev) {
            return -1;
        }

        if (prev == another.next) {
            return 1;
        }

        return 0;
    }

    public static final Creator<LinkedListObject> CREATOR = new Creator<LinkedListObject>() {
        @Override
        public LinkedListObject createFromParcel(Parcel in) {
            return new LinkedListObject(in);
        }

        @Override
        public LinkedListObject[] newArray(int size) {
            return new LinkedListObject[size];
        }
    };

    public static ArrayList<LinkedListObject> sort(ArrayList<LinkedListObject> _source) {
        ArrayList<LinkedListObject>        resultArray = new ArrayList<>();
        LinkedListObject                   header      = new LinkedListObject(0, "Header", 0, 0);
        LinkedListObject                   target;
        HashMap<Integer, LinkedListObject> hashMap     = new HashMap<>();

        for (LinkedListObject object : _source) {
            hashMap.put(object.getId(), object);
            if (object.getPrev() == 0) {
                header.setPrev(object.getId());
            }
        }

        while (hashMap.size() > 0) {
            target = hashMap.get(header.getPrev());
            hashMap.remove(target.getId());
            resultArray.add(target);
            header.setPrev(target.getNext());
        }


        return resultArray;
    }
}
