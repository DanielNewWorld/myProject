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
import ua.in.handycontacts2021.model.RequestJSON_PhoneList;
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
public class DetailPhoneFragment extends Fragment {

    //TextInputEditText inpMasterNewCatalogName, inpMasterNewCatalogDescription;
    ProgressBar pbMasterNewCatalog;
    SpiceManager smAdd = new SpiceManager(DataService.class);
    TextView txtMasterInfo;
    RecyclerView rvMaster;
    ImageView imgAddPhone;
    private boolean mTwoPane = false;
    List<RequestJSON_PhoneList> copyList;
    int catalogID = 0, positionList = 0;
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

    public DetailPhoneFragment() {
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
                appBarLayout.setTitle("Телефон"); }
        //}

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.detail_phone, container, false);

        txtMasterInfo = rootView.findViewById(R.id.txtMasterInfo);
        pbMasterNewCatalog = rootView.findViewById(R.id.pbMasterNewCatalog);
        rvMaster = rootView.findViewById(R.id.rvMaster);
        imgAddPhone = rootView.findViewById(R.id.imgAddPhone);

        View.OnClickListener onClickAddPhone = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    for (int i = 0; i < copyList.size(); i++) {
                        View view = rvMaster.getChildAt(i);
                        TextInputEditText mtvText = (TextInputEditText) view.findViewById(R.id.tvText);
                        TextInputEditText mtvDescription = (TextInputEditText) view.findViewById(R.id.tvDescription);
                        copyList.get(i).phoneName = mtvText.getText().toString();
                        copyList.get(i).phoneDescription = mtvDescription.getText().toString();
                    }

                RequestJSON_PhoneList itemPhone = new RequestJSON_PhoneList("", 0,
                        "", true);
                copyList.add(itemPhone);

                StaggeredGridLayoutManager layoutManager5 = new StaggeredGridLayoutManager(1,
                        LinearLayoutManager.VERTICAL);
                rvMaster.setLayoutManager(layoutManager5);
                RecyclerView.ItemDecoration dividerItemDecoration5 = new DividerItemDecoration(rvMaster.getContext(),
                        layoutManager5.getOrientation());
                rvMaster.addItemDecoration(dividerItemDecoration5);
                setupRVPhone((RecyclerView) rvMaster, copyList);
            }};
        imgAddPhone.setOnClickListener(onClickAddPhone);
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

                case "phone_YES":
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    txtMasterInfo.setText("");
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    //Toast.makeText(Act_menu_add_dovidnik.this,
                    //        "Немає записів в таблиці!", Toast.LENGTH_LONG).show();

                    try {
                        data_utf8 = new String (data, "UTF-8");

                        Type itemsListType = new TypeToken<List<RequestJSON_PhoneList>>() {}.getType();
                        List<RequestJSON_PhoneList> arrayPhone;
                        arrayPhone = new Gson().fromJson(data_utf8, itemsListType);
                        copyList = arrayPhone;

                        StaggeredGridLayoutManager layoutManager5 = new StaggeredGridLayoutManager(1,
                                LinearLayoutManager.VERTICAL);
                        rvMaster.setLayoutManager(layoutManager5);
                        RecyclerView.ItemDecoration dividerItemDecoration5 = new DividerItemDecoration(rvMaster.getContext(),
                                layoutManager5.getOrientation());
                        rvMaster.addItemDecoration(dividerItemDecoration5);
                        setupRVPhone((RecyclerView) rvMaster, copyList);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        //} catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "phone_dublicat":
                    txtMasterInfo.setText("Такий номер вже існує!");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getContext(),
                            "Такий номер вже існує!", Toast.LENGTH_LONG).show();
                    break;

                case "phone_update_yes":
                    txtMasterInfo.setText("Оновлено!");
                    txtMasterInfo.setTextColor(Color.GREEN);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(),
                            "Оновлено!", Toast.LENGTH_LONG).show();

                    try {
                        data_utf8 = new String (data, "UTF-8");

                        Type itemsListType = new TypeToken<List<RequestJSON_PhoneList>>() {}.getType();
                        List<RequestJSON_PhoneList> arrayPhone;
                        arrayPhone = new Gson().fromJson(data_utf8, itemsListType);
                        copyList.get(positionList).phoneID = arrayPhone.get(0).phoneID;
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
                    setupRVPhone((RecyclerView) rvMaster, copyList);
                    break;

                case "phone_del_yes":
                    txtMasterInfo.setText("Видалено!");
                    txtMasterInfo.setTextColor(Color.RED);
                    pbMasterNewCatalog.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(),
                            "Видалено!", Toast.LENGTH_LONG).show();

                    copyList.remove(positionList);

                    StaggeredGridLayoutManager layoutManager5 = new StaggeredGridLayoutManager(1,
                            LinearLayoutManager.VERTICAL);
                    rvMaster.setLayoutManager(layoutManager5);
                    RecyclerView.ItemDecoration dividerItemDecoration5 = new DividerItemDecoration(rvMaster.getContext(),
                            layoutManager5.getOrientation());
                    rvMaster.addItemDecoration(dividerItemDecoration5);
                    setupRVPhone((RecyclerView) rvMaster, copyList);
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

    private void setupRVPhone(@NonNull RecyclerView recyclerView, List<RequestJSON_PhoneList> dbItems) {
        Activity activity = getActivity();
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapterCatalog(activity, dbItems, mTwoPane));
    }

    public class SimpleItemRecyclerViewAdapterCatalog
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapterCatalog.ViewHolder> {

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

        SimpleItemRecyclerViewAdapterCatalog(Activity parent,
                                             List<RequestJSON_PhoneList> items,
                                             boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @NonNull
        @Override
        public SimpleItemRecyclerViewAdapterCatalog.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_update_description, parent, false);
            return new SimpleItemRecyclerViewAdapterCatalog.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final SimpleItemRecyclerViewAdapterCatalog.ViewHolder holder,
                                              final int position) {
            holder.mtvText.setText(mValues.get(position).phoneName);
            holder.mtvDescription.setText(mValues.get(position).phoneDescription);

            if (mValues.get(position).boolChekedDEL) {
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
                    catalogID = c.getInt(c.getColumnIndex(keyCatalogID));
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
                                    TextInputEditText mtvDescription = (TextInputEditText) view.findViewById(R.id.tvDescription);
                                    copyList.get(i).phoneName = mtvText.getText().toString();
                                    copyList.get(i).phoneDescription = mtvDescription.getText().toString();
                                }

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("catalogID", catalogID);
                                jsonObject.put("phoneID", mValues.get(position).phoneID);
                                jsonObject.put("login", login);
                                jsonObject.put("pass", pass);

                                byte[] data = jsonObject.toString().getBytes("UTF-8");
                                String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);

                                positionList = position;
                                smAdd.execute(new GetRequest("phone_del", data_base64),
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
                    catalogID = c.getInt(c.getColumnIndex(keyCatalogID));
                    c.close();
                    dbHelper.close();

                    if (login.equals("anonym")) {
                        DialogAutorization dialogAutorization = new DialogAutorization();
                        dialogAutorization.dialogView(getContext(), getActivity());
                    } else {
                    try {
                        if (catalogID == 0) {
                            txtMasterInfo.setText("Помилка! Не обраний ID довідника! ");
                            txtMasterInfo.setTextColor(Color.RED);
                        } else {
                            txtMasterInfo.setText("Йде оновлення...");
                            txtMasterInfo.setTextColor(Color.GREEN);

                            for (int i = 0; i < copyList.size(); i++) {
                                View view = rvMaster.getChildAt(i);
                                TextInputEditText mtvText = (TextInputEditText) view.findViewById(R.id.tvText);
                                TextInputEditText mtvDescription = (TextInputEditText) view.findViewById(R.id.tvDescription);
                                copyList.get(i).phoneName = mtvText.getText().toString();
                                copyList.get(i).phoneDescription = mtvDescription.getText().toString();
                            }

                            if (mValues.get(position).phoneName.equals("") ) {
                                Toast.makeText(getActivity(), "Ви не ввели номер телефону!",
                                        Toast.LENGTH_LONG).show();
                                txtMasterInfo.setText("Ви не ввели номер телефону!");
                                txtMasterInfo.setTextColor(Color.RED);
                            } else {
                                pbMasterNewCatalog.setVisibility(View.VISIBLE);
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("catalogID", catalogID);
                                jsonObject.put("phoneID", mValues.get(position).phoneID);
                                jsonObject.put("phoneName", mValues.get(position).phoneName);
                                jsonObject.put("phoneDescription", mValues.get(position).phoneDescription);
                                jsonObject.put("login", login);
                                jsonObject.put("pass", pass);

                                byte[] data = jsonObject.toString().getBytes("UTF-8");
                                String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);
                                copyList.get(position).boolChekedDEL = false;

                                positionList = position;
                                smAdd.execute(new GetRequest("phone_update", data_base64),
                                        new GetAddListener());
                            }
                        }
                    } catch (JSONException |
                            UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }}
            };
            holder.mimgSave.setOnClickListener(onClickSave);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final TextInputEditText mtvText;
            final TextInputEditText mtvDescription;
            final ImageView mimgDel;
            final ImageView mimgSave;
            final LinearLayout mll;

            ViewHolder(View view) {
                super(view);
                mtvText = (TextInputEditText) view.findViewById(R.id.tvText);
                mtvDescription = (TextInputEditText) view.findViewById(R.id.tvDescription);
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

                byte[] data = jsonObject.toString().getBytes("UTF-8");
                String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);
                smAdd.execute(new GetRequest("get_phone", data_base64),
                        new GetAddListener());
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();}
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        smAdd.shouldStop();
    }
}