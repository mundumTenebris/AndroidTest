package ru.androidtest.dataBaseLayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.androidtest.dataBaseLayer.ContractClass.LinkedListTable;

public class TestSQLH extends SQLiteOpenHelper {
    private static final String DATABASE_NAME    = "TestDB";
    private static final int    DATABASE_VERSION = 3;

    public TestSQLH(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public TestSQLH(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LinkedListTable.SQL_CREATE_TABLE);
        db.execSQL("insert into " + LinkedListTable.TABLE_NAME + " values ('1', 'Японская кухня', '0', '3');");
        db.execSQL("insert into " + LinkedListTable.TABLE_NAME + " values ('2', 'Паназиатская кухня', '3', '4');");
        db.execSQL("insert into " + LinkedListTable.TABLE_NAME + " values ('3', 'Итальянская кухня', '1', '2');");
        db.execSQL("insert into " + LinkedListTable.TABLE_NAME + " values ('4', 'Китайская кухня', '2', '5');");
        db.execSQL("insert into " + LinkedListTable.TABLE_NAME + " values ('5', 'Русская кухня', '4', '6');");
        db.execSQL("insert into " + LinkedListTable.TABLE_NAME + " values ('6', 'Индийская кухня', '5', '0');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO
        db.execSQL("DROP TABLE IF EXISTS " + LinkedListTable.TABLE_NAME);
        onCreate(db);
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LinkedListTable.TABLE_NAME);
        onCreate(db);
    }

    public void updateData(SQLiteDatabase db) {

        //Удаляем таблицы
        db.execSQL("DROP TABLE IF EXISTS " + LinkedListTable.TABLE_NAME);

        //Создаем заново
        db.execSQL(LinkedListTable.SQL_CREATE_TABLE);

    }

//    @Override
//    public void onOpen(SQLiteDatabase db) {
//        super.onOpen(db);
//        if (!db.isReadOnly()) {
//            // Enable foreign key constraints
//            db.execSQL("PRAGMA foreign_keys=ON;");
//        }
//    }
}
