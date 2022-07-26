package ua.in.handycontacts2021;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import ua.in.handycontacts2021.model.DBHelper;

import static ua.in.handycontacts2021.model.DBHelper.keyCatalogMaster;
import static ua.in.handycontacts2021.model.DBHelper.keyLogin;
import static ua.in.handycontacts2021.model.DBHelper.tableName;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private static long back_pressed;
    final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Log.i (TAG, "onCreate: Info");
        Log.w (TAG, "onCreate: Warning");
        Log.d (TAG, "onCreate: Debug");
        Log.v (TAG, "onCreate: Verbose");
        Log.e (TAG, "onCreate: Exception", new Exception("My Exception"));*/

        //Уведомления в смартфоне
        /*Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);*/

        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        //.setContentIntent(pendingIntent)
        builder.setContentTitle("New")
                .setSmallIcon(R.drawable.ic_launcher)
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                //.setWhen(System.currentTimeMillis())
                //.setAutoCancel(true)
                //.setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText("press");
        Notification notification = builder.build();
        //notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;

        //long[] vibrate = {1500, 1000, 1500, 1000};
        //notification.vibrate = vibrate;

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1, notification);*/
        //----

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                Context context = view.getContext();
                Intent intent = new Intent(context, FiltreActivity.class);
                //intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);
                context.startActivity(intent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_no_publish, R.id.nav_publish,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        DBHelper dbHelper;
        SQLiteDatabase db;
        ContentValues cv;
        long rowID;
        Context context = this;
        Intent intent;
        String login;
        DialogAutorization dialogAutorization;
        String query;
        Cursor c;

        switch (id) {
            case R.id.action_login:
                dialogAutorization = new DialogAutorization();
                dialogAutorization.dialogView(this, this);
                return true;

            case R.id.action_settings:
                dbHelper = new DBHelper(this);
                db = dbHelper.getReadableDatabase();
                query = "SELECT * FROM " + tableName;
                c = db.rawQuery(query, null);
                c.moveToFirst(); // переходим на первую строку
                login = c.getString(c.getColumnIndex(keyLogin));
                c.close();
                dbHelper.close();

                if (login.equals("anonym")) {
                    dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(this, this);
                } else {
                    dbHelper = new DBHelper(this);
                    db = dbHelper.getWritableDatabase();
                    cv = new ContentValues();
                    cv.put(keyCatalogMaster, "category");
                    //cv.put(keyBack, "home");
                    rowID = db.update(tableName, cv, null, null);
                    //long rowID = db.insert(tableName, keyCatalogMaster, cv);
                    dbHelper.close();

                    intent = new Intent(context, MasterDetaBaseActivity.class);
                    context.startActivity(intent);
                }
                return true;

            case R.id.action_settings2:
                dbHelper = new DBHelper(this);
                db = dbHelper.getReadableDatabase();
                query = "SELECT * FROM " + tableName;
                c = db.rawQuery(query, null);
                c.moveToFirst(); // переходим на первую строку
                login = c.getString(c.getColumnIndex(keyLogin));
                c.close();
                dbHelper.close();

                if (login.equals("anonym")) {
                    dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(this, this);
                } else {
                    dbHelper = new DBHelper(this);
                    db = dbHelper.getWritableDatabase();
                    cv = new ContentValues();
                    cv.put(keyCatalogMaster, "country");
                    //cv.put(keyBack, "home");
                    rowID = db.update(tableName, cv, null, null);
                    //long rowID = db.insert(tableName, keyCatalogMaster, cv);
                    dbHelper.close();

                    intent = new Intent(context, MasterDetaBaseActivity.class);
                    context.startActivity(intent);
                }
                return true;

            case R.id.action_users:
                dbHelper = new DBHelper(this);
                db = dbHelper.getReadableDatabase();
                query = "SELECT * FROM " + tableName;
                c = db.rawQuery(query, null);
                c.moveToFirst(); // переходим на первую строку
                login = c.getString(c.getColumnIndex(keyLogin));
                c.close();
                dbHelper.close();

                if (login.equals("anonym")) {
                    dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(this, this);
                } else {
                    dbHelper = new DBHelper(this);
                    db = dbHelper.getWritableDatabase();
                    cv = new ContentValues();
                    cv.put(keyCatalogMaster, "usersDetail");
                    //cv.put(keyBack, "home");
                    rowID = db.update(tableName, cv, null, null);
                    //long rowID = db.insert(tableName, keyCatalogMaster, cv);
                    dbHelper.close();

                    intent = new Intent(context, MasterDetaBaseActivity.class);
                    context.startActivity(intent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())
        {
            //    super.onBackPressed();
            moveTaskToBack(true);
            finish();
            System.runFinalizersOnExit(true);
            System.exit(0);
        }
        else
            Toast.makeText(getBaseContext(), "Натисніть ще раз, щоб вийти",
                    Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public void onStart() {
        super.onStart( );

        /*Bundle arguments = getIntent().getExtras();
        RequestJSON_CategoryList categoryList;
        if(arguments!=null){
            categoryList = (RequestJSON_CategoryList) arguments.getSerializable(RequestJSON_CategoryList.class.getSimpleName());

            Toast.makeText(this,
                    "Name: " + categoryList.getCategoryName() + "\nCompany: " +
                            String.valueOf(categoryList.getBoolCheked()) +
                            "\nAge: " + String.valueOf(categoryList.getCategoryID()), Toast.LENGTH_LONG).show();
        }*/
    }
}