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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit2.Retrofit;
import ua.in.handycontacts2021.model.DBHelper;
import ua.in.handycontacts2021.model.DBRequest;
import ua.in.handycontacts2021.model.RequestJSON_CatalogList;
import ua.in.handycontacts2021.model.RequestJSON_CategoryList;
import ua.in.handycontacts2021.model.RequestJSON_PhoneList;
import ua.in.handycontacts2021.network.DataService;
import ua.in.handycontacts2021.requests.GetRequest;

import static ua.in.handycontacts2021.model.DBHelper.keyCatalogID;
import static ua.in.handycontacts2021.model.DBHelper.keyLogin;
import static ua.in.handycontacts2021.model.DBHelper.keyPass;
import static ua.in.handycontacts2021.model.DBHelper.tableName;
import static ua.in.handycontacts2021.network.DataService.BASE_URL;

/**
 * A fragment representing a single Item detail screen.
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {

    SpiceManager smConnect = new SpiceManager(DataService.class);
    ProgressBar pbDetailPhone, pbItemDetail, pbItemCategory, pbItemDesc, pbDetailPublish;
    TextInputEditText inpDetailName, inpDetailDescription;
    TextView txtDetailPhoneInfo, txtDetailTerritory,
             txtPublish;
    RecyclerView rvDetailPhone;
    ImageView imgNameEdit, imgDescriptionEdit, imgPhoneEdit, imgCountryEdit, imgPhoto1,
                imgNameSave, imgDescriptionSave, imgPhoto2, imgPhoto3;
    Spinner spnCategory;

    private static Retrofit retrofit;

    private boolean mTwoPane = false;
    int catalogID = 0, categoryID = 0, access = 0, selectedSPN = -1;
    String login = "", pass = "";
    Button btnPublish;
    CollapsingToolbarLayout appBarLayout;

    //List<RequestJSON_CatalogList> copyArrayCatalog;

    public static final String ARG_catalogID = "catalog_id";
    public static final String ARG_catalogTerritory = "catalog_territory";
    public static final String ARG_catalogFIO = "catalog_fio";
    public static final String ARG_userRaiting = "user_raiting";
    public static final String ARG_catalogDescription = "user_description";
    public static final String ARG_catalogCatalog = "catalog_catalog";

    private String catalogTerritory, catalogFIO, catalogDescription, catalogCatalog;
    private int userRaiting;

    class GetListener implements RequestListener<DBRequest> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            RetrofitError cause = (RetrofitError) spiceException.getCause();
            if (cause == null ||
                    cause.isNetworkError() ||
                    cause.getResponse() == null) {

                appBarLayout.setTitle("Немає доступу до Інтернету або сервер недоступний!");
                Toast.makeText(getActivity(),
                        "Немає доступу до Інтернету або сервер недоступний!", Toast.LENGTH_LONG).show();
                pbDetailPhone.setVisibility(View.INVISIBLE);
            }
            return;
        }

        @Override
        public void onRequestSuccess(DBRequest dbItems) {

            if (dbItems == null) {
                appBarLayout.setTitle("Не вдалося отримати списки!");
                Toast.makeText(getActivity(),
                        "Не вдалося отримати списки!", Toast.LENGTH_LONG).show();
                pbDetailPhone.setVisibility(View.INVISIBLE);
                pbItemDetail.setVisibility(View.INVISIBLE);
                return;
            }

            String chekError = dbItems.dbChekError;

            String data_utf8;
            byte[] data = Base64.decode(dbItems.dbDATA, Base64.DEFAULT);

            //DialogFragment newFragment = new FireMissilesDialogFragment();
            DBHelper dbHelper;
            SQLiteDatabase db;
            String query;
            Cursor c;

            switch (chekError) {
                case "catalog_category_update_NO":  //catalog_category_update
                    appBarLayout.setTitle("Помилка запису категорії!");
                    pbItemCategory.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Помилка запису категорії!", Toast.LENGTH_LONG).show();
                    break;

                case "catalog_category_update_YES":  //catalog_category_update
                    try {
                        data_utf8 = new String (data, "UTF-8");
                        Type itemsListType = new TypeToken<List<RequestJSON_CatalogList>>() {}.getType();
                        List<RequestJSON_CatalogList> arrayCatalog;
                        arrayCatalog = new Gson().fromJson(data_utf8, itemsListType);
                        categoryID = arrayCatalog.get(0).categoryID;
                        //copyArrayCatalog.get(0).categoryID = arrayCatalog.get(0).categoryID;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    appBarLayout.setTitle("Збережено!");
                    pbItemCategory.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(),
                            "Збережено!", Toast.LENGTH_LONG).show();
                    break;

                case "category_NO": //category_list
                    pbItemCategory.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(),
                            "Немає жодної категорії!", Toast.LENGTH_LONG).show();
                    break;

                case "category_YES":  //category_list
                    pbItemCategory.setVisibility(View.INVISIBLE);

                    try {
                        data_utf8 = new String (data, "UTF-8");

                        Type itemsListType = new TypeToken<List<RequestJSON_CategoryList>>() {}.getType();
                        List<RequestJSON_CategoryList> arrayCategory;
                        arrayCategory = new Gson().fromJson(data_utf8, itemsListType);

                        List<String> arrayCategoryList = new ArrayList<String>();
                        arrayCategoryList.add("Оберіть категорію...");
                        for (RequestJSON_CategoryList i:arrayCategory) {
                            arrayCategoryList.add(i.categoryName);
                            }

                        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>
                                (getActivity(), android.R.layout.simple_spinner_item, arrayCategoryList);
                        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnCategory.setAdapter(adapterCategory);
                        spnCategory.setPrompt("Оберіть категорію...");

                        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                //Toast.makeText(getActivity(), selected, Toast.LENGTH_SHORT).show();
                                DBHelper dbHelper;
                                SQLiteDatabase db;
                                dbHelper = new DBHelper(getContext( ));
                                db = dbHelper.getReadableDatabase( );
                                String query = "SELECT * FROM " + tableName;
                                Cursor c = db.rawQuery(query, null);
                                c.moveToFirst( ); // переходим на первую строку
                                login = c.getString(c.getColumnIndex(keyLogin));
                                pass = c.getString(c.getColumnIndex(keyPass));
                                catalogID = c.getInt(c.getColumnIndex(keyCatalogID));
                                c.close( );
                                dbHelper.close( );

                                //String selected = spnCategory.getSelectedItem( ).toString( );
                                selectedSPN = spnCategory.getSelectedItemPosition();
                                if (categoryID != selectedSPN) {
                                if (catalogID == 0) {
                                    appBarLayout.setTitle("Ви не ввели назву компанії!");
                                } else {
                                    if (login.equals("anonym")) {
                                        DialogAutorization dialogAutorization = new DialogAutorization( );
                                        dialogAutorization.dialogView(getContext( ), getActivity( ));
                                    } else {
                                        if (selectedSPN != 0) {
                                        try {
                                            pbItemCategory.setVisibility(View.VISIBLE);
                                            appBarLayout.setTitle("Йде оновлення...");

                                            JSONObject jsonObject = new JSONObject( );
                                            jsonObject.put("catalogID", catalogID);
                                            jsonObject.put("login", login);
                                            jsonObject.put("pass", pass);
                                            jsonObject.put("categoryID", selectedSPN);

                                            byte[] data = jsonObject.toString( ).getBytes("UTF-8");
                                            String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);
                                            smConnect.execute(new GetRequest("catalog_category_update", data_base64),
                                                    new GetListener());
                                        } catch (JSONException | UnsupportedEncodingException e) {
                                            e.printStackTrace( );
                                        }}
                                    }
                                }}
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    dbHelper = new DBHelper(getContext());
                    db = dbHelper.getReadableDatabase();
                    query = "SELECT * FROM " + tableName;
                    c = db.rawQuery(query, null);
                    c.moveToFirst(); // переходим на первую строку
                    // извлекаем данные из курсора
                    catalogID = c.getInt(c.getColumnIndex(keyCatalogID));
                    login = c.getString(c.getColumnIndex(keyLogin));
                    pass = c.getString(c.getColumnIndex(keyPass));
                    c.close();
                    dbHelper.close();

                    if (catalogID == 0) {
                        appBarLayout.setTitle("Нове оголошення");
                    } else {
                        appBarLayout.setTitle("Йде оновлення...");
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("login", "");
                            jsonObject.put("pass", "");
                            jsonObject.put("catalogID", catalogID);
                            jsonObject.put("access", 0);
                            jsonObject.put("show", "all");

                            data = jsonObject.toString().getBytes("UTF-8");
                            String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);
                            smConnect.execute(new GetRequest("catalog_list", data_base64), new GetListener());
                        } catch (JSONException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case "add_NO":  //catalog_add, catalog_name_update
                    appBarLayout.setTitle("Помилка додавання або оновлення оголошення");
                    pbItemDetail.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Помилка додавання або оновлення оголошення", Toast.LENGTH_LONG).show();
                    break;

                case "add_YES": //catalog_add, catalog_name_update
                    appBarLayout.setTitle("Оголошення додано або оновленно!");
                    Toast.makeText(getActivity(),
                            "Оголошення додано або оновленно!", Toast.LENGTH_LONG).show();
                    pbItemDetail.setVisibility(View.INVISIBLE);

                    inpDetailName.setEnabled(false);
                    inpDetailName.setFocusableInTouchMode(false);
                    imgNameEdit.setVisibility(View.VISIBLE);
                    imgNameSave.setVisibility(View.INVISIBLE);

                    try {
                        data_utf8 = new String (data, "UTF-8");

                        Type itemsListType = new TypeToken<List<RequestJSON_CatalogList>>() {}.getType();
                        List<RequestJSON_CatalogList> arrayCategory;
                        arrayCategory = new Gson().fromJson(data_utf8, itemsListType);
                        //txtMasterInfo.setText("запис" + arrayCategory.get(0).catalogID);

                        dbHelper = new DBHelper(getContext());
                        // создаем объект для создания и управления версиями БД
                        ContentValues cv = new ContentValues();
                        // подключаемся к БД
                        db = dbHelper.getWritableDatabase();
                        // подготовим данные для вставки в виде пар: наименование столбца - значение
                        cv.put(keyCatalogID, arrayCategory.get(0).catalogID);
                        //cv.put("dbBalance", String.valueOf(roundUp(dbItems.dbBalance, 2)));
                        // вставляем запись и получаем ее ID
                        long rowID = db.update(tableName, cv, null, null);
                        //long rowID = db.insert(tableName[0], null, cv);
                        //Log.d(TAG, "row inserted, ID = " + rowID);
                        // закрываем подключение к БД
                        dbHelper.close();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        //} catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "update_desc_NO":  //catalog_desc_update
                    appBarLayout.setTitle("Помилка оновлення опису послуги чи товара");
                    pbItemDesc.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Помилка оновлення опису послуги чи товара", Toast.LENGTH_LONG).show();
                    break;

                case "update_desc_YES": //catalog_desc_update
                    appBarLayout.setTitle("Опис оновленно!");
                    Toast.makeText(getActivity(),
                            "Опис оновленно!", Toast.LENGTH_LONG).show();
                    pbItemDesc.setVisibility(View.INVISIBLE);

                    inpDetailDescription.setEnabled(false);
                    inpDetailDescription.setFocusableInTouchMode(false);
                    imgDescriptionEdit.setVisibility(View.VISIBLE);
                    imgDescriptionSave.setVisibility(View.INVISIBLE);

                    try {
                        data_utf8 = new String (data, "UTF-8");

                        Type itemsListType = new TypeToken<List<RequestJSON_CatalogList>>() {}.getType();
                        List<RequestJSON_CatalogList> arrayCategory;
                        arrayCategory = new Gson().fromJson(data_utf8, itemsListType);
                        //txtMasterInfo.setText("запис" + arrayCategory.get(0).catalogID);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        //} catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "phone_YES":
                    //Log.d(TAG, "Посмотрели все тикеты" + chekError);
                    txtDetailPhoneInfo.setText("");
                    pbDetailPhone.setVisibility(View.INVISIBLE);
                    try {
                        data_utf8 = new String (data, "UTF-8");

                        Type itemsListType = new TypeToken<List<RequestJSON_PhoneList>>() {}.getType();
                        List<RequestJSON_PhoneList> arrayPhone;
                        arrayPhone = new Gson().fromJson(data_utf8, itemsListType);

                        StaggeredGridLayoutManager layoutManager5 = new StaggeredGridLayoutManager(1,
                                LinearLayoutManager.VERTICAL);
                        rvDetailPhone.setLayoutManager(layoutManager5);
                        RecyclerView.ItemDecoration dividerItemDecoration5 =
                                new DividerItemDecoration(rvDetailPhone.getContext(),
                                layoutManager5.getOrientation());
                        rvDetailPhone.addItemDecoration(dividerItemDecoration5);
                        setupRVPhone((RecyclerView) rvDetailPhone, arrayPhone);

                        //txtDetailPhoneInfo.setText("успішно: " + data_utf8);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        //} catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "catalog_list_NO": //catalog_list
                    appBarLayout.setTitle("Немає цього id у каталозі!");
                    pbItemDetail.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(),
                            "Немає цього id у каталозі!", Toast.LENGTH_LONG).show();

                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "catalog_list_YES": //catalog_list
                    //Log.d(TAG, "Посмотрели все тикеты" + chekError);
                    pbItemDetail.setVisibility(View.INVISIBLE);
                    try {
                        data_utf8 = new String (data, "UTF-8");

                        Type itemsListType = new TypeToken<List<RequestJSON_CatalogList>>() {}.getType();
                        List<RequestJSON_CatalogList> arrayCatalog;
                        arrayCatalog = new Gson().fromJson(data_utf8, itemsListType);
                        //copyArrayCatalog = arrayCatalog;
                        categoryID = arrayCatalog.get(0).categoryID;

                        appBarLayout.setTitle(arrayCatalog.get(0).catalogName);
                        inpDetailName.setText(arrayCatalog.get(0).catalogName);
                        inpDetailDescription.setText(arrayCatalog.get(0).catalogDescription);
                        spnCategory.setSelection(arrayCatalog.get(0).categoryID);

                        txtDetailTerritory.setText(arrayCatalog.get(0).catalogTerritory);
                        if (arrayCatalog.get(0).catalogPublish == 0) {
                            txtPublish.setText("Об'ява не опублікована");
                            btnPublish.setText("Опублікувати");
                        }
                          else {
                            txtPublish.setText("Об'ява опублікована");
                            btnPublish.setText("Видалити з публікації");
                        }
                        access = arrayCatalog.get(0).catalogPublish;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        //} catch (JSONException e) {
                        e.printStackTrace();
                    }

                    dbHelper = new DBHelper(getContext());
                    db = dbHelper.getReadableDatabase();
                    query = "SELECT * FROM " + tableName;
                    c = db.rawQuery(query, null);
                    c.moveToFirst(); // переходим на первую строку
                    // извлекаем данные из курсора
                    catalogID = c.getInt(c.getColumnIndex(keyCatalogID));
                    login = c.getString(c.getColumnIndex(keyLogin));
                    pass = c.getString(c.getColumnIndex(keyPass));
                    c.close();
                    dbHelper.close();

                    if (catalogID != 0) {
                        txtDetailPhoneInfo.setText("Оновлення...");
                        txtDetailPhoneInfo.setTextColor(Color.GREEN);
                        pbDetailPhone.setVisibility(View.VISIBLE);
                        //Toast.makeText(getActivity(),
                        //        "Оновлення...", Toast.LENGTH_LONG).show();

                        try {
                            JSONObject jsonObject = new JSONObject( );
                            jsonObject.put("catalogID", catalogID);
                            jsonObject.put("login", "");
                            jsonObject.put("pass", "");

                            byte[] data2 = jsonObject.toString( ).getBytes("UTF-8");
                            String data_base64 = Base64.encodeToString(data2, Base64.DEFAULT);
                            smConnect.execute(new GetRequest("get_phone", data_base64), new GetListener( ));
                        } catch (JSONException e) {
                            e.printStackTrace( );
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace( );
                        }
                    }
                    break;

                case "publish_NO": //publish
                    appBarLayout.setTitle("Помилка! Не визначений каталог");
                    pbDetailPublish.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Помилка! Не визначений каталог", Toast.LENGTH_LONG).show();
                    break;

                case "publish_NO_category": //publish
                    appBarLayout.setTitle("Оберить категорію, щоб опублікувати!");
                    pbDetailPublish.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Оберить категорію, щоб опублікувати!", Toast.LENGTH_LONG).show();
                    break;

                case "publish_NO_territory":  //publish
                    appBarLayout.setTitle("Оберіть терріторію роботи, щоб опублікувати!");
                    pbDetailPublish.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Оберіть терріторію роботи, щоб опублікувати!", Toast.LENGTH_LONG).show();
                    break;

                case "publish_NO_phone":  //publish
                    appBarLayout.setTitle("Вкажіть контактні дані, щоб опублікувати!");
                    pbDetailPublish.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Вкажіть контактні дані, щоб опублікувати!", Toast.LENGTH_LONG).show();
                    break;

                case "publish_YES":  //publish
                    pbDetailPublish.setVisibility(View.INVISIBLE);
                    try {
                        data_utf8 = new String (data, "UTF-8");

                        Type itemsListType = new TypeToken<List<RequestJSON_CatalogList>>() {}.getType();
                        List<RequestJSON_CatalogList> arrayCatalog;
                        arrayCatalog = new Gson().fromJson(data_utf8, itemsListType);
                        //copyArrayCatalog = arrayCatalog;

                        if (arrayCatalog.get(0).catalogPublish == 1) {
                            txtPublish.setText("Об'ява не опублікована");
                            btnPublish.setText("Опублікувати");
                            appBarLayout.setTitle("Видалено з публікації");
                            Toast.makeText(getActivity(),
                                    "Видалено з публікації", Toast.LENGTH_LONG).show();
                        }
                        else {
                            txtPublish.setText("Об'ява опублікована");
                            btnPublish.setText("Видалити з публікації");
                            appBarLayout.setTitle("Об'ява опублікована!");
                            Toast.makeText(getActivity(),
                                    "Об'ява опублікована!", Toast.LENGTH_LONG).show();
                        }
                        access = arrayCatalog.get(0).catalogPublish;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        //} catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "-10":
                    pbItemDetail.setVisibility(View.INVISIBLE);
                    appBarLayout.setTitle("Помилка на сервері");
                    Toast.makeText(getActivity(),
                            "Помилка на сервері", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "no_login_pass":
                    appBarLayout.setTitle("Логін або пароль не вірний!");
                    pbItemDetail.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(),
                            "Логін або пароль не вірний!", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "no_id":
                    appBarLayout.setTitle("Ви не можете управляти чужим оголошенням!");
                    pbItemDetail.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(),
                            "Ви не можете управляти чужим оголошенням!", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "no_EMAIL":
                    appBarLayout.setTitle("Для цієї дії, Вам потрібно підтвердити свій е-mail!");
                    pbItemDetail.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(),
                            "Для цієї дії, Вам потрібно підтвердити свій е-mail!", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                default:
                    appBarLayout.setTitle("Невідома помилка! " + chekError);
                    pbItemDetail.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "Вы ещё не создавали тикеты: " + chekError);
                    Toast.makeText(getActivity(),
                            "Невідома помилка!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private void setupRVPhone(@NonNull RecyclerView recyclerView, List<RequestJSON_PhoneList> dbItems) {
        Activity activity = this.getActivity();
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapterPhone(activity, dbItems, mTwoPane));
    }

    public class SimpleItemRecyclerViewAdapterPhone
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapterPhone.ViewHolder> {

        private final Activity mParentActivity;
        private final List<RequestJSON_PhoneList> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DBTerritory item = (DBTerritory) view.getTag();
                if (mTwoPane) {
                    //Bundle arguments = new Bundle();
                    //arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.userFIO);
                    //arguments.putString(ItemDetailFragment.ARG_ITEM_T, item.userTerritory);

                    //ItemDetailFragment fragment = new ItemDetailFragment();
                    //fragment.setArguments(arguments);
                    //mParentActivity.getSupportFragmentManager().beginTransaction()
                    //        .replace(R.id.item_detail_container, fragment)
                    //        .commit();
                } else {
                    Context context = view.getContext();

                    //Intent intent = new Intent(context, ItemDetailActivity.class);
                    //intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.userFIO);
                    //intent.putExtra(ItemDetailFragment.ARG_ITEM_T, item.userTerritory);
                    //context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapterPhone(Activity parent,
                                             List<RequestJSON_PhoneList> items,
                                             boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @NonNull
        @Override
        public SimpleItemRecyclerViewAdapterPhone.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item4, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final SimpleItemRecyclerViewAdapterPhone.ViewHolder holder, final int position) {
            holder.mtvText.setText(mValues.get(position).phoneName);
            holder.mtvDescription.setText(mValues.get(position).phoneDescription);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mtvText, mtvDescription;

            ViewHolder(View view) {
                super(view);
                mtvText = (TextView) view.findViewById(R.id.tvText);
                mtvDescription = (TextView) view.findViewById(R.id.tvDescription);
            }
        }
    }

    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if (getArguments().containsKey(ARG_catalogID)) {
        //    catalogFIO = getArguments().getString(ARG_catalogFIO);

            //catalogFIO = "fff";
            Activity activity = this.getActivity();
            appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            //if (appBarLayout != null) {
            //    appBarLayout.setTitle(catalogFIO);
            //}
        //}
    }

    @Override
    public void onStart() {
        super.onStart();
        smConnect.start(getActivity());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject();
            jsonObject.put("catalogID", 0);
            jsonObject.put("login", "");
            jsonObject.put("pass", "");
            byte[] data2 = jsonObject.toString().getBytes("UTF-8");
            String data_base64 = Base64.encodeToString(data2, Base64.DEFAULT);

            smConnect.execute(new GetRequest("category_list", data_base64), new GetListener());
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        smConnect.shouldStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        pbDetailPhone = rootView.findViewById(R.id.pbDetailPhone);
        pbDetailPublish = rootView.findViewById(R.id.pbDetailPublish);
        pbItemCategory = rootView.findViewById(R.id.pbItemCategory);
        pbItemDetail = rootView.findViewById(R.id.pbItemDetail);
        pbItemDesc = rootView.findViewById(R.id.pbItemDesc);
        txtDetailPhoneInfo = rootView.findViewById(R.id.txtDetailPhoneInfo);
        rvDetailPhone = rootView.findViewById(R.id.rvDetailPhone);

        imgNameEdit = rootView.findViewById(R.id.imgNameEdit);
        imgDescriptionEdit = rootView.findViewById(R.id.imgDescriptionEdit);
        imgPhoneEdit = rootView.findViewById(R.id.imgPhoneEdit);
        imgCountryEdit = rootView.findViewById(R.id.imgCountryEdit);
        imgPhoto1 = rootView.findViewById(R.id.imgPhoto1);
        imgPhoto2 = rootView.findViewById(R.id.imgPhoto2);
        imgPhoto3 = rootView.findViewById(R.id.imgPhoto3);
        imgNameSave = rootView.findViewById(R.id.imgNameSave);
        imgDescriptionSave = rootView.findViewById(R.id.imgDescriptionSave);

        inpDetailName = rootView.findViewById(R.id.inpDetailName);
        inpDetailDescription = rootView.findViewById(R.id.inpDetailDescription);
        txtDetailTerritory = rootView.findViewById(R.id.txtDetailTerritory);
        btnPublish = rootView.findViewById(R.id.btnPublish);
        txtPublish = rootView.findViewById(R.id.txtDetailPublish);
        spnCategory = rootView.findViewById(R.id.spnCategory);

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
        Picasso.with(getActivity())
                .load(BASE_URL + "/script/hcontacts/uploads/" +
                        String.valueOf(catalogID) + "_adv_photo_1.png")
                .placeholder(R.drawable.photo_no)
                .into(imgPhoto1);

        Picasso.with(getActivity())
                .load(BASE_URL + "/script/hcontacts/uploads/" +
                        String.valueOf(catalogID) + "_adv_photo_2.png")
                .placeholder(R.drawable.photo_no)
                .into(imgPhoto2);

        Picasso.with(getActivity())
                .load(BASE_URL + "/script/hcontacts/uploads/" +
                        String.valueOf(catalogID) + "_adv_photo_3.png")
                .placeholder(R.drawable.photo_no)
                .into(imgPhoto3);

        /*Picasso.with(getActivity())
                .load(BASE_URL + "/script/hcontacts/uploads/" +
                        String.valueOf(catalogID) + "_adv_logo.png")
                .placeholder(R.drawable.avatar_no)
                .into(imgPhoto1);*/

        View.OnClickListener onClickEditName = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login.equals("anonym")) {
                    DialogAutorization dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(getContext(), getActivity());
                } else {
                        inpDetailName.setEnabled(true);
                        inpDetailName.setFocusableInTouchMode(true);
                        imgNameEdit.setVisibility(View.INVISIBLE);
                        imgNameSave.setVisibility(View.VISIBLE);

                }}
        };
        imgNameEdit.setOnClickListener(onClickEditName);

        View.OnClickListener onClickEditSave = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                if (login.equals("anonym")) {
                    DialogAutorization dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(getContext(), getActivity());
                } else {
                    if (inpDetailName.getText().toString().equals("") ) {
                        Toast.makeText(getActivity(), "Ви не ввели назву компанії!",
                                Toast.LENGTH_LONG).show();
                        appBarLayout.setTitle("Ви не ввели назву компанії!");
                    } else {
                        pbItemDetail.setVisibility(View.VISIBLE);
                        appBarLayout.setTitle("Йде оновлення...");

                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("fio", inpDetailName.getText().toString());
                            jsonObject.put("catalogID", catalogID);
                            jsonObject.put("login", login);
                            jsonObject.put("pass", pass);
                            byte[] data = jsonObject.toString().getBytes("UTF-8");
                            String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);

                            if (catalogID == 0) {
                                smConnect.execute(new GetRequest("catalog_add", data_base64), new GetListener());
                            } else {
                                smConnect.execute(new GetRequest("catalog_name_update", data_base64), new GetListener());
                            }
                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();}
                    }
                }}
        };
        imgNameSave.setOnClickListener(onClickEditSave);

        View.OnClickListener onClickEditDescription = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login.equals("anonym")) {
                    DialogAutorization dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(getContext(), getActivity());
                } else {
                    inpDetailDescription.setEnabled(true);
                    inpDetailDescription.setFocusableInTouchMode(true);
                    imgDescriptionEdit.setVisibility(View.INVISIBLE);
                    imgDescriptionSave.setVisibility(View.VISIBLE);
            }}
        };
        imgDescriptionEdit.setOnClickListener(onClickEditDescription);

        View.OnClickListener onClickDescriptionSave = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                if (login.equals("anonym")) {
                    DialogAutorization dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(getContext(), getActivity());
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("description", inpDetailDescription.getText().toString());
                        jsonObject.put("catalogID", catalogID);
                        jsonObject.put("login", login);
                        jsonObject.put("pass", pass);
                        byte[] data = jsonObject.toString().getBytes("UTF-8");
                        String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);

                        if (catalogID == 0) {
                            Toast.makeText(getActivity(), "Ви не ввели назву компанії!",
                                    Toast.LENGTH_LONG).show();
                            appBarLayout.setTitle("Ви не ввели назву компанії!");
                        } else {
                            appBarLayout.setTitle("Йде оновлення...");
                            pbItemDesc.setVisibility(View.VISIBLE);
                            smConnect.execute(new GetRequest("catalog_desc_update", data_base64), new GetListener());
                        }
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();}
                }}
        };
        imgDescriptionSave.setOnClickListener(onClickDescriptionSave);

        View.OnClickListener onClickEditPhone = new View.OnClickListener() {
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
                catalogID = c.getInt(c.getColumnIndex(keyCatalogID));
                c.close();
                dbHelper.close();

                if (login.equals("anonym")) {
                    DialogAutorization dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(getContext(), getActivity());
                } else {
                    if (catalogID == 0) {
                        Toast.makeText(getActivity(), "Ви не ввели назву компанії!",
                                Toast.LENGTH_LONG).show();
                        appBarLayout.setTitle("Ви не ввели назву компанії!");
                    } else {
                        dbHelper = new DBHelper(getContext( ));
                        // создаем объект для данных
                        ContentValues cv = new ContentValues( );
                        // подключаемся к БД
                        db = dbHelper.getWritableDatabase( );
                        // подготовим данные для вставки в виде пар: наименование столбца - значение
                        cv.put(dbHelper.keyCatalogMaster, "phone");
                        //cv.put(keyBack, "itemDetail");
                        //cv.put("dbBalance", String.valueOf(roundUp(dbItems.dbBalance, 2)));
                        // вставляем запись и получаем ее ID
                        long rowID = db.update(tableName, cv, null, null);
                        //long rowID = db.insert(tableName[0], null, cv);
                        //Log.d(TAG, "row inserted, ID = " + rowID);
                        // закрываем подключение к БД
                        dbHelper.close( );

                        Context context = v.getContext( );
                        Intent intent = new Intent(context, MasterDetailActivity.class);
                        context.startActivity(intent);
                    }
            }}
        };
        imgPhoneEdit.setOnClickListener(onClickEditPhone);

        View.OnClickListener onClickEditCountry = new View.OnClickListener() {
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
                catalogID = c.getInt(c.getColumnIndex(keyCatalogID));
                c.close();
                dbHelper.close();

                if (login.equals("anonym")) {
                    DialogAutorization dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(getContext(), getActivity());
                } else {
                    if (catalogID == 0) {
                        Toast.makeText(getActivity(), "Ви не ввели назву компанії!",
                                Toast.LENGTH_LONG).show();
                        appBarLayout.setTitle("Ви не ввели назву компанії!");
                    } else {
                        dbHelper = new DBHelper(getContext( ));
                        // создаем объект для данных
                        ContentValues cv = new ContentValues( );
                        // подключаемся к БД
                        db = dbHelper.getWritableDatabase( );
                        // подготовим данные для вставки в виде пар: наименование столбца - значение
                        cv.put(dbHelper.keyCatalogMaster, "country");
                        //cv.put(keyBack, "itemDetail");
                        //cv.put("dbBalance", String.valueOf(roundUp(dbItems.dbBalance, 2)));
                        // вставляем запись и получаем ее ID
                        long rowID = db.update(tableName, cv, null, null);
                        //long rowID = db.insert(tableName[0], null, cv);
                        //Log.d(TAG, "row inserted, ID = " + rowID);
                        // закрываем подключение к БД
                        dbHelper.close( );

                        Context context = v.getContext( );
                        Intent intent = new Intent(context, MasterDetailActivity.class);
                        context.startActivity(intent);
                    }
            }}
        };
        imgCountryEdit.setOnClickListener(onClickEditCountry);

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
                catalogID = c.getInt(c.getColumnIndex(keyCatalogID));
                c.close();
                dbHelper.close();

                if (login.equals("anonym")) {
                    DialogAutorization dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(getContext(), getActivity());
                } else {
                    if (catalogID == 0) {
                        Toast.makeText(getActivity(), "Ви не ввели назву компанії!",
                                Toast.LENGTH_LONG).show();
                        appBarLayout.setTitle("Ви не ввели назву компанії!");
                    } else {
                        pbDetailPublish.setVisibility(View.VISIBLE);
                        appBarLayout.setTitle("Йде оновлення...");

                        try {
                            JSONObject jsonObject = new JSONObject( );
                            jsonObject.put("catalogID", catalogID);
                            jsonObject.put("login", login);
                            jsonObject.put("pass", pass);
                            jsonObject.put("access", access);
                            byte[] data = jsonObject.toString( ).getBytes("UTF-8");
                            String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);

                            smConnect.execute(new GetRequest("publish", data_base64), new GetListener( ));
                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace( );
                        }
                    }
                }}
        };
        btnPublish.setOnClickListener(onClickPublish);

        imgPhoto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (catalogID == 0) {
                    Toast.makeText(getActivity(), "Ви не ввели назву компанії!",
                            Toast.LENGTH_LONG).show();
                    appBarLayout.setTitle("Ви не ввели назву компанії!");
                } else {
                DBHelper dbHelper;
                SQLiteDatabase db;
                dbHelper = new DBHelper(getActivity());
                db = dbHelper.getReadableDatabase();
                String query = "SELECT * FROM " + tableName;
                Cursor c = db.rawQuery(query, null);
                c.moveToFirst(); // переходим на первую строку
                String login = c.getString(c.getColumnIndex(keyLogin));
                c.close();
                dbHelper.close();

                if (login.equals("anonym")) {
                    DialogAutorization dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(getContext(), getActivity());
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    photoPickerIntent.setType("image/*");
                    getActivity().startActivityForResult(photoPickerIntent, 2);
                }
            }}
        });

        imgPhoto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (catalogID == 0) {
                    Toast.makeText(getActivity(), "Ви не ввели назву компанії!",
                            Toast.LENGTH_LONG).show();
                    appBarLayout.setTitle("Ви не ввели назву компанії!");
                } else {
                DBHelper dbHelper;
                SQLiteDatabase db;
                dbHelper = new DBHelper(getActivity());
                db = dbHelper.getReadableDatabase();
                String query = "SELECT * FROM " + tableName;
                Cursor c = db.rawQuery(query, null);
                c.moveToFirst(); // переходим на первую строку
                String login = c.getString(c.getColumnIndex(keyLogin));
                c.close();
                dbHelper.close();

                if (login.equals("anonym")) {
                    DialogAutorization dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(getContext(), getActivity());
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    photoPickerIntent.setType("image/*");
                    getActivity().startActivityForResult(photoPickerIntent, 3);
                }
            }}
        });

        imgPhoto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (catalogID == 0) {
                    Toast.makeText(getActivity(), "Ви не ввели назву компанії!",
                            Toast.LENGTH_LONG).show();
                    appBarLayout.setTitle("Ви не ввели назву компанії!");
                } else {
                DBHelper dbHelper;
                SQLiteDatabase db;
                dbHelper = new DBHelper(getActivity());
                db = dbHelper.getReadableDatabase();
                String query = "SELECT * FROM " + tableName;
                Cursor c = db.rawQuery(query, null);
                c.moveToFirst(); // переходим на первую строку
                String login = c.getString(c.getColumnIndex(keyLogin));
                c.close();
                dbHelper.close();

                if (login.equals("anonym")) {
                    DialogAutorization dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(getContext(), getActivity());
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    photoPickerIntent.setType("image/*");
                    getActivity().startActivityForResult(photoPickerIntent, 4);
                }
            }}
        });
        return rootView;
    }
}