package ua.in.handycontacts2021;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import ua.in.handycontacts2021.model.DBHelper;

import static ua.in.handycontacts2021.model.DBHelper.keyCatalogMaster;
import static ua.in.handycontacts2021.model.DBHelper.tableName;

public class MasterDetaBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_database_home);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_master);
        //setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.master_fab);
        //fab.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
                //Snackbar.make(view, "Напишите свой отзыв", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                //MasterNewCatalogCategoryFragment fragment = new MasterNewCatalogCategoryFragment();
                //fragment.setArguments(arguments);
                //getSupportFragmentManager().beginTransaction()
                //        .replace(R.id.master_new_catalog_container, fragment)
                //        .commit();
        //    }
        //});

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);}

        if (savedInstanceState == null) {
            String master = "";
            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = "SELECT * FROM " + tableName;
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst(); // переходим на первую строку
            // извлекаем данные из курсора
            master = c.getString(c.getColumnIndex(keyCatalogMaster));
            c.close();
            dbHelper.close();

        switch (master) {
            case "category":
                MasterDataBaseCategoryFragment fragment = new MasterDataBaseCategoryFragment();
                //fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_container, fragment)
                        .commit();
                break;

            case "country":
                MasterDataBaseCountryFragment fragment4 = new MasterDataBaseCountryFragment();
                //fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_container, fragment4)
                        .commit();
                break;

            case "city":
                MasterDataBaseCityFragment fragment5 = new MasterDataBaseCityFragment();
                //fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_container, fragment5)
                        .commit();
                break;

            case "usersDetail":
                MasterDataBaseUsersDetailFragment fragment6 = new MasterDataBaseUsersDetailFragment();
                //fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_container, fragment6)
                        .commit();
                break;

            case "policy":
                MasterDataBasePolicyFragment fragment7 = new MasterDataBasePolicyFragment();
                //fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_container, fragment7)
                        .commit();
                break;

                default:
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();

        /*String masterBack = "";
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + tableName;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst(); // переходим на первую строку
        // извлекаем данные из курсора
        masterBack = c.getString(c.getColumnIndex(keyBack));
        //String item_content = c.getString(c.getColumnIndex(CatsDataBase.CATNAME));
        c.close();
        dbHelper.close();

        Context context = this;
        Intent intent;
        long rowID;
        ContentValues cv;
        switch (masterBack) {
            case "home":
                dbHelper = new DBHelper(context);
                // создаем объект для данных
                cv = new ContentValues();
                // подключаемся к БД
                db = dbHelper.getWritableDatabase();
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                cv.put(dbHelper.keyCatalogMaster, "home");
                //cv.put(keyBack, "category");
                //cv.put("dbBalance", String.valueOf(roundUp(dbItems.dbBalance, 2)));
                // вставляем запись и получаем ее ID
                rowID = db.update(tableName, cv, null, null);
                //long rowID = db.insert(tableName[0], null, cv);
                //Log.d(TAG, "row inserted, ID = " + rowID);
                // закрываем подключение к БД
                dbHelper.close();

                intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                break;

            case "addDovidnik":
                dbHelper = new DBHelper(context);
                // создаем объект для данных
                cv = new ContentValues();
                // подключаемся к БД
                db = dbHelper.getWritableDatabase();
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                cv.put(dbHelper.keyCatalogMaster, "addDovidnik");
                //cv.put(keyBack, "category");
                //cv.put("dbBalance", String.valueOf(roundUp(dbItems.dbBalance, 2)));
                // вставляем запись и получаем ее ID
                rowID = db.update(tableName, cv, null, null);
                //long rowID = db.insert(tableName[0], null, cv);
                //Log.d(TAG, "row inserted, ID = " + rowID);
                // закрываем подключение к БД
                dbHelper.close();

                navigateUpTo(new Intent(this, MainActivity.class));
                break;

            case "category":
                dbHelper = new DBHelper(context);
                // создаем объект для данных
                cv = new ContentValues();
                // подключаемся к БД
                db = dbHelper.getWritableDatabase();
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                cv.put(dbHelper.keyCatalogMaster, "category");
                //cv.put(keyBack, "category");
                //cv.put("dbBalance", String.valueOf(roundUp(dbItems.dbBalance, 2)));
                // вставляем запись и получаем ее ID
                rowID = db.update(tableName, cv, null, null);
                //long rowID = db.insert(tableName[0], null, cv);
                //Log.d(TAG, "row inserted, ID = " + rowID);
                // закрываем подключение к БД
                dbHelper.close();

                intent = new Intent(context, MasterNewCatalogActivity.class);
                context.startActivity(intent);
                break;

            case "city":
                dbHelper = new DBHelper(this);
                // создаем объект для данных
                cv = new ContentValues();
                // подключаемся к БД
                db = dbHelper.getWritableDatabase();
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                cv.put(dbHelper.keyCatalogMaster, "city");
                //cv.put(keyBack, "category");
                //cv.put("dbBalance", String.valueOf(roundUp(dbItems.dbBalance, 2)));
                // вставляем запись и получаем ее ID
                rowID = db.update(tableName, cv, null, null);
                //long rowID = db.insert(tableName[0], null, cv);
                //Log.d(TAG, "row inserted, ID = " + rowID);
                // закрываем подключение к БД
                dbHelper.close();

                intent = new Intent(context, MasterNewCatalogActivity.class);
                context.startActivity(intent);
                break;

            case "country":
                dbHelper = new DBHelper(context);
                // создаем объект для данных
                cv = new ContentValues();
                // подключаемся к БД
                db = dbHelper.getWritableDatabase();
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                cv.put(dbHelper.keyCatalogMaster, "country");
                //cv.put(keyBack, "category");
                //cv.put("dbBalance", String.valueOf(roundUp(dbItems.dbBalance, 2)));
                // вставляем запись и получаем ее ID
                rowID = db.update(tableName, cv, null, null);
                //long rowID = db.insert(tableName[0], null, cv);
                //Log.d(TAG, "row inserted, ID = " + rowID);
                // закрываем подключение к БД
                dbHelper.close();

                intent = new Intent(context, MasterNewCatalogActivity.class);
                context.startActivity(intent);
                break;

            case "usersDetail":
                dbHelper = new DBHelper(context);
                cv = new ContentValues();
                db = dbHelper.getWritableDatabase();
                cv.put(dbHelper.keyCatalogMaster, "usersDetail");
                rowID = db.update(tableName, cv, null, null);
                dbHelper.close();

                intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                break;

            default:
                break;
        }*/
    }
}