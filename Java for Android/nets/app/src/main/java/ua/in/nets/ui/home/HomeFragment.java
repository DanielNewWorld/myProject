package ua.in.nets.ui.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
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

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import ua.in.nets.FireMissilesDialogFragment;
import ua.in.nets.R;
import ua.in.nets.model.ArrayListUser;
//import ua.in.nets.dummy.DummyContent;
import ua.in.nets.model.DBRequest;
import ua.in.nets.model.DBRequestJSON;
import ua.in.nets.requests.GetRequest;
import ua.in.nets.network.DataService;
import ua.in.nets.model.DBUser;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    SpiceManager smConnect = new SpiceManager(DataService.class);
    TextView txtUserInfo;
    ProgressBar pbUser;
    private boolean mTwoPane = false;
    RecyclerView rvUser;

    public static ArrayListUser ITEMS = new ArrayListUser();
    public static Map<String, DBUser> ITEM_MAP = new HashMap<String, DBUser>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        txtUserInfo = root.findViewById(R.id.txt_user_info);
        pbUser = root.findViewById(R.id.pbUser);
        pbUser.setVisibility(View.VISIBLE);

        //homeViewModel.getText().observe(this, new Observer<String>() {
        //    @Override
        //    public void onChanged(@Nullable String s) {
        //        textView.setText("Администратор: " + admin);
        //    }
        //});

        if (root.findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            //mTwoPane = true;
        }

        rvUser = root.findViewById(R.id.rvUser);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        smConnect.start(getActivity());

        try {
            JSONObject jsonObject = new JSONObject();
            //jsonObject.put("login", inpMasterNewCatalogName.getText().toString());
            //jsonObject.put("pass", inpMasterNewCatalogDescription.getText().toString());

            byte[] data = jsonObject.toString().getBytes("UTF-8");
            String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);
            smConnect.execute(new GetRequest("user_list", data_base64), new GetListener());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        smConnect.shouldStop();
    }

    private void setupRVUser(@NonNull RecyclerView recyclerView, List<DBRequestJSON> dbItems) {
        Activity activity = this.getActivity();
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(activity, dbItems, mTwoPane));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final Activity mParentActivity;
        private final List<DBRequestJSON> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                //DBUser item = (DBUser) view.getTag();
                if (mTwoPane) {
                    //Bundle arguments = new Bundle();
                    //arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.get(1).id);
                    //ItemDetailFragment fragment = new ItemDetailFragment();
                    //fragment.setArguments(arguments);
                    //mParentActivity.getSupportFragmentManager().beginTransaction()
                    //        .replace(R.id.item_detail_container, fragment)
                    //        .commit();
                } else {
                    Context context = view.getContext();
                    //int viewID = item.id;
                    //Intent intent = new Intent(context, ItemDetailActivity.class);
                    //intent.putExtra(ItemDetailFragment.ARG_ITEM_ID,"dd");
                    //context.startActivity(intent);
                    //Toast.makeText(context, "aa"+viewID, Toast.LENGTH_SHORT).show();
                }
            }
        };

        SimpleItemRecyclerViewAdapter(Activity parent,
                                      List<DBRequestJSON> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @NonNull
        @Override
        public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_users, parent, false);
            return new SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {

            float Balance;

            Balance = mValues.get(position).userBALANCE;

            holder.mUserLoginView.setText(mValues.get(position).userLogin);
            holder.mGRPView.setText(mValues.get(position).userGRP);
            holder.mFIOView.setText(mValues.get(position).userFIO);
            holder.mContractView.setText(mValues.get(position).userContract);
            holder.mContractDateView.setText("Дата контракта: "+mValues.get(position).userContractDate);
            holder.mUserIPView.setText(mValues.get(position).userIP);
            holder.mUserStateView.setText(mValues.get(position).userSTATE);
            holder.mUserBALANCEView.setText("Баланс: " + Balance + " грн.");
            holder.mUserLimitBalanceView.setText("лимит: "+mValues.get(position).userLimitBalance);
            holder.mUserModifyTimeView.setText("Время изменения: "+mValues.get(position).userModifyTime);
            holder.mUserPaketView.setText(""+mValues.get(position).userPaket + " ("+mValues.get(position).userPaketPrice + " грн.)");
            holder.mUserNextPaketView.setText("Следующий тариф: "+mValues.get(position).userNextPaket);
            holder.mUserDiscount.setText("Скидка: "+mValues.get(position).userDiscount + "%");
            holder.mUserComment.setText("Комментарий: "+mValues.get(position).userComment);
            holder.mUserID.setText(mValues.get(position).userID);

            holder.itemView.setTag(mValues.get(position));
            //holder.itemView.setOnClickListener(mOnClickListener);

            switch (mValues.get(position).userGRP_id) {
                case 1:
                    holder.mLinear.setBackgroundColor(Color.GREEN);
                    break;

                case 2:
                    holder.mLinear.setBackgroundColor(Color.RED);
                    break;

                case 3:
                    holder.mLinear.setBackgroundColor(Color.YELLOW);
                    break;

                default:
                    holder.mLinear.setBackgroundColor(Color.WHITE);
                    break;
            }

            if (Balance >= 0) {holder.mUserBALANCEView.setTextColor(Color.GREEN);} else
            {holder.mUserBALANCEView.setTextColor(Color.RED);}

            if (mValues.get(position).userSTATE.equals("on")) {holder.mUserStateView.setTextColor(Color.BLUE);} else
            {holder.mUserStateView.setTextColor(Color.RED);}

            holder.mUserComment.setTextColor(Color.GRAY);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mGRPView;
            final TextView mFIOView;
            final TextView mContractView;
            final TextView mContractDateView;
            final TextView mUserIPView;
            final TextView mUserLoginView;
            final TextView mUserStateView;
            final TextView mUserBALANCEView;
            final TextView mUserLimitBalanceView;
            final TextView mUserModifyTimeView;
            final TextView mUserPaketView;
            final TextView mUserNextPaketView;
            final TextView mUserDiscount;
            final TextView mUserComment;
            final TextView mUserID;
            final LinearLayout mLinear;

            ViewHolder(View view) {
                super(view);
                mLinear = (LinearLayout) view.findViewById(R.id.linearLabel);
                mGRPView = (TextView) view.findViewById(R.id.paysAmount);
                mFIOView = (TextView) view.findViewById(R.id.paysNumDoc);
                mContractView = (TextView) view.findViewById(R.id.usrContract);
                mContractDateView = (TextView) view.findViewById(R.id.usrContractDate);
                mUserIPView = (TextView) view.findViewById(R.id.ip);
                mUserLoginView = (TextView) view.findViewById(R.id.paysDate);
                mUserStateView = (TextView) view.findViewById(R.id.paysType);
                mUserBALANCEView = (TextView) view.findViewById(R.id.usrBALANCE);
                mUserLimitBalanceView = (TextView) view.findViewById(R.id.usrLIMIT_BALANCE);
                mUserModifyTimeView = (TextView) view.findViewById(R.id.usrModifyTime);
                mUserPaketView = (TextView) view.findViewById(R.id.usrPaket);
                mUserNextPaketView = (TextView) view.findViewById(R.id.usrPaketNext);
                mUserDiscount = (TextView) view.findViewById(R.id.usrDiscount);
                mUserComment = (TextView) view.findViewById(R.id.paysDescription);
                mUserID = (TextView) view.findViewById(R.id.paysID);
            }
        }
    }

    class GetListener implements RequestListener<DBRequest> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            RetrofitError cause = (RetrofitError) spiceException.getCause();
            if (cause == null ||
                    cause.isNetworkError() ||
                    cause.getResponse() == null) {

                //txtAddInfo.setText("Немає доступу до Інтернету або сервер недоступний!");
                //txtAddInfo.setTextColor(Color.RED);
                Toast.makeText(getActivity(),
                        "Немає доступу до Інтернету або сервер недоступний!", Toast.LENGTH_LONG).show();
                txtUserInfo.setText("Немає доступу до Інтернету або сервер недоступний!");
                txtUserInfo.setTextColor(Color.RED);
                pbUser.setVisibility(View.INVISIBLE);
            }
            return;
        }

        @Override
        public void onRequestSuccess(DBRequest dbItems) {

            if (dbItems == null) {
                txtUserInfo.setText("Не вдалося отримати списки!");
                txtUserInfo.setTextColor(Color.RED);
                Toast.makeText(getActivity(),
                        "Не вдалося отримати списки!", Toast.LENGTH_LONG).show();
                pbUser.setVisibility(View.INVISIBLE);
                return;
            }

            String chekError;
            chekError = dbItems.dbChekError;

            String data_utf8;
            byte[] data = Base64.decode(dbItems.dbDATA, Base64.DEFAULT);

            DialogFragment newFragment = new FireMissilesDialogFragment();

            switch (chekError) {
                case "-10":
                    txtUserInfo.setText("Помилка на сервері");
                    txtUserInfo.setTextColor(Color.RED);
                    pbUser.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(),
                            "Помилка на сервері", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "user_list_NO":
                    txtUserInfo.setText("Немає жодного клієнта");
                    txtUserInfo.setTextColor(Color.GREEN);
                    pbUser.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Немає жодного клієнта", Toast.LENGTH_LONG).show();
                    break;

                case "user_list_YES":
                    //Log.d(TAG, "Посмотрели все тикеты" + chekError);

                    //newFragment.show(getActivity().getSupportFragmentManager(), "missiles");

                    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1,
                            LinearLayoutManager.VERTICAL);
                    rvUser.setLayoutManager(layoutManager);
                    RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(rvUser.getContext(),
                            layoutManager.getOrientation());
                    rvUser.addItemDecoration(dividerItemDecoration);

                    try {
                        data_utf8 = new String (data, "UTF-8");

                        Type itemsListType = new TypeToken<List<DBRequestJSON>>() {}.getType();
                        List<DBRequestJSON> arrayPays;
                        arrayPays = new Gson().fromJson(data_utf8, itemsListType);

                        setupRVUser((RecyclerView) rvUser, arrayPays);

                        //copyCatalog = arrayCatalog;
                        //txtUserInfo.setText("успішно, data: " + arrayPays.get(0).userFIO);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    txtUserInfo.setText("Список клієнтів");
                    txtUserInfo.setTextColor(Color.GREEN);
                    pbUser.setVisibility(View.INVISIBLE);
                    break;

                default:
                    //Log.d(TAG, "Вы ещё не создавали тикеты: " + chekError);
                    txtUserInfo.setText("Невідома помилка!");
                    txtUserInfo.setTextColor(Color.RED);
                    pbUser.setVisibility(View.INVISIBLE);

                    Toast.makeText(getActivity(),
                            "Невідома помилка!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}