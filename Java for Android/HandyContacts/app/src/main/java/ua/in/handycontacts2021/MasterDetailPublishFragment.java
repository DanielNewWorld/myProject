package ua.in.handycontacts2021;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;

import retrofit.RetrofitError;
import ua.in.handycontacts2021.model.DBHelper;
import ua.in.handycontacts2021.model.DBRequest;
import ua.in.handycontacts2021.model.RequestJSON_CatalogList;
import ua.in.handycontacts2021.network.DataService;
import ua.in.handycontacts2021.requests.GetRequest;

import static ua.in.handycontacts2021.model.DBHelper.keyCatalogID;
import static ua.in.handycontacts2021.model.DBHelper.keyLogin;
import static ua.in.handycontacts2021.model.DBHelper.keyPass;
import static ua.in.handycontacts2021.model.DBHelper.tableName;

/**
 * A fragment representing a single Item detail screen.
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class MasterDetailPublishFragment extends Fragment {

    Button btnPublish;
    ProgressBar pbMasterNewCatalog;
    SpiceManager smAdd = new SpiceManager(DataService.class);
    TextView txtMasterInfo;
    int catalogID = 0;
    //private boolean mTwoPane = false;
    String login = "", pass = "";
    int access = 0;

    //public static final String ARG_catalogID = "catalog_id";
    //public static final String ARG_catalogTerritory = "catalog_territory";
    //public static final String ARG_catalogFIO = "catalog_fio";
    //public static final String ARG_userRaiting = "user_raiting";
    //public static final String ARG_catalogDescription = "user_description";
    //public static final String ARG_catalogCatalog = "catalog_catalog";

    //private String catalogTerritory, catalogFIO, catalogDescription, catalogCatalog;
    //private int catalogID, userRaiting;

    public MasterDetailPublishFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if (getArguments().containsKey(ARG_catalogID)) {
        //    catalogID = getArguments().getInt(ARG_catalogID);
        //    catalogTerritory = getArguments().getString(ARG_catalogTerritory);
        //    catalogFIO = getArguments().getString(ARG_catalogFIO);
        //    userRaiting = getArguments().getInt(ARG_userRaiting);
        //    catalogDescription = getArguments().getString(ARG_catalogDescription);
        //    catalogCatalog = getArguments().getString(ARG_catalogCatalog);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.tbMasterNewCatalogTOP);
            if (appBarLayout != null) {
                appBarLayout.setTitle("Заголовок та опис"); }
        //}

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.master_detail_publish, container, false);

        btnPublish = rootView.findViewById(R.id.btnMasterNewCatalogPublish);
        pbMasterNewCatalog = rootView.findViewById(R.id.pbMasterNewCatalog);
        txtMasterInfo = rootView.findViewById(R.id.txtMasterInfo);
        TextView txtBack = rootView.findViewById(R.id.txtBack);

        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + tableName;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst(); // переходим на первую строку
        // извлекаем данные из курсора
        catalogID = c.getInt(c.getColumnIndex(keyCatalogID));
        login = c.getString(c.getColumnIndex(keyLogin));
        pass = c.getString(c.getColumnIndex(keyPass));
        c.close();
        dbHelper.close();

        View.OnClickListener onClickBack = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper;
                SQLiteDatabase db;
                long rowID;
                ContentValues cv;

                dbHelper = new DBHelper(getActivity());
                // создаем объект для данных
                cv = new ContentValues();
                // подключаемся к БД
                db = dbHelper.getWritableDatabase();
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                cv.put(dbHelper.keyCatalogMaster, "country");
                //cv.put(keyBack, "publish");
                //cv.put("dbBalance", String.valueOf(roundUp(dbItems.dbBalance, 2)));
                // вставляем запись и получаем ее ID
                rowID = db.update(tableName, cv, null, null);
                //long rowID = db.insert(tableName[0], null, cv);
                //Log.d(TAG, "row inserted, ID = " + rowID);
                // закрываем подключение к БД
                dbHelper.close();

                Context context = v.getContext();
                Intent intent = new Intent(context, MasterDetailActivity.class);
                context.startActivity(intent);
            }};
        txtBack.setOnClickListener(onClickBack);

        View.OnClickListener onClickPublish = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper;
                SQLiteDatabase db;
                dbHelper = new DBHelper(getContext());
                db = dbHelper.getReadableDatabase();
                String query = "SELECT * FROM " + tableName;
                Cursor c = db.rawQuery(query, null);
                c.moveToFirst(); // переходим на первую строку
                login = c.getString(c.getColumnIndex(keyLogin));
                pass = c.getString(c.getColumnIndex(keyPass));
                c.close();
                dbHelper.close();

                if (login.equals("anonym")) {
                    DialogAutorization dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(getContext(), getActivity());
                } else {
                    pbMasterNewCatalog.setVisibility(View.VISIBLE);
                    txtMasterInfo.setText("Йде оновлення...");
                    txtMasterInfo.setTextColor(Color.GREEN);

                    try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("catalogID", catalogID);
                    jsonObject.put("login", login);
                    jsonObject.put("pass", pass);
                    jsonObject.put("access", access);
                    byte[] data = jsonObject.toString().getBytes("UTF-8");
                    String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);

                    smAdd.execute(new GetRequest("publish", data_base64), new GetAddListener());
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();}
                }
            }
        };
        btnPublish.setOnClickListener(onClickPublish);

        //if (catalogDescription != null) {
        //    ((TextView) rootView.findViewById(R.id.txtDetailDescription)).setText(catalogDescription);}

        if (catalogID == 0) {
            txtMasterInfo.setText("Помилка. Каталог не знайденний!");
            txtMasterInfo.setTextColor(Color.RED);
        } else
        {
            txtMasterInfo.setText("Йде оновлення...");
            txtMasterInfo.setTextColor(Color.GREEN);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("catalogID", catalogID);
                jsonObject.put("login", login);
                jsonObject.put("pass", pass);
                jsonObject.put("show", "all");

            byte[] data = jsonObject.toString().getBytes("UTF-8");
            String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);
            smAdd.execute(new GetRequest("catalog_list", data_base64),
                    new GetAddListener());
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();}
        }
        return rootView;
    }

    class GetAddListener implements RequestListener<DBRequest> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            RetrofitError cause = (RetrofitError) spiceException.getCause();
            if (cause == null ||
                    cause.isNetworkError() ||
                    cause.getResponse() == null) {
                txtMasterInfo.setText("Немає доступу до Інтернету або сервер недоступний!");
                txtMasterInfo.setTextColor(Color.RED);
                Toast.makeText(getActivity(),
                        "Немає доступу до Інтернету або сервер недоступний!", Toast.LENGTH_LONG).show();
                pbMasterNewCatalog.setVisibility(View.INVISIBLE);
            }
            return;
        }

        @Override
        public void onRequestSuccess(DBRequest dbItems) {

            if (dbItems == null) {
                txtMasterInfo.setText("Не вдалося! Повторіть");
                txtMasterInfo.setTextColor(Color.RED);
                Toast.makeText(getActivity(),
                        "Не вдалося! Повторіть", Toast.LENGTH_LONG).show();
                pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                return;
            }

            String chekError;
            chekError = dbItems.dbChekError;

            String data_utf8;
            byte[] data = Base64.decode(dbItems.dbDATA, Base64.DEFAULT);

            switch (chekError) {
                case "-10":
                    txtMasterInfo.setText("Помилка на сервері");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(),
                            "Помилка на сервері", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "publish_NO":
                    txtMasterInfo.setText("Помилка! Не визначений каталог");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Помилка! Не визначений каталог", Toast.LENGTH_LONG).show();
                    break;

                case "publish_NO_category":
                    txtMasterInfo.setText("Оберить категорію!");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Оберить категорію!", Toast.LENGTH_LONG).show();
                    break;

                case "publish_NO_territory":
                    txtMasterInfo.setText("Оберіть терріторію роботи!");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Оберіть терріторію роботи!", Toast.LENGTH_LONG).show();
                    break;

                case "publish_NO_phone":
                    txtMasterInfo.setText("Вкажіть контактні дані!");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Вкажіть контактні дані!", Toast.LENGTH_LONG).show();
                    break;

                case "publish_YES":
                    //Log.d(TAG, "Посмотрели все тикеты" + chekError);
                    //newFragment.show(getActivity().getSupportFragmentManager(), "missiles");
                    txtMasterInfo.setText("Оновлено!");
                    txtMasterInfo.setTextColor(Color.GREEN);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(),
                            "Оновлено!", Toast.LENGTH_LONG).show();
                    break;

                case "catalog_list_YES":
                    //Log.d(TAG, "Посмотрели все тикеты" + chekError);
                    //newFragment.show(getActivity().getSupportFragmentManager(), "missiles");
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    txtMasterInfo.setText("");

                    try {
                        data_utf8 = new String (data, "UTF-8");

                        Type itemsListType = new TypeToken<List<RequestJSON_CatalogList>>() {}.getType();
                        List<RequestJSON_CatalogList> arrayCatalog;
                        arrayCatalog = new Gson().fromJson(data_utf8, itemsListType);
                        access = arrayCatalog.get(0).catalogPublish;
                        if (arrayCatalog.get(0).catalogPublish == 0) {
                            txtMasterInfo.setText("Об'ява не опублікована");
                            btnPublish.setText("Опублікувати");}
                        else {
                            txtMasterInfo.setText("Об'ява опублікована");
                            btnPublish.setText("Видалити з публікації");}
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        //} catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "no_login_pass":
                    txtMasterInfo.setText("Логін або пароль не вірний!");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),
                            "Логін або пароль не вірний!", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "no_id":
                    txtMasterInfo.setText("Ви не можете управляти чужим оголошенням!");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),
                            "Ви не можете управляти чужим оголошенням!", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "no_EMAIL":
                    txtMasterInfo.setText("Для цієї дії, Вам потрібно підтвердити свій е-mail!");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),
                            "Для цієї дії, Вам потрібно підтвердити свій е-mail!", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                default:
                    //Log.d(TAG, "Вы ещё не создавали тикеты: " + chekError);
                    Toast.makeText(getActivity(),
                            "Невідома помилка!", Toast.LENGTH_LONG).show();
                    txtMasterInfo.setText("Невідома помилка! " + chekError);
                    txtMasterInfo.setTextColor(Color.RED);
                    break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        smAdd.start(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        smAdd.shouldStop();
    }
}