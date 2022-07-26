package ua.in.handycontacts2021;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.LinearLayout;
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
import com.google.android.material.textfield.TextInputEditText;
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
import ua.in.handycontacts2021.model.RequestJSON_CityList;
import ua.in.handycontacts2021.network.DataService;
import ua.in.handycontacts2021.requests.GetRequest;

import static ua.in.handycontacts2021.model.DBHelper.keyCountryID;
import static ua.in.handycontacts2021.model.DBHelper.keyCountryName;
import static ua.in.handycontacts2021.model.DBHelper.keyLogin;
import static ua.in.handycontacts2021.model.DBHelper.keyPass;
import static ua.in.handycontacts2021.model.DBHelper.tableName;

/**
 * A fragment representing a single Item detail screen.
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class MasterDataBaseCityFragment extends Fragment {
    ProgressBar pbMasterNewCatalog;
    SpiceManager smAdd = new SpiceManager(DataService.class);
    TextView txtMasterInfo, txtCityInfo;
    RecyclerView rvMaster;
    ImageView imgAdd;
    private boolean mTwoPane = false;
    List<RequestJSON_CityList> copyList;
    int countryID = 0, positionList = 0;
    String countryName="";
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

    public MasterDataBaseCityFragment() {
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
                appBarLayout.setTitle("Список міст"); }
        //}

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.master_database_city, container, false);

        txtMasterInfo = rootView.findViewById(R.id.txtMasterInfo);
        txtCityInfo = rootView.findViewById(R.id.txtCityInfo);
        pbMasterNewCatalog = rootView.findViewById(R.id.pbMasterNewCatalog);
        rvMaster = rootView.findViewById(R.id.rvMaster);
        imgAdd = rootView.findViewById(R.id.imgAdd);

        View.OnClickListener onClickAdd = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (copyList.size() != 0) {
                    for (int i = 0; i < copyList.size(); i++) {
                        View view = rvMaster.getChildAt(i);
                        TextInputEditText mtvText = (TextInputEditText) view.findViewById(R.id.tvText);
                        copyList.get(i).cityName = mtvText.getText().toString();
                    }
                }

                RequestJSON_CityList itemList = new RequestJSON_CityList (0,
                        "", true, 0, false);
                copyList.add(itemList);

                StaggeredGridLayoutManager layoutManager5 = new StaggeredGridLayoutManager(1,
                        LinearLayoutManager.VERTICAL);
                rvMaster.setLayoutManager(layoutManager5);
                RecyclerView.ItemDecoration dividerItemDecoration5 = new DividerItemDecoration(rvMaster.getContext(),
                        layoutManager5.getOrientation());
                rvMaster.addItemDecoration(dividerItemDecoration5);
                setupRVMaster((RecyclerView) rvMaster, copyList);
            }};
        imgAdd.setOnClickListener(onClickAdd);

            txtMasterInfo.setText("Йде оновлення...");
            txtMasterInfo.setTextColor(Color.GREEN);

        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + tableName;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst(); // переходим на первую строку
        login = c.getString(c.getColumnIndex(keyLogin));
        pass = c.getString(c.getColumnIndex(keyPass));
        countryID = c.getInt(c.getColumnIndex(keyCountryID));
        countryName = c.getString(c.getColumnIndex(keyCountryName));
        c.close();
        dbHelper.close();

        txtCityInfo.setText("Міста країни " + countryName);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("login", "");
            jsonObject.put("pass", "");
            jsonObject.put("countryID", countryID);
            jsonObject.put("catalogID", 0);
            byte[] data = jsonObject.toString().getBytes("UTF-8");
            String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);
            smAdd.execute(new GetRequest("city_list", data_base64), new GetAddListener());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();}
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

                case "city_update_NO":
                    txtMasterInfo.setText("Помилка оновлення! Спробуйте ще раз");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Помилка оновлення! Спробуйте ще раз", Toast.LENGTH_LONG).show();
                    break;

                case "city_dublicat":
                    txtMasterInfo.setText("Такае місто вже існує!");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getContext(),
                            "Таке місто вже існує!", Toast.LENGTH_LONG).show();
                    //smAdd.execute(new GetRequest("category_list", ""), new GetAddListener());
                    break;

                case "city_YES":
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    txtMasterInfo.setText("");
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    //Toast.makeText(Act_menu_add_dovidnik.this,
                    //        "Немає записів в таблиці!", Toast.LENGTH_LONG).show();

                    try {
                        data_utf8 = new String (data, "UTF-8");

                        Type itemsListType = new TypeToken<List<RequestJSON_CityList>>() {}.getType();
                        List<RequestJSON_CityList> arrayList;
                        arrayList = new Gson().fromJson(data_utf8, itemsListType);
                        copyList = arrayList;

                        StaggeredGridLayoutManager layoutManager5 = new StaggeredGridLayoutManager(1,
                                LinearLayoutManager.VERTICAL);
                        rvMaster.setLayoutManager(layoutManager5);
                        RecyclerView.ItemDecoration dividerItemDecoration5 = new DividerItemDecoration(rvMaster.getContext(),
                                layoutManager5.getOrientation());
                        rvMaster.addItemDecoration(dividerItemDecoration5);
                        setupRVMaster((RecyclerView) rvMaster, copyList);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        //} catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "del_dublicat":
                    txtMasterInfo.setText("Це місто не можливо видалити, бо воно використовується в каталозі!");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getContext(),
                            "Це місто не можливо видалити, бо воно використовується в каталозі!", Toast.LENGTH_LONG).show();
                    break;

                case "city_del_yes":
                    txtMasterInfo.setText("Видалено!");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(),
                            "Видалено!", Toast.LENGTH_LONG).show();

                    copyList.remove(positionList);

                    StaggeredGridLayoutManager layoutManager5 = new StaggeredGridLayoutManager(1,
                            LinearLayoutManager.VERTICAL);
                    rvMaster.setLayoutManager(layoutManager5);
                    RecyclerView.ItemDecoration dividerItemDecoration5 = new DividerItemDecoration(rvMaster.getContext(),
                            layoutManager5.getOrientation());
                    rvMaster.addItemDecoration(dividerItemDecoration5);
                    setupRVMaster((RecyclerView) rvMaster, copyList);
                    break;

                case "city_update_yes":
                    txtMasterInfo.setText("Оновлено!");
                    txtMasterInfo.setTextColor(Color.GREEN);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(),
                            "Оновлено!", Toast.LENGTH_LONG).show();

                    try {
                        data_utf8 = new String (data, "UTF-8");
                        Type itemsListType = new TypeToken<List<RequestJSON_CityList>>() {}.getType();
                        List<RequestJSON_CityList> arrayCategory;
                        arrayCategory = new Gson().fromJson(data_utf8, itemsListType);
                        copyList.get(positionList).cityID = arrayCategory.get(0).cityID;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        //} catch (JSONException e) {
                        e.printStackTrace();
                    }

                    StaggeredGridLayoutManager layoutManager6 = new StaggeredGridLayoutManager(1,
                            LinearLayoutManager.VERTICAL);
                    rvMaster.setLayoutManager(layoutManager6);
                    RecyclerView.ItemDecoration dividerItemDecoration6 = new DividerItemDecoration(rvMaster.getContext(),
                            layoutManager6.getOrientation());
                    rvMaster.addItemDecoration(dividerItemDecoration6);
                    setupRVMaster((RecyclerView) rvMaster, copyList);
                    break;

                case "admin_NO":
                    txtMasterInfo.setText("Цю дія може робити тільки адміністратор!");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Цю дія може робити тільки адміністратор!", Toast.LENGTH_LONG).show();
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
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }

    private void setupRVMaster(@NonNull RecyclerView recyclerView, List<RequestJSON_CityList> dbItems) {
        Activity activity = getActivity();
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapterCatalog(activity, dbItems, mTwoPane));
    }

    public class SimpleItemRecyclerViewAdapterCatalog
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapterCatalog.ViewHolder> {

        private final Activity mParentActivity;
        private final List<RequestJSON_CityList> mValues;
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
                                             List<RequestJSON_CityList> items,
                                             boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_update, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final SimpleItemRecyclerViewAdapterCatalog.ViewHolder holder,
                                              final int position) {
            holder.mtvText.setText(mValues.get(position).cityName);
            if (mValues.get(position).boolCheked) {
                holder.mimgSave.setBackgroundColor(Color.GRAY);
            }

            View.OnClickListener onClickDelete = new View.OnClickListener() {
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
                        final Dialog dialogDel = new Dialog(getActivity());
                        dialogDel.setContentView(R.layout.dialog_quest_yes_no);
                        dialogDel.show();

                        Button btnYES = (Button) dialogDel.findViewById(R.id.btnYes);
                        Button btnNO = (Button) dialogDel.findViewById(R.id.btnNO);

                        btnYES.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogDel.dismiss();

                                try {
                                    txtMasterInfo.setText("Йде оновлення...");
                                txtMasterInfo.setTextColor(Color.GREEN);
                                pbMasterNewCatalog.setVisibility(View.VISIBLE);

                                for (int i = 0; i < copyList.size(); i++) {
                                    View view = rvMaster.getChildAt(i);
                                    TextInputEditText mtvText = (TextInputEditText) view.findViewById(R.id.tvText);
                                    copyList.get(i).cityName = mtvText.getText().toString();
                                }

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("cityID", mValues.get(position).cityID);
                                jsonObject.put("login", login);
                                jsonObject.put("pass", pass);
                                jsonObject.put("catalogID", 0);

                                byte[] data = jsonObject.toString().getBytes("UTF-8");
                                String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);
                                positionList = position;
                                smAdd.execute(new GetRequest("city_del", data_base64),
                                        new GetAddListener());
                                } catch(JSONException |
                                        UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        btnNO.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogDel.dismiss();
                            }
                        });
                }}
            };
            holder.mimgDel.setOnClickListener(onClickDelete);

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
                    pass = c.getString(c.getColumnIndex(keyPass));
                    c.close();
                    dbHelper.close();

                    if (login.equals("anonym")) {
                        DialogAutorization dialogAutorization = new DialogAutorization();
                        dialogAutorization.dialogView(getContext(), getActivity());
                    } else {
                    try {
                            txtMasterInfo.setText("Йде оновлення...");
                            txtMasterInfo.setTextColor(Color.GREEN);

                        for (int i = 0; i < copyList.size(); i++) {
                            View view = rvMaster.getChildAt(i);
                            TextInputEditText mtvText = (TextInputEditText) view.findViewById(R.id.tvText);
                            copyList.get(i).cityName = mtvText.getText().toString();
                        }

                        if (mValues.get(position).cityName.equals("") ) {
                            Toast.makeText(getActivity(), "Ви не ввели назву міста!",
                                    Toast.LENGTH_LONG).show();
                            txtMasterInfo.setText("Ви не ввели назву міста!");
                            txtMasterInfo.setTextColor(Color.RED);
                        } else {
                            pbMasterNewCatalog.setVisibility(View.VISIBLE);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("login", login);
                            jsonObject.put("pass", pass);
                            jsonObject.put("catalogID", 0);
                            jsonObject.put("cityID", mValues.get(position).cityID);
                            jsonObject.put("cityName", mValues.get(position).cityName);
                            jsonObject.put("countryID", countryID);

                            byte[] data = jsonObject.toString().getBytes("UTF-8");
                            String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);
                            copyList.get(position).boolCheked = false;
                            positionList = position;
                            smAdd.execute(new GetRequest("city_update", data_base64),
                                    new GetAddListener());
                        }
                    } catch(JSONException |
                            UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }}
                }
            };
            holder.mimgSave.setOnClickListener(onClickSave);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final TextInputEditText mtvText;
            final ImageView mimgDel;
            final ImageView mimgSave;
            final LinearLayout mll;

            ViewHolder(View view) {
                super(view);
                mtvText = (TextInputEditText) view.findViewById(R.id.tvText);
                mimgDel = (ImageView) view.findViewById(R.id.imgDelete);
                mimgSave = (ImageView) view.findViewById(R.id.imgSave);
                mll = (LinearLayout) view.findViewById(R.id.ll);
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