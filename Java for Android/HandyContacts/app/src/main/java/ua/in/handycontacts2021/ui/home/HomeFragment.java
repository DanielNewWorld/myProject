package ua.in.handycontacts2021.ui.home;

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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;

import retrofit.RetrofitError;
import ua.in.handycontacts2021.model.DBHelper;
import ua.in.handycontacts2021.DialogAutorization;
import ua.in.handycontacts2021.ItemDetailActivity;
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

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private boolean mTwoPane = false;
    TextView textView, txtAccount, txtFiltr;
    RecyclerView rvDovidnikList;
    List<RequestJSON_CatalogList> copyList;
    public String chekError;
    String login = "", pass = "";
    int positionList = 0;
    SpiceManager smConnect = new SpiceManager(DataService.class);
    ProgressBar loadingProgressBar;

    JSONArray jsonCategory = new JSONArray();
    JSONArray jsonCountry = new JSONArray();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        textView = root.findViewById(R.id.text_home);
        txtFiltr = root.findViewById(R.id.txtFiltr);
        loadingProgressBar = root.findViewById(R.id.loading);
        loadingProgressBar.setVisibility(View.VISIBLE);
        rvDovidnikList = root.findViewById(R.id.rvDovidnikList);
        txtAccount = getActivity().findViewById(R.id.txtAccount);

        homeViewModel.getText().observe((LifecycleOwner) getContext(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        if (root.findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            //mTwoPane = true;
        }
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        smConnect.start(getActivity());

        txtFiltr.setText("Без фільтру");
        txtFiltr.setTextColor(Color.GREEN);

        /*Bundle arguments = getActivity().getIntent().getExtras();
        if(arguments!=null){
            Filtre_class filtreList;
            filtreList = (Filtre_class) arguments.getSerializable("filtre");

            List<RequestJSON_CategoryList> arrayCategory;
            List<RequestJSON_CountryList> arrayCountry;

            Type itemsCategory = new TypeToken<List<RequestJSON_CategoryList>>() {}.getType();
            arrayCategory = new Gson().fromJson(filtreList.jsonCategory, itemsCategory);

            Type itemsCountry = new TypeToken<List<RequestJSON_CountryList>>() {}.getType();
            arrayCountry = new Gson().fromJson(filtreList.jsonCountry, itemsCountry);

            String line = "";
            for (RequestJSON_CategoryList list : arrayCategory) {
                //line = line + "\n" + list.boolCheked;
                if (list.boolCheked) {
                    jsonCategory.put(list.categoryID);
                    txtFiltr.setText("Працює фільтр по категорії");
                }
            }

            for (RequestJSON_CountryList list : arrayCountry) {
                line = line + "\n" + list.boolCheked;
                if (list.boolCheked) {
                    jsonCountry.put(list.countryID);
                    txtFiltr.setText("Працює фільтр по категорії");
                }
            }
            Toast.makeText(getActivity(), line, Toast.LENGTH_LONG).show( );
        }*/

        DBHelper dbHelper;
        SQLiteDatabase db;
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + tableName;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst(); // переходим на первую строку
        String login = c.getString(c.getColumnIndex(keyLogin));
        c.close();
        dbHelper.close();

        txtAccount.setText("Ви увійшли як " + login);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("login", "");
            jsonObject.put("pass", "");
            jsonObject.put("catalogID", 0);
            jsonObject.put("access", 1); //опубликованный 1
            jsonObject.put("show", "all");
            jsonObject.put("filter_category", jsonCategory);
            jsonObject.put("filter_country", jsonCountry);
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

                case "user_del_yes":
                    textView.setText("Видалено!");
                    textView.setTextColor(Color.RED);
                    loadingProgressBar.setVisibility(View.INVISIBLE);

                    copyList.remove(positionList);

                    layoutManager5 = new StaggeredGridLayoutManager(1,
                            LinearLayoutManager.VERTICAL);
                    rvDovidnikList.setLayoutManager(layoutManager5);
                    dividerItemDecoration5 = new DividerItemDecoration(rvDovidnikList.getContext(),
                            layoutManager5.getOrientation());
                    rvDovidnikList.addItemDecoration(dividerItemDecoration5);
                    setupRecyclerView((RecyclerView) rvDovidnikList, copyList);
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

                case "no_EMAIL":
                    textView.setText("Для цієї дії, Вам потрібно підтвердити свій е-mail!");
                    textView.setTextColor(Color.RED);
                    loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),
                            "Для цієї дії, Вам потрібно підтвердити свій е-mail!", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "catalog_list_NO":
                    textView.setText("Немає опублікованних оголошень!");
                    textView.setTextColor(Color.GREEN);
                    loadingProgressBar.setVisibility(View.INVISIBLE);

                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Немає опублікованних оголошень!", Toast.LENGTH_LONG).show();
                    break;

                case "catalog_list_YES":
                    //Log.d(TAG, "Посмотрели все тикеты" + chekError);
                    //newFragment.show(getSupportFragmentManager(), "missiles");
                    textView.setText("");

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

                default:
                    //Log.d(TAG, "Вы ещё не создавали тикеты: " + chekError);
                    textView.setTextColor(Color.RED);
                    textView.setText(
                            "Невідома помилка! " + chekError);
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

            //holder.mIdView.setText(mValues.get(position).id);
            holder.mRatingBar.setRating(mValues.get(position).userRating);
            holder.mFIOView.setText(mValues.get(position).catalogName);
            holder.mCatalogView.setText(mValues.get(position).catalogCategory);
            holder.mTerritoryView.setText(mValues.get(position).catalogTerritory);
            holder.mDescriptionView.setText(mValues.get(position).catalogDescription);
            holder.mtxtUser.setText("Продавець " + mValues.get(position).userLogin);
            holder.adv_id.setText("№ " + mValues.get(position).catalogID);

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

                                smConnect.execute(new GetRequest("catalog_list_del", data_base64), new GetListener());
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
            final TextView mFIOView, mTerritoryView, mDescriptionView, mCatalogView, mtxtUser,
                    adv_id;
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
                adv_id = (TextView) view.findViewById(R.id.adv_id);
                mLinear.setBackgroundColor(Color.GREEN);
            }
        }
    }
}