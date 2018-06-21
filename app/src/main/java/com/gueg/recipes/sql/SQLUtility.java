package com.gueg.recipes.sql;


import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.gueg.recipes.sql.SQLReaderContract.SQLEntry;
import com.gueg.recipes.Recipe;

import java.util.ArrayList;

public class SQLUtility {

    public static final String SQL_ADDED = "com.gueg.recipes.sql.SQLUtility.added";
    public static final String SQL_MODIFIED = "com.gueg.recipes.sql.SQLUtility.modified";
    public static final String SQL_REMOVED = "com.gueg.recipes.sql.SQLUtility.removed";

    public static final String SQL_RECIPE = "com.gueg.recipes.sql.SQLUtility.recipe";

    private SQLReader helper;
    private static SQLUtility utility;
    private static String[] projection = {
            SQLEntry._ID,
            SQLEntry.DB_COLUMN_TITLE,
            SQLEntry.DB_COLUMN_URL,
            SQLEntry.DB_COLUMN_DATE
    };


    private ArrayList<Recipe> _entrees = new ArrayList<>();
    private ArrayList<Recipe> _plats = new ArrayList<>();
    private ArrayList<Recipe> _desserts = new ArrayList<>();



    private BroadcastReceiver _receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()!=null) {
                switch (intent.getAction()) {
                    case SQL_ADDED:
                        write((Recipe) intent.getSerializableExtra(SQL_RECIPE));
                        break;
                    case SQL_MODIFIED:
                        break;
                    case SQL_REMOVED:
                        removeItem((Recipe) intent.getSerializableExtra(SQL_RECIPE));
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private SQLUtility(Context context) {
        helper = new SQLReader(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SQL_ADDED);
        filter.addAction(SQL_MODIFIED);
        filter.addAction(SQL_REMOVED);
        LocalBroadcastManager.getInstance(context).registerReceiver(_receiver, filter);
    }

    public static synchronized void init(Context c) {
        utility = new SQLUtility(c);
    }

    public static synchronized SQLUtility getInstance() {
        return utility;
    }

    public ArrayList<Recipe> readRecipes() {

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                SQLEntry.DB_TABLE_NAME,                     // The table to query
                projection,                                 // The columns to return
                null,                                       // The columns for the WHERE clause
                null,                                       // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                SQLEntry.DB_COLUMN_DATE +" DESC"        // The sort order
        );

        ArrayList<Recipe> items = new ArrayList<>();
        while(cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(SQLEntry.DB_COLUMN_TITLE));
            String url = cursor.getString(cursor.getColumnIndexOrThrow(SQLEntry.DB_COLUMN_URL));
            Long date = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(SQLEntry.DB_COLUMN_DATE)));

            //items.add(new Recipe(title,0,0,null,null,null));
        }
        cursor.close();

        return items;
    }

    private void write(Recipe item) {
        switch (item.getCategory()) {
            case Recipe.CATEGORY_ENTREE:
                _entrees.add(item);
                break;
            case Recipe.CATEGORY_PLAT:
                _plats.add(item);
                break;
            case Recipe.CATEGORY_DESSERT:
                _desserts.add(item);
                break;
            default:
                break;

        }

        Log.d(":-:",String.format("SQL - \t entrées : %d \t plats %d \t desserts %d", _entrees.size(), _plats.size(), _desserts.size()));

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();/*
        values.put(SQLEntry.DB_COLUMN_TITLE, item.title);
        values.put(SQLEntry.DB_COLUMN_URL, item.url);
        values.put(SQLEntry.DB_COLUMN_DATE, item.date.toString());
        */
        //db.insert(SQLEntry.DB_TABLE_NAME, null, values);
    }


    public void removeItem(Recipe item) {
        switch (item.getCategory()) {
            case Recipe.CATEGORY_ENTREE:
                _entrees.remove(item);
                break;
            case Recipe.CATEGORY_PLAT:
                _plats.remove(item);
                break;
            case Recipe.CATEGORY_DESSERT:
                _desserts.remove(item);
                break;
            default:
                break;
        }

        Log.d(":-:",String.format("SQL - \t entrées : %d \t plats %d \t desserts %d", _entrees.size(), _plats.size(), _desserts.size()));

        SQLiteDatabase db = helper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = SQLEntry.DB_COLUMN_TITLE + " LIKE ? AND " + SQLEntry.DB_COLUMN_URL + " LIKE ? AND " + SQLEntry.DB_COLUMN_DATE + " LIKE ?" ;
        // Specify arguments in placeholder order.
        //String[] selectionArgs = {item.title, item.url, item.date.toString()};
        // Issue SQL statement.
        //db.delete(SQLEntry.DB_TABLE_NAME, selection, selectionArgs);
    }

    public ArrayList<Recipe> get_entrees() {
        return _entrees;
    }

    public ArrayList<Recipe> get_plats() {
        return _plats;
    }

    public ArrayList<Recipe> get_desserts() {
        return _desserts;
    }

    public void onDestroy() {
        helper.close();
    }

}
