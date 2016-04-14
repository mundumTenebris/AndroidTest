package ru.androidtest.dataProviders;

import android.content.Intent;
import android.os.Bundle;

public abstract class AbstractDataProvider {

    public abstract int getCount();

    public abstract Data getItem(int index);

    public abstract void removeItem(int position);

    public abstract void moveItem(int fromPosition, int toPosition);

    public abstract int undoLastRemoval();

    public abstract void setMData(int requestId, Intent requestIntent, int resultCode, Bundle resultData);

    public static abstract class Data {
        public abstract int getId();

        public abstract boolean isSectionHeader();

        public abstract int getViewType();

        public abstract String getText();

        public abstract boolean isPinned();

        public abstract void setPinned(boolean pinned);
    }
}
