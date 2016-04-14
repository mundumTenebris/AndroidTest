package ru.androidtest.dataBaseLayer;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.HashMap;

import ru.androidtest.dataBaseLayer.ContractClass.LinkedListTable;

public class TestProvider extends ContentProvider {
    //Константы операций
    private static final int LIST    = 1;
    private static final int LIST_ID = 2;


    //Проекции
    private static final UriMatcher              sUriMatcher;
    private static       HashMap<String, String> listProjectionMap;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(ContractClass.AUTHORITY, LinkedListTable.TABLE_NAME, LIST);
        sUriMatcher.addURI(ContractClass.AUTHORITY, LinkedListTable.TABLE_NAME + "/#", LIST_ID);

        listProjectionMap = new HashMap<>();
        for (int i = 0; i < LinkedListTable.DEFAULT_PROJECTION.length; i++) {
            listProjectionMap.put(LinkedListTable.DEFAULT_PROJECTION[i],
                                  LinkedListTable.DEFAULT_PROJECTION[i]);
        }
    }

    //Класс БД
    private TestSQLH testSQLH;

    public TestProvider() {
    }

    @Override
    public boolean onCreate() {
        testSQLH = new TestSQLH(getContext());
        return true;
    }


    @Override
    public Cursor query(
            @NonNull Uri _uri,
            String[] _projection,
            String _selection,
            String[] _selectionArgs,
            String _sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String             orderBy      = null;
        Cursor             cursor;

        switch (sUriMatcher.match(_uri)) {
            case LIST:
                queryBuilder.setTables(LinkedListTable.TABLE_NAME);
                queryBuilder.setProjectionMap(listProjectionMap);
                orderBy = _sortOrder == null ? LinkedListTable.DEFAULT_ORDERBY : _sortOrder;
                break;
        }

        SQLiteDatabase database = testSQLH.getReadableDatabase();


        cursor = queryBuilder.query(database, _projection, _selection, _selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), _uri);

        if (sUriMatcher.match(_uri) == LIST) {
            getContext().getContentResolver().notifyChange(LinkedListTable.CONTENT_QUERY_URI, null);
        }

        return cursor;
    }

    @Override
    public int bulkInsert(@NonNull Uri _uri, @NonNull ContentValues[] _values) {
        int             rowInserted = 0;
        SQLiteDatabase  database    = testSQLH.getWritableDatabase();
        SQLiteStatement sqLiteStatement;

        try {
            switch (sUriMatcher.match(_uri)) {
                case LIST:
                    sqLiteStatement = database.compileStatement(LinkedListTable.SQL_INSERT_TABLE);
                    database.beginTransaction();
                    try {
                        for (ContentValues value : _values) {
                            //bind the 1-indexed ?'s to the values specified
                            sqLiteStatement.bindLong(1, value.getAsLong(LinkedListTable._ID));
                            sqLiteStatement.bindString(2, value.getAsString(LinkedListTable.DATA_FIELD));
                            sqLiteStatement.bindLong(3, value.getAsLong(LinkedListTable.PREV_ELEMENT));
                            sqLiteStatement.bindLong(4, value.getAsLong(LinkedListTable.NEXT_ELEMENT));
                            sqLiteStatement.execute();
                        }

                        database.setTransactionSuccessful();
                        rowInserted = _values.length;
                    } finally {
                        database.endTransaction();
                        getContext().getContentResolver().notifyChange(_uri, null);
                    }
                    break;

            }
        } catch (SQLException _e) {
            Toast.makeText(getContext(), "Ошибка при записи в " + _uri, Toast.LENGTH_LONG).show();
        }
        return rowInserted;
    }


    @Override
    public Uri insert(@NonNull Uri _uri, ContentValues _values) {
        long           rowId    = -1;
        Uri            rowUri   = Uri.EMPTY;
        SQLiteDatabase database = testSQLH.getWritableDatabase();

        try {
            switch (sUriMatcher.match(_uri)) {
                case LIST:
                    rowId = database.insertOrThrow(LinkedListTable.TABLE_NAME, LinkedListTable.DATA_FIELD, _values);
                    if (rowId > 0) {
                        rowUri = ContentUris.withAppendedId(LinkedListTable.CONTENT_URI, rowId);
                        getContext().getContentResolver().notifyChange(rowUri, null);
                    }
                    break;
            }
        } catch (SQLException _e) {
            Toast.makeText(getContext(), "Ошибка при записи в " + rowUri, Toast.LENGTH_LONG).show();
        }
        return rowUri;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int            upNum    = 0;
        SQLiteDatabase database = testSQLH.getWritableDatabase();

        try {
            switch (sUriMatcher.match(uri)) {
                case LIST_ID:
                    database.beginTransaction();
                    try {
                        selection = LinkedListTable._ID + " = " + uri.getLastPathSegment();
                        upNum = database.update(LinkedListTable.TABLE_NAME, values, selection, selectionArgs);
                        database.setTransactionSuccessful();
                    } finally {
                        database.endTransaction();
                        getContext().getContentResolver().notifyChange(LinkedListTable.CONTENT_UPDATE_ITEM_URI, null);
                    }
                    break;
            }

        } catch (SQLException _e) {
            Toast.makeText(getContext(), "Ошибка при записи в " + uri, Toast.LENGTH_LONG).show();
        }
        return upNum;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int            deleteNum = 0;
        SQLiteDatabase database  = testSQLH.getWritableDatabase();

        try {
            switch (sUriMatcher.match(uri)) {
                case LIST_ID:
                    selection = LinkedListTable._ID + " = " + uri.getLastPathSegment();
                    deleteNum = database.delete(LinkedListTable.TABLE_NAME, selection, selectionArgs);
                    getContext().getContentResolver().notifyChange(LinkedListTable.CONTENT_DELETE_ITEM_URI, null);
                    break;
            }

        } catch (SQLException _e) {
            Toast.makeText(getContext(), "Ошибка при записи в " + uri, Toast.LENGTH_LONG).show();
        }
        return deleteNum;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case LIST:
                return LinkedListTable.CONTENT_TYPE;
            case LIST_ID:
                return LinkedListTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Неизвестный URI " + uri);
        }
    }
}
