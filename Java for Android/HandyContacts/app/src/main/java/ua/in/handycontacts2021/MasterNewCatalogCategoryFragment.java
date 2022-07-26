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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
import ua.in.handycontacts2021.model.RequestJSON_CategoryList;
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
public class MasterNewCatalogCategoryFragment extends Fragment {

    Button btnMasterNewCatalogSave;
    //TextInputEditText inpMasterNewCatalogName, inpMasterNewCatalogDescription;
    ProgressBar pbMasterNewCatalog;
    SpiceManager smAdd = new SpiceManager(DataService.class);
    TextView txtMasterInfo, txtBack;
    ImageView imgAdd;
    RecyclerView rvMasterCategory;
    private boolean mTwoPane = false;
    List<RequestJSON_CategoryList> copyCategory;
    List<RequestJSON_CatalogList> copyArrayCatalog;
    int catalogID = 0;
    String login = "", pass = "";
    int userID = 0;

    //public static final String ARG_catalogID = "catalog_id";
    //public static final String ARG_catalogTerritory = "catalog_territory";
    //public static final String ARG_catalogFIO = "catalog_fio";
    //public static final String ARG_userRaiting = "user_raiting";
    //public static final String ARG_catalogDescription = "user_description";
    //public static final String ARG_catalogCatalog = "catalog_catalog";

    //private String catalogTerritory, catalogFIO, catalogDescription, catalogCatalog;
    //private int catalogID, userRaiting;

    public MasterNewCatalogCategoryFragment() {
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
                appBarLayout.setTitle("Категорія товару або послуги"); }
        //}

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.master_new_catalog_category, container, false);

        btnMasterNewCatalogSave = rootView.findViewById(R.id.btnMasterNewCatalogCategorySave);
        txtMasterInfo = rootView.findViewById(R.id.txtMasterCategoryInfo);
        pbMasterNewCatalog = rootView.findViewById(R.id.pbMasterNewCatalogCategory);
        rvMasterCategory = rootView.findViewById(R.id.rvMasterCategory);
        imgAdd = rootView.findViewById(R.id.imgAdd);
        txtBack = rootView.findViewById(R.id.txtBack);

        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + tableName;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst(); // переходим на первую строку
        // извлекаем данные из курсора
        catalogID = c.getInt(c.getColumnIndex(keyCatalogID));
        login = c.getString(c.getColumnIndex(keyLogin));
        pass = c.getString(c.getColumnIndex(keyPass));
        //String item_content = c.getString(c.getColumnIndex(CatsDataBase.CATNAME));
        c.close();
        dbHelper.close();

        View.OnClickListener onClickAdd = new View.OnClickListener() {
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
                c.close();
                dbHelper.close();

                if (login.equals("anonym")) {
                    DialogAutorization dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(getContext(), getActivity());
                } else {

                dbHelper = new DBHelper(getContext());
                // создаем объект для данных
                ContentValues cv = new ContentValues();
                // подключаемся к БД
                db = dbHelper.getWritableDatabase();
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                cv.put(dbHelper.keyCatalogMaster, "category");
                //cv.put(keyBack, "category");
                //cv.put("dbBalance", String.valueOf(roundUp(dbItems.dbBalance, 2)));
                // вставляем запись и получаем ее ID
                long rowID = db.update(tableName, cv, null, null);
                //long rowID = db.insert(tableName[0], null, cv);
                //Log.d(TAG, "row inserted, ID = " + rowID);
                // закрываем подключение к БД
                dbHelper.close();

                Context context = v.getContext();
                Intent intent = new Intent(context, MasterDetaBaseActivity.class);
                context.startActivity(intent);
            }}
        };
        imgAdd.setOnClickListener(onClickAdd);

        View.OnClickListener onClickSave = new View.OnClickListener() {
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
                c.close();
                dbHelper.close();

                if (login.equals("anonym")) {
                    DialogAutorization dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(getContext(), getActivity());
                } else {
                        boolean chekError = false;
                        JSONObject jsonObject = new JSONObject();
                try {
                        for (RequestJSON_CategoryList i:copyCategory) {
                            if (i.boolCheked) {
                                    jsonObject.put("categoryID", i.categoryID);
                                chekError = true;
                            }}

                        if (chekError) {
                            if (catalogID == 0) {
                                txtMasterInfo.setText("Помилка! Не обрано ID об'яви! ");
                                txtMasterInfo.setTextColor(Color.RED);
                            } else {
                                pbMasterNewCatalog.setVisibility(View.VISIBLE);
                                txtMasterInfo.setText("Йде оновлення...");
                                txtMasterInfo.setTextColor(Color.GREEN);

                                jsonObject.put("catalogID", catalogID);
                                jsonObject.put("login", login);
                                jsonObject.put("pass", pass);

                                byte[] data = jsonObject.toString().getBytes("UTF-8");
                                String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);
                                smAdd.execute(new GetRequest("category_save", data_base64),
                                        new GetAddListener());
                            }
                        } else {
                            Toast.makeText(getActivity(), "Оберіть хоча б одну категорію",
                                                Toast.LENGTH_LONG).show();
                            txtMasterInfo.setText("Оберіть хоча б одну категорію");
                            txtMasterInfo.setTextColor(Color.RED);
                        }
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();}
            }}
        };
        btnMasterNewCatalogSave.setOnClickListener(onClickSave);

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
                cv.put(dbHelper.keyCatalogMaster, "name");
                //cv.put(keyBack, "category");
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

        if (catalogID == 0) {
            txtMasterInfo.setText("Помилка, немає ID каталогу");
            txtMasterInfo.setTextColor(Color.RED);
        } else
        {
            txtMasterInfo.setText("Йде оновлення...");
            txtMasterInfo.setTextColor(Color.GREEN);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("catalogID", catalogID);
                jsonObject.put("login", "");
                jsonObject.put("pass", "");
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
                txtMasterInfo.setText("Не вдалося зберегти! Повторіть");
                txtMasterInfo.setTextColor(Color.RED);
                Toast.makeText(getActivity(),
                        "Не вдалося зберегти! Повторіть", Toast.LENGTH_LONG).show();
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

                case "update_category_NO": //category_update
                    txtMasterInfo.setText("Помилка запису категорії!");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Помилка запису категорії!", Toast.LENGTH_LONG).show();
                    break;

                case "category_NO":
                    txtMasterInfo.setText("Немає жодної категорії!");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Немає жодної категорії!", Toast.LENGTH_LONG).show();
                    break;

                case "catalog_list_NO":
                    txtMasterInfo.setText("Не знайдена ваша об'ява!");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Не знайдена ваша об'ява!", Toast.LENGTH_LONG).show();
                    break;

                case "catalog_list_YES":
                    txtMasterInfo.setText("");
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);

                    try {
                        data_utf8 = new String (data, "UTF-8");
                    Type itemsListType = new TypeToken<List<RequestJSON_CatalogList>>() {}.getType();
                    List<RequestJSON_CatalogList> arrayCatalog;
                    arrayCatalog = new Gson().fromJson(data_utf8, itemsListType);
                    copyArrayCatalog = arrayCatalog;

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("catalogID", 0);
                    jsonObject.put("login", "");
                    jsonObject.put("pass", "");
                    byte[] data2 = jsonObject.toString().getBytes("UTF-8");
                    String data_base64 = Base64.encodeToString(data2, Base64.DEFAULT);

                    smAdd.execute(new GetRequest("category_list", data_base64), new GetAddListener());
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "category_YES":
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    txtMasterInfo.setText("");
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    //Toast.makeText(Act_menu_add_dovidnik.this,
                    //        "Немає записів в таблиці!", Toast.LENGTH_LONG).show();

                    try {
                        data_utf8 = new String (data, "UTF-8");

                        Type itemsListType = new TypeToken<List<RequestJSON_CategoryList>>() {}.getType();
                        List<RequestJSON_CategoryList> arrayCategory;
                        arrayCategory = new Gson().fromJson(data_utf8, itemsListType);

                        for (RequestJSON_CategoryList i:arrayCategory) {
                            if (i.categoryID == copyArrayCatalog.get(0).categoryID) {
                                i.boolCheked = true;
                            }}

                        StaggeredGridLayoutManager layoutManager5 = new StaggeredGridLayoutManager(1,
                                LinearLayoutManager.VERTICAL);
                        rvMasterCategory.setLayoutManager(layoutManager5);
                        RecyclerView.ItemDecoration dividerItemDecoration5 = new DividerItemDecoration(rvMasterCategory.getContext(),
                                layoutManager5.getOrientation());
                        rvMasterCategory.addItemDecoration(dividerItemDecoration5);
                        setupRVCategory((RecyclerView) rvMasterCategory, arrayCategory);

                        copyCategory = arrayCategory;
                        //txtCatalogInfo.setText("успішно, data: " + data_utf8);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        //} catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "save_YES":
                    txtMasterInfo.setText("Збережено!");
                    txtMasterInfo.setTextColor(Color.GREEN);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(),
                            "Збережено!", Toast.LENGTH_LONG).show();

                    long rowID;
                    ContentValues cv;
                    DBHelper dbHelper = new DBHelper(getActivity());
                        // создаем объект для создания и управления версиями БД
                        cv = new ContentValues();
                        // подключаемся к БД
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
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

                    Context context = getActivity();
                    Intent intent = new Intent(context, MasterDetailActivity.class);
                    context.startActivity(intent);
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

    private void setupRVCategory(@NonNull RecyclerView recyclerView, List<RequestJSON_CategoryList> dbItems) {
        Activity activity = getActivity();
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapterCatalog(activity, dbItems, mTwoPane));
    }

    public class SimpleItemRecyclerViewAdapterCatalog
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapterCatalog.ViewHolder> {

        private final Activity mParentActivity;
        private final List<RequestJSON_CategoryList> mValues;
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

        SimpleItemRecyclerViewAdapterCatalog(Activity parent,
                                             List<RequestJSON_CategoryList> items,
                                             boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @NonNull
        @Override
        public SimpleItemRecyclerViewAdapterCatalog.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_no_del_name, parent, false);
            return new SimpleItemRecyclerViewAdapterCatalog.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final SimpleItemRecyclerViewAdapterCatalog.ViewHolder holder, final int position) {
            holder.mtvText.setText(mValues.get(position).categoryName);
            holder.mtvText.setBackgroundColor(Color.WHITE);
            //mValues.get(position).boolCheked = false;
            if (mValues.get(position).boolCheked) {
                //mValues.get(position).boolCheked = true;
                holder.mtvText.setBackgroundColor(Color.GREEN);
            }

            View.OnClickListener onClickCatalog = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mValues.get(position).boolCheked == false) {
                        holder.mtvText.setBackgroundColor(Color.GREEN);

                        for (RequestJSON_CategoryList i: mValues) {
                            i.boolCheked = false;
                        }
                        mValues.get(position).boolCheked = true;

                        //positionRVCountry = position;
                        StaggeredGridLayoutManager layoutManager5 = new StaggeredGridLayoutManager(1,
                                LinearLayoutManager.VERTICAL);
                        rvMasterCategory.setLayoutManager(layoutManager5);
                        RecyclerView.ItemDecoration dividerItemDecoration5 = new DividerItemDecoration(rvMasterCategory.getContext(),
                                layoutManager5.getOrientation());
                        rvMasterCategory.addItemDecoration(dividerItemDecoration5);
                        setupRVCategory((RecyclerView) rvMasterCategory, mValues);

                        //idCatalog = mValues.get(position).catalogID;
                        //smAdd.execute(new GetAddCityRequest(act[4], "", idCatalog),
                        //        new GetAddCityListener());
                    } else {
                        holder.mtvText.setBackgroundColor(Color.WHITE);
                        mValues.get(position).boolCheked = false;
                    }
                }
            };
            holder.mtvText.setOnClickListener(onClickCatalog);

            //holder.itemView.setOnClickListener(mOnClickListener);
            //holder.getAdapterPosition();
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mtvText;

            ViewHolder(View view) {
                super(view);
                mtvText = (TextView) view.findViewById(R.id.tvText);
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