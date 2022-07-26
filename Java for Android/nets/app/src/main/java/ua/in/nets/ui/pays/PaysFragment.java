package ua.in.nets.ui.pays;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;

import retrofit.RetrofitError;
import ua.in.nets.FireMissilesDialogFragment;
import ua.in.nets.R;
import ua.in.nets.model.DBRequest;
import ua.in.nets.model.DBRequestJSON;
import ua.in.nets.network.DataService;
import ua.in.nets.requests.GetRequest;

public class PaysFragment extends Fragment {

    private PaysViewModel paysViewModel;
    SpiceManager smConnect = new SpiceManager(DataService.class);
    TextView txtPaysInfo;
    ProgressBar pbPays;
    private boolean mTwoPane = false;
    RecyclerView rvPays;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        paysViewModel =
                ViewModelProviders.of(this).get(PaysViewModel.class);
        View root = inflater.inflate(R.layout.fragment_pays, container, false);
        final TextView textView = root.findViewById(R.id.txt_pays_info);
        paysViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        txtPaysInfo = root.findViewById(R.id.txt_pays_info);
        pbPays = root.findViewById(R.id.pbPays);
        pbPays.setVisibility(View.VISIBLE);
        rvPays = root.findViewById(R.id.rvPays);

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
            smConnect.execute(new GetRequest("pays_list_noname", data_base64), new GetListener());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        smConnect.shouldStop();
    }

    private void setupRVPays(@NonNull RecyclerView recyclerView, List<DBRequestJSON> dbItems) {
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_pays, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

            holder.mPaysID.setText("id в БД: " + mValues.get(position).paysID);
            holder.mPaysDate.setText(mValues.get(position).paysDates);
            holder.mPaysNumDoc.setText("Номер платежа: " + mValues.get(position).paysNumDoc);
            holder.mPaysType.setText(mValues.get(position).paysType);
            holder.mPaysAmount.setText("Сумма платежа: " + mValues.get(position).paysAmount + " грн.");
            holder.mPaysDescription.setText(mValues.get(position).paysDescription);

            //holder.itemView.setTag(mValues.get(position));
            //holder.itemView.setOnClickListener(mOnClickListener);

            View.OnClickListener onClickDelete = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pbPays.setVisibility(View.VISIBLE);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("pays_noname_ID", mValues.get(position).paysID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    byte[] data = new byte[0];
                    try {
                        data = jsonObject.toString().getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);
                    smConnect.execute(new GetRequest("pays_delete_noname", data_base64), new GetListener());
                }
            };
            holder.mImgPaysDel.setOnClickListener(onClickDelete);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mPaysAmount;
            final TextView mPaysNumDoc;
            final TextView mPaysDate;
            final TextView mPaysType;
            final TextView mPaysDescription;
            final TextView mPaysID;
            final LinearLayout mLinear;
            final ImageView mImgPaysDel;

            ViewHolder(View view) {
                super(view);
                mLinear = (LinearLayout) view.findViewById(R.id.linearLabel);
                mPaysID = (TextView) view.findViewById(R.id.paysID);
                mPaysAmount = (TextView) view.findViewById(R.id.paysAmount);
                mPaysDate = (TextView) view.findViewById(R.id.paysDate);
                mPaysType = (TextView) view.findViewById(R.id.paysType);
                mPaysNumDoc = (TextView) view.findViewById(R.id.paysNumDoc);
                mPaysDescription = (TextView) view.findViewById(R.id.paysDescription);
                mImgPaysDel = (ImageView) view.findViewById(R.id.imgPaysDelete);
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
                txtPaysInfo.setText("Немає доступу до Інтернету або сервер недоступний!");
                txtPaysInfo.setTextColor(Color.RED);
                pbPays.setVisibility(View.INVISIBLE);
            }
            return;
        }

        @Override
        public void onRequestSuccess(DBRequest dbItems) {

            if (dbItems == null) {
                txtPaysInfo.setText("Не вдалося отримати списки!");
                txtPaysInfo.setTextColor(Color.RED);
                Toast.makeText(getActivity(),
                        "Не вдалося отримати списки!", Toast.LENGTH_LONG).show();
                pbPays.setVisibility(View.INVISIBLE);
                return;
            }

            String chekError;
            chekError = dbItems.dbChekError;

            String data_utf8;
            byte[] data = Base64.decode(dbItems.dbDATA, Base64.DEFAULT);

            DialogFragment newFragment = new FireMissilesDialogFragment();

            switch (chekError) {
                case "-10":
                    txtPaysInfo.setText("Помилка на сервері");
                    txtPaysInfo.setTextColor(Color.RED);
                    pbPays.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(),
                            "Помилка на сервері", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "pays_list_NO":
                    txtPaysInfo.setText("Немає жодного платежу");
                    txtPaysInfo.setTextColor(Color.GREEN);
                    pbPays.setVisibility(View.INVISIBLE);
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    Toast.makeText(getActivity(),
                            "Немає жодного платежу", Toast.LENGTH_LONG).show();
                    break;

                case "pays_list_YES":
                    //Log.d(TAG, "Посмотрели все тикеты" + chekError);

                    //newFragment.show(getActivity().getSupportFragmentManager(), "missiles");

                    try {
                        data_utf8 = new String (data, "UTF-8");

                        Type itemsListType = new TypeToken<List<DBRequestJSON>>() {}.getType();
                        List<DBRequestJSON> arrayPays;
                        arrayPays = new Gson().fromJson(data_utf8, itemsListType);

                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1,
                                LinearLayoutManager.VERTICAL);
                        rvPays.setLayoutManager(layoutManager);
                        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(rvPays.getContext(),
                                layoutManager.getOrientation());
                        rvPays.addItemDecoration(dividerItemDecoration);
                        setupRVPays((RecyclerView) rvPays, arrayPays);

                        //copyCatalog = arrayCatalog;
                        //txtPaysInfo.setText("успішно, data: " + arrayPays.get(0).userFIO);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    txtPaysInfo.setText("Список не знайдених платежів");
                    txtPaysInfo.setTextColor(Color.GREEN);
                    pbPays.setVisibility(View.INVISIBLE);
                    break;

                default:
                    //Log.d(TAG, "Вы ещё не создавали тикеты: " + chekError);
                    txtPaysInfo.setText("Невідома помилка!");
                    pbPays.setVisibility(View.INVISIBLE);
                    txtPaysInfo.setTextColor(Color.RED);

                    Toast.makeText(getActivity(),
                            "Невідома помилка!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}