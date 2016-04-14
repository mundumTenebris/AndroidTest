package ru.androidtest.dataProviders;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;

import java.util.LinkedList;
import java.util.List;

import ru.androidtest.dataBaseLayer.ContractClass.LinkedListTable;
import ru.androidtest.objects.LinkedListObject;
import ru.androidtest.sfClasses.SFServiceHelper;

public class DataProvider extends AbstractDataProvider {
    private int requestId = -1;
    private List<LinkedListObject> mData;
    private LinkedListObject       mLastRemovedData;
    private int mLastRemovedPosition = -1;
    private SFServiceHelper helper;

    public DataProvider(SFServiceHelper _helper) {
        helper = _helper;

        mData = new LinkedList<>();

        requestId = helper.queryCommand();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Data getItem(int index) {
        if (index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException("index = " + index);
        }

        return mData.get(index);
    }

    @Override
    public int undoLastRemoval() {
        if (mLastRemovedData != null) {
            int insertedPosition;
            if (mLastRemovedPosition >= 0 && mLastRemovedPosition < mData.size()) {
                insertedPosition = mLastRemovedPosition;
            } else {
                insertedPosition = mData.size();
            }

            mData.add(insertedPosition, mLastRemovedData);

            mLastRemovedData = null;
            mLastRemovedPosition = -1;

            return insertedPosition;
        } else {
            return -1;
        }
    }

    @Override
    public void setMData(int requestId, Intent requestIntent, int resultCode, Bundle resultData) {

        LinkedListObject[] objects = (LinkedListObject[]) resultData.getParcelableArray("QUERY");

        if (objects != null) {
            mData.clear();
            for (LinkedListObject object : objects) {
                mData.add(object);
            }
        }
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        updateDBList(fromPosition, toPosition);

        final LinkedListObject item = mData.remove(fromPosition);

        mData.add(toPosition, item);
        mLastRemovedPosition = -1;

    }

    @Override
    public void removeItem(int position) {
        //noinspection UnnecessaryLocalVariable
        final LinkedListObject removedItem = mData.remove(position);

        mLastRemovedData = removedItem;
        mLastRemovedPosition = position;
    }

    private void updateDBList(int _fromPosition, int _toPosition) {
        boolean isPair = Math.abs(_fromPosition - _toPosition) == 1;

        if (!isPair) {
            updateItemsFrom(_fromPosition);
            updateItemsTo(_fromPosition, _toPosition);
        } else {
            updatePairItem(_fromPosition, _toPosition);
        }
    }

    private void makeRequest(LinkedListObject _item) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(LinkedListTable._ID, _item.getId());
        contentValues.put(LinkedListTable.DATA_FIELD, _item.getText());
        contentValues.put(LinkedListTable.NEXT_ELEMENT, _item.getNext());
        contentValues.put(LinkedListTable.PREV_ELEMENT, _item.getPrev());
        requestId = helper.updateCommand(_item.getId(), contentValues);
    }

    private void updateItemsFrom(int _fromPosition) {
        LinkedListObject itemPrev;
        LinkedListObject itemNext;
        LinkedListObject itemFrom = mData.get(_fromPosition);

        if (itemFrom.getPrev() == 0) {
            itemNext = mData.get(_fromPosition + 1);
            itemNext.setPrev(0);
            makeRequest(itemNext);
        }
        if (itemFrom.getNext() == 0) {
            itemPrev = mData.get(_fromPosition - 1);
            itemPrev.setNext(0);
            makeRequest(itemPrev);
        }
        if (itemFrom.getPrev() != 0 && itemFrom.getNext() != 0) {
            itemPrev = mData.get(_fromPosition - 1);
            itemNext = mData.get(_fromPosition + 1);

            itemPrev.setNext(itemNext.getId());
            itemNext.setPrev(itemPrev.getId());
            makeRequest(itemNext);
            makeRequest(itemPrev);
        }
    }

    private void updateItemsTo(int _fromPosition, int _toPosition) {
        LinkedListObject itemNext;
        LinkedListObject itemPrev;
        LinkedListObject itemFrom = mData.get(_fromPosition);
        LinkedListObject itemTo   = mData.get(_toPosition);

        if (itemTo.getPrev() == 0) {
            itemTo.setPrev(itemFrom.getId());
            itemFrom.setPrev(0);
            itemFrom.setNext(itemTo.getId());
            makeRequest(itemFrom);
            makeRequest(itemTo);
        } else if (itemTo.getNext() == 0) {
            itemTo.setNext(itemFrom.getId());
            itemFrom.setNext(0);
            itemFrom.setPrev(itemTo.getId());
            makeRequest(itemFrom);
            makeRequest(itemTo);
        } else {
            if (_fromPosition < _toPosition) {
                itemNext = mData.get(_toPosition + 1);

                itemFrom.setPrev(itemTo.getId());
                itemFrom.setNext(itemNext.getId());

                itemTo.setNext(itemFrom.getId());
                itemNext.setPrev(itemFrom.getId());

                makeRequest(itemNext);
            } else {
                itemPrev = mData.get(_toPosition - 1);
                itemPrev.setNext(itemFrom.getId());

                itemFrom.setPrev(itemPrev.getId());
                itemFrom.setNext(itemTo.getId());

                itemTo.setPrev(itemFrom.getId());
                makeRequest(itemPrev);
            }

            makeRequest(itemFrom);
            makeRequest(itemTo);
        }
    }

    private void updatePairItem(int _fromPosition, int _toPosition) {
        int              buff;
        LinkedListObject itemPrev;
        LinkedListObject itemNext;
        LinkedListObject itemFrom = mData.get(_fromPosition);
        LinkedListObject itemTo   = mData.get(_toPosition);

        if (itemFrom.getNext() == itemTo.getId()) {
            buff = itemTo.getNext();

            itemTo.setPrev(itemFrom.getPrev());
            itemTo.setNext(itemFrom.getId());
            itemFrom.setPrev(itemTo.getId());
            itemFrom.setNext(buff);

            if (itemFrom.getNext() != 0) {
                itemNext = mData.get(_toPosition + 1);
                itemNext.setPrev(itemFrom.getId());
                makeRequest(itemNext);
            }

            if (itemTo.getPrev() != 0) {
                itemPrev = mData.get(_fromPosition - 1);
                itemPrev.setNext(itemTo.getId());
                makeRequest(itemPrev);
            }

        } else {
            buff = itemTo.getPrev();

            itemTo.setPrev(itemFrom.getId());
            itemTo.setNext(itemFrom.getNext());
            itemFrom.setNext(itemTo.getId());
            itemFrom.setPrev(buff);

            if (itemFrom.getPrev() != 0) {
                itemPrev = mData.get(_toPosition - 1);
                itemPrev.setNext(itemFrom.getId());
                makeRequest(itemPrev);
            }

            if (itemTo.getNext() != 0) {
                itemNext = mData.get(_fromPosition + 1);
                itemNext.setPrev(itemTo.getId());
                makeRequest(itemNext);
            }
        }
        makeRequest(itemFrom);
        makeRequest(itemTo);
    }
}
