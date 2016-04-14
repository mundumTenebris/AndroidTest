package ru.androidtest.dataBaseLayer;


import android.net.Uri;
import android.provider.BaseColumns;

public class ContractClass {
    public static final String AUTHORITY     = "ru.androidtest.dataLayer.provider";
    public static final Uri    AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    private ContractClass() {}

    //Базовая информация
    public static final class LinkedListTable implements BaseColumns {

        //Название таблицы, и адрес
        public static final String   TABLE_NAME              = "linkedList";
        //URI доступа к данным
        public static final Uri      CONTENT_URI             = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);
        //Это понадобится если использовать ContentObserver
        public static final Uri      CONTENT_DELETE_ITEM_URI = Uri.withAppendedPath(CONTENT_URI, "DELETE");
        public static final Uri      CONTENT_UPDATE_ITEM_URI = Uri.withAppendedPath(CONTENT_URI, "UPDATE");
        public static final Uri      CONTENT_QUERY_URI       = Uri.withAppendedPath(CONTENT_URI, "QUERY");
        //MIME типы
        public static final String   CONTENT_TYPE            = "vnd.android.cursor.dir/linkedList";
        public static final String   CONTENT_ITEM_TYPE       = "vnd.android.cursor.item/linkedList";
        //Поля таблицы
        public static final String   DATA_FIELD              = "dataField";
        public static final String   PREV_ELEMENT            = "PrevElement";
        public static final String   NEXT_ELEMENT            = "NextElement";
        //Сортировака
        public static final String   DEFAULT_ORDERBY         = _ID + " ASC";
        //Проекция
        public static final String[] DEFAULT_PROJECTION      = new String[]{
                _ID,
                DATA_FIELD,
                PREV_ELEMENT,
                NEXT_ELEMENT
        };
        //Скрипт создания таблицы
        public static final String   SQL_CREATE_TABLE        = "CREATE TABLE " + TABLE_NAME + " ("
                                                               + _ID + " INTEGER PRIMARY KEY,"
                                                               + DATA_FIELD + " TEXT,"
                                                               + PREV_ELEMENT + " INTEGER,"
                                                               + NEXT_ELEMENT + " INTEGER"
                                                               + ");";

        //Скрипт insert таблицы
        public static final String SQL_INSERT_TABLE = "INSERT INTO " + TABLE_NAME + " ("
                                                      + _ID + ","
                                                      + DATA_FIELD + ","
                                                      + PREV_ELEMENT + ","
                                                      + NEXT_ELEMENT + ")"
                                                      + " VALUES(?,?,?,?)";
    }


}
