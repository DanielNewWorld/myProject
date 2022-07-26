package ua.in.handycontacts2021;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import ua.in.handycontacts2021.model.DBHelper;

import static ua.in.handycontacts2021.model.DBHelper.keyCatalogMaster;
import static ua.in.handycontacts2021.model.DBHelper.tableName;
import static ua.in.handycontacts2021.network.DataService.BASE_URL;

public class MasterDetailActivity extends AppCompatActivity {

    ImageView imgCatalogLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_detail_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar_master_new_catalog);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.master_new_catalog_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        FloatingActionButton fab_back = (FloatingActionButton) findViewById(R.id.master_new_catalog_fab_back);
        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);}

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
         //   Bundle arguments = new Bundle();
          //  arguments.putString(ItemDetailFragment.ARG_catalogTerritory,
          //          getIntent().getStringExtra(ItemDetailFragment.ARG_catalogTerritory));
          //  arguments.putString(ItemDetailFragment.ARG_catalogFIO,
          //          getIntent().getStringExtra(ItemDetailFragment.ARG_catalogFIO));
          //  arguments.putInt(ItemDetailFragment.ARG_catalogID,
          //          getIntent().getIntExtra(ItemDetailFragment.ARG_catalogID, 0));
          //  arguments.putInt(ItemDetailFragment.ARG_userRaiting,
          //          getIntent().getIntExtra(ItemDetailFragment.ARG_userRaiting, 0));
          //  arguments.putString(ItemDetailFragment.ARG_catalogDescription,
          //          getIntent().getStringExtra(ItemDetailFragment.ARG_catalogDescription));
          //  arguments.putString(ItemDetailFragment.ARG_catalogCatalog,
          //          getIntent().getStringExtra(ItemDetailFragment.ARG_catalogCatalog));

            String catalogMaster = "";
            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = "SELECT * FROM " + tableName;
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst(); // переходим на первую строку
            // извлекаем данные из курсора
            catalogMaster = c.getString(c.getColumnIndex(keyCatalogMaster));
            //String item_content = c.getString(c.getColumnIndex(CatsDataBase.CATNAME));
            c.close();
            dbHelper.close();

            imgCatalogLogo = (ImageView) findViewById(R.id.imgCatalogLogo);

            Picasso.with(this)
                    .load(BASE_URL + "/uploads/namefile5.png")
                    .placeholder(R.drawable.avatar_no)
                    .into(imgCatalogLogo);

        switch (catalogMaster) {
            case "category":
                MasterNewCatalogCategoryFragment fragment2 = new MasterNewCatalogCategoryFragment();
                //fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                            .replace(R.id.master_new_catalog_container, fragment2)
                            .commit();
                break;

            case "phone":
                DetailPhoneFragment fragment3 = new DetailPhoneFragment();
                //fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_new_catalog_container, fragment3)
                        .commit();
                break;

            case "country":
                MasterDetailCountryFragment fragment4 = new MasterDetailCountryFragment();
                //fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_new_catalog_container, fragment4)
                        .commit();
                break;

            case "city":
                MasterDetailCityFragment fragment6 = new MasterDetailCityFragment();
                //fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_new_catalog_container, fragment6)
                        .commit();
                break;

            case "publish":
                MasterDetailPublishFragment fragment7 = new MasterDetailPublishFragment();
                //fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_new_catalog_container, fragment7)
                        .commit();
                break;


            default:
                    MasterNewCatalogNameFragment fragment = new MasterNewCatalogNameFragment();
                    //fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_new_catalog_container, fragment)
                        .commit();
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
            case "itemDetail":
                dbHelper = new DBHelper(MasterNewCatalogActivity.this);
                // создаем объект для данных
                cv = new ContentValues();
                // подключаемся к БД
                db = dbHelper.getWritableDatabase();
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                cv.put(dbHelper.keyCatalogMaster, "itemDetail");
                //cv.put(keyBack, "category");
                //cv.put("dbBalance", String.valueOf(roundUp(dbItems.dbBalance, 2)));
                // вставляем запись и получаем ее ID
                rowID = db.update(tableName, cv, null, null);
                //long rowID = db.insert(tableName[0], null, cv);
                //Log.d(TAG, "row inserted, ID = " + rowID);
                // закрываем подключение к БД
                dbHelper.close();

                intent = new Intent(context, ItemDetailActivity.class);
                context.startActivity(intent);
                break;

            case "addDovidnik":
                dbHelper = new DBHelper(MasterNewCatalogActivity.this);
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

            case "publish":
                dbHelper = new DBHelper(MasterNewCatalogActivity.this);
                // создаем объект для данных
                cv = new ContentValues();
                // подключаемся к БД
                db = dbHelper.getWritableDatabase();
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                cv.put(dbHelper.keyCatalogMaster, "publish");
                //cv.put(keyBack, "category");
                //cv.put("dbBalance", String.valueOf(roundUp(dbItems.dbBalance, 2)));
                // вставляем запись и получаем ее ID
                rowID = db.update(tableName, cv, null, null);
                //long rowID = db.insert(tableName[0], null, cv);
                //Log.d(TAG, "row inserted, ID = " + rowID);
                // закрываем подключение к БД
                dbHelper.close();

                MasterNewCatalogPublishFragment fragment7 = new MasterNewCatalogPublishFragment();
                //fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_new_catalog_container, fragment7)
                        .commit();
                break;

            case "category":
                dbHelper = new DBHelper(MasterNewCatalogActivity.this);
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

                MasterNewCatalogCategoryFragment fragment2 = new MasterNewCatalogCategoryFragment();
                //fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_new_catalog_container, fragment2)
                        .commit();
                break;

            case "city":
                dbHelper = new DBHelper(MasterNewCatalogActivity.this);
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

                MasterNewCatalogCityFragment fragment5 = new MasterNewCatalogCityFragment();
                //fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_new_catalog_container, fragment5)
                        .commit();
                break;

            case "country":
                dbHelper = new DBHelper(MasterNewCatalogActivity.this);
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

                MasterNewCatalogCountryFragment fragment4 = new MasterNewCatalogCountryFragment();
                //fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_new_catalog_container, fragment4)
                        .commit();
                break;

            case "phone":
                dbHelper = new DBHelper(MasterNewCatalogActivity.this);
                // создаем объект для данных
                cv = new ContentValues();
                // подключаемся к БД
                db = dbHelper.getWritableDatabase();
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                cv.put(dbHelper.keyCatalogMaster, "phone");
                //cv.put(keyBack, "category");
                //cv.put("dbBalance", String.valueOf(roundUp(dbItems.dbBalance, 2)));
                // вставляем запись и получаем ее ID
                rowID = db.update(tableName, cv, null, null);
                //long rowID = db.insert(tableName[0], null, cv);
                //Log.d(TAG, "row inserted, ID = " + rowID);
                // закрываем подключение к БД
                dbHelper.close();

                MasterNewCatalogPhoneFragment fragment3 = new MasterNewCatalogPhoneFragment();
                //fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_new_catalog_container, fragment3)
                        .commit();
                break;

            case "name":
                dbHelper = new DBHelper(MasterNewCatalogActivity.this);
                // создаем объект для данных
                cv = new ContentValues();
                // подключаемся к БД
                db = dbHelper.getWritableDatabase();
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                cv.put(dbHelper.keyCatalogMaster, "name");
                //cv.put(keyBack, "category");
                //cv.put("dbBalance", String.valueOf(roundUp(dbItems.dbBalance, 2)));
                // вставляем запись и получаем ее ID
                rowID = db.update(tableName, cv, null, null);
                //long rowID = db.insert(tableName[0], null, cv);
                //Log.d(TAG, "row inserted, ID = " + rowID);
                // закрываем подключение к БД
                dbHelper.close();

                MasterNewCatalogNameFragment fragment = new MasterNewCatalogNameFragment();
                //fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_new_catalog_container, fragment)
                        .commit();
                break;

            default:
                break;
        }*/
    }
}
