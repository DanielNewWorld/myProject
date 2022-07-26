package ua.in.handycontacts2021.ui.noPublish;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
import ua.in.handycontacts2021.DialogAutorization;
import ua.in.handycontacts2021.ItemDetailActivity;
import ua.in.handycontacts2021.ItemDetailFragment;
import ua.in.handycontacts2021.R;
import ua.in.handycontacts2021.model.DBRequest;
import ua.in.handycontacts2021.model.RequestJSON_CatalogList;
import ua.in.handycontacts2021.network.DataService;
import ua.in.handycontacts2021.requests.GetRequest;

import static ua.in.handycontacts2021.model.DBHelper.keyCatalogID;
import static ua.in.handycontacts2021.model.DBHelper.keyCatalogMaster;
import static ua.in.handycontacts2021.model.DBHelper.keyLogin;
import static ua.in.handycontacts2021.model.DBHelper.keyPass;
import static ua.in.handycontacts2021.model.DBHelper.tableName;

public class NoPublishFragment extends Fragment {

    private NoPublishViewModel noPublishViewModel;
    TextView txtAccount, textView;
    Button btnMasterAddCatalogName;
    ProgressBar loadingProgressBar;
    String login = "anonym", pass = "";
    SpiceManager smConnect = new SpiceManager(DataService.class);
    RecyclerView rvDovidnikList;
    List<RequestJSON_CatalogList> copyList;
    public String chekError;
    private boolean mTwoPane = false;
    int positionList = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        noPublishViewModel =
                ViewModelProviders.of(this).get(NoPublishViewModel.class);
        View root = inflater.inflate(R.layout.fragment_no_publish, container, false);

        //final TextView textView = root.findViewById(R.id.text_gallery);
        //navigatorAddCatalogViewModel.getText().observe(getActivity(), new Observer<String>() {
        //    @Override
        //    public void onChanged(@Nullable String s) {
        //        textView.setText(s);
        //    }
        //});

        btnMasterAddCatalogName = root.findViewById(R.id.btnMasterAddCatalogName);
        txtAccount = getActivity().findViewById(R.id.txtAccount);
        textView = root.findViewById(R.id.text_home);
        loadingProgressBar = root.findViewById(R.id.loading);
        loadingProgressBar.setVisibility(View.VISIBLE);
        rvDovidnikList = root.findViewById(R.id.rvDovidnikList);
        txtAccount = getActivity().findViewById(R.id.txtAccount);

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
                    ContentValues cv = new ContentValues();
                    db = dbHelper.getWritableDatabase();
                    cv.put(keyCatalogID, 0);
                    cv.put(keyCatalogMaster, "name");
                    long rowID = db.update(tableName, cv, null, null);
                    dbHelper.close();

                    Intent intent;
                    intent = new Intent(getContext(), ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_catalogFIO, "Нове оголошення");
                    startActivity(intent);
                }
            }
        };
        btnMasterAddCatalogName.setOnClickListener(onClickAdd);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        smConnect.start(getActivity());

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

        txtAccount.setText("Ви увійшли як " + login);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("login", login);
            jsonObject.put("pass", pass);
            jsonObject.put("catalogID", 0);
            jsonObject.put("access", 0);
            jsonObject.put("show", "user");
            byte[] data = new byte[0];
            data = jsonObject.toString().getBytes("UTF-8");
            String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);
            smConnect.execute(new GetRequest("catalog_list", data_base64), new GetListener());
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        smConnect.shouldStop();
    }

    class GetListener implements RequestListener<DBRequest> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            RetrofitError cause = (RetrofitError) spiceException.getCause();
            if (cause == null ||
                    cause.isNetworkError() ||
                    cause.getResponse() == null) {

                textView.setText("Немає доступу до Інтернету або сервер недоступний!");
                textView.setTextColor(Color.RED);
                Toast.makeText(getActivity(),
                        "Немає доступу до Інтернету або сервер недоступний!", Toast.LENGTH_LONG).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
            return;
        }

        @Override
        public void onRequestSuccess(DBRequest dbItems) {

            if (dbItems == null) {
                textView.setText("Не вдалося отримати списки!");
                textView.setTextColor(Color.RED);
                Toast.makeText(getActivity(),
                        "Не вдалося отримати списки!", Toast.LENGTH_LONG).show();
                loadingProgressBar.setVisibility(View.GONE);
                return;
            }

            chekError = dbItems.dbChekError;

            String data_utf8;
            byte[] data = Base64.decode(dbItems.dbDATA, Base64.DEFAULT);

            //DialogFragment newFragment = new FireMissilesDialogFragment();
            StaggeredGridLayoutManager layoutManager5;
            RecyclerView.ItemDecoration dividerItemDecoration5;

            switch (chekError) {
                case "-10":
                    textView.setText("Помилка на сервері");
                    textView.setTextColor(Color.RED);
                    loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),
                            "Помилка на сервері", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "no_login_pass":
                    textView.setText("Логін або пароль не вірний!");
                    textView.setTextColor(Color.RED);
                    loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),
                            "Логін або пароль не вірний!", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "no_id":
                    textView.setText("Ви не можете управляти чужим оголошенням!");
                    textView.setTextColor(Color.RED);
                    loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),
                            "Ви не можете управляти чужим оголошенням!", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "catalog_list_NO":
                    textView.setText("Немає неопублікованних оголошень вашого аккаунту!");
                    textView.setTextColor(Color.GREEN);
                    loadingProgressBar.setVisibility(View.INVISIBLE);

                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Немає неопублікованних оголошень вашого аккаунту!", Toast.LENGTH_LONG).show();
                    break;

                case "no_EMAIL":
                    textView.setText("Для цієї дії, Вам потрібно підтвердити свій е-mail!");
                    textView.setTextColor(Color.RED);
                    loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),
                            "Для цієї дії, Вам потрібно підтвердити свій е-mail!", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "catalog_list_YES":
                    //Log.d(TAG, "Посмотрели все тикеты" + chekError);
                    //newFragment.show(getSupportFragmentManager(), "missiles");
                    textView.setText("");
                    textView.setTextColor(Color.GREEN);

                    try {
                        data_utf8 = new String (data, "UTF-8");

                        Type itemsListType = new TypeToken<List<RequestJSON_CatalogList>>() {}.getType();
                        List<RequestJSON_CatalogList> arrayDovidnik;
                        arrayDovidnik = new Gson().fromJson(data_utf8, itemsListType);
                        copyList = arrayDovidnik;

                        layoutManager5 = new StaggeredGridLayoutManager(1,
                                LinearLayoutManager.VERTICAL);
                        rvDovidnikList.setLayoutManager(layoutManager5);
                        dividerItemDecoration5 = new DividerItemDecoration(rvDovidnikList.getContext(),
                                layoutManager5.getOrientation());
                        rvDovidnikList.addItemDecoration(dividerItemDecoration5);
                        setupRecyclerView((RecyclerView) rvDovidnikList, arrayDovidnik);
                        //txtCatalogInfo.setText("успішно, data: " + data_utf8);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        //} catch (JSONException e) {
                        e.printStackTrace();
                    }

                    loadingProgressBar.setVisibility(View.INVISIBLE);
                    textView.setTextColor(Color.GREEN);
                    break;

                case "user_del_yes":
                    textView.setText("Видалено!");
                    textView.setTextColor(Color.RED);
                    loadingProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(),
                            "Видалено!", Toast.LENGTH_LONG).show();

                    copyList.remove(positionList);

                    layoutManager5 = new StaggeredGridLayoutManager(1,
                            LinearLayoutManager.VERTICAL);
                    rvDovidnikList.setLayoutManager(layoutManager5);
                    dividerItemDecoration5 = new DividerItemDecoration(rvDovidnikList.getContext(),
                            layoutManager5.getOrientation());
                    rvDovidnikList.addItemDecoration(dividerItemDecoration5);
                    setupRecyclerView((RecyclerView) rvDovidnikList, copyList);
                    break;

                default:
                    //Log.d(TAG, "Вы ещё не создавали тикеты: " + chekError);
                    textView.setTextColor(Color.RED);
                    textView.setText("Невідома помилка! " + chekError);
                    Toast.makeText(getActivity(),
                            "Невідома помилка!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<RequestJSON_CatalogList> dbItems) {
        Activity activity = this.getActivity();
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(activity, dbItems, mTwoPane));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final Activity mParentActivity;
        private final List<RequestJSON_CatalogList> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestJSON_CatalogList item = (RequestJSON_CatalogList) view.getTag();
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
                    DBHelper dbHelper = new DBHelper(getContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put(keyCatalogID, item.catalogID);
                    cv.put(keyCatalogMaster, "name");
                    long rowID = db.update(tableName, cv, null, null);
                    //long rowID = db.insert(tableName, keyCatalogMaster, cv);
                    dbHelper.close();

                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(Activity parent,
                                      List<RequestJSON_CatalogList> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @NonNull
        @Override
        public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final SimpleItemRecyclerViewAdapter.ViewHolder holder, final int position) {

            //holder.mIdView.setText(mValues.get(position).id);
            holder.mRatingBar.setRating(mValues.get(position).userRating);
            holder.mFIOView.setText(mValues.get(position).catalogName);
            holder.mCatalogView.setText(mValues.get(position).catalogCategory);
            holder.mTerritoryView.setText(mValues.get(position).catalogTerritory);
            holder.mDescriptionView.setText(mValues.get(position).catalogDescription);
            holder.mtxtUser.setText(mValues.get(position).userLogin);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);

            View.OnClickListener mOnClickDel = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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

                                textView.setText("Йде видалення...");
                                textView.setTextColor(Color.GREEN);
                                loadingProgressBar.setVisibility(View.VISIBLE);

                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("login", login);
                                    jsonObject.put("pass", pass);
                                    jsonObject.put("catalogID", mValues.get(position).catalogID);
                                    byte[] data = new byte[0];
                                    data = jsonObject.toString().getBytes("UTF-8");
                                    String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);
                                    positionList = position;

                                    smConnect.execute(new GetRequest("catalog_list_del", data_base64),
                                            new GetListener());
                                } catch (UnsupportedEncodingException | JSONException e) {
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
            holder.imgListDel.setOnClickListener(mOnClickDel);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final RatingBar mRatingBar;
            final TextView mFIOView, mTerritoryView, mDescriptionView, mCatalogView, mtxtUser;
            final LinearLayout mLinear;
            final ImageView imgListDel;

            ViewHolder(View view) {
                super(view);
                mRatingBar = (RatingBar) view.findViewById(R.id.ratingBar);
                mFIOView = (TextView) view.findViewById(R.id.fio);
                mCatalogView = (TextView) view.findViewById(R.id.txtCatalog);
                mTerritoryView = (TextView) view.findViewById(R.id.territory);
                mDescriptionView = (TextView) view.findViewById(R.id.txtDescription);
                mLinear = (LinearLayout) view.findViewById(R.id.linearLabel);
                imgListDel = (ImageView) view.findViewById(R.id.imgListDel);
                mtxtUser = (TextView) view.findViewById(R.id.txtUser);

                mLinear.setBackgroundColor(Color.GREEN);
            }
        }
    }
}