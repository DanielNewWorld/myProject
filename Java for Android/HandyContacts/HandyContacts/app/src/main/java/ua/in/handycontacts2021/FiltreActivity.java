package ua.in.handycontacts2021;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.in.handycontacts2021.model.DBRequest;
import ua.in.handycontacts2021.model.Filtre_class;
import ua.in.handycontacts2021.model.RequestJSON_CategoryList;
import ua.in.handycontacts2021.model.RequestJSON_CountryList;
import ua.in.handycontacts2021.network.APIUploadFile;

import static ua.in.handycontacts2021.network.DataService.BASE_URL;

public class FiltreActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private boolean mTwoPane = false;
    final String[] arrayGroup = new String[] {"Фільтр по категорії",
                                                "Фільтр по країні"};
    List<RequestJSON_CategoryList> arrayCategory;
    List<RequestJSON_CountryList> arrayCountry;

    private static Retrofit retrofit;

    Button btnFilterGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtre);

        btnFilterGo = findViewById(R.id.btnFilterGo);
        View.OnClickListener onClickGo = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        btnFilterGo.setOnClickListener(onClickGo);
    }

    @Override
    public void onStart() {
        super.onStart();

        APIUploadFile service = ItemDetailActivity.RetrofitClientInstance.getRetrofitInstance()
                .create(APIUploadFile.class);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject();
            jsonObject.put("catalogID", 0);
            jsonObject.put("login", "");
            jsonObject.put("pass", "");
            byte[] data2 = jsonObject.toString().getBytes("UTF-8");
            String data_base64 = Base64.encodeToString(data2, Base64.DEFAULT);

            Call<DBRequest> call = service.serviceGET("category_list", data_base64);

            call.enqueue(new Callback<DBRequest>() {
                @Override
                public void onResponse(Call<DBRequest> call,
                                       Response<DBRequest> response) {
                    if(response.isSuccessful()) {
                        DBRequest changesList = response.body();
                        checkErrorRequest(changesList.dbChekError, changesList.dbDATA);
                    } else {
                        Toast.makeText(FiltreActivity.this,
                                response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DBRequest> call, Throwable t) {
                    Toast.makeText(FiltreActivity.this,
                            "Щось пішло не так... Будь ласка, спробуйте пізніше!",
                            Toast.LENGTH_SHORT).show();
                }
            });

        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkErrorRequest (String check, String dbDATA) {
        String data_utf8;
        byte[] data = Base64.decode(dbDATA, Base64.DEFAULT);

        switch (check) {
            case "category_YES": //category_list
                try {
                    data_utf8 = new String (data, "UTF-8");
                    Type itemsListType = new TypeToken<List<RequestJSON_CategoryList>>() {}.getType();
                    arrayCategory = new Gson().fromJson(data_utf8, itemsListType);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                APIUploadFile service = ItemDetailActivity.RetrofitClientInstance.getRetrofitInstance()
                        .create(APIUploadFile.class);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("catalogID", 0);
                    jsonObject.put("login", "");
                    jsonObject.put("pass", "");
                    byte[] data2 = jsonObject.toString().getBytes("UTF-8");
                    String data_base64 = Base64.encodeToString(data2, Base64.DEFAULT);

                    Call<DBRequest> call = service.serviceGET("country_list", data_base64);

                    call.enqueue(new Callback<DBRequest>() {
                        @Override
                        public void onResponse(Call<DBRequest> call,
                                               Response<DBRequest> response) {
                            if(response.isSuccessful()) {
                                DBRequest changesList = response.body();
                                checkErrorRequest(changesList.dbChekError, changesList.dbDATA);
                            } else {
                                Toast.makeText(FiltreActivity.this,
                                        response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<DBRequest> call, Throwable t) {
                            Toast.makeText(FiltreActivity.this,
                                    "Щось пішло не так... Будь ласка, спробуйте пізніше!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "country_YES": //country_list
                try {
                    data_utf8 = new String (data, "UTF-8");
                    Type itemsListType = new TypeToken<List<RequestJSON_CountryList>>() {}.getType();
                    arrayCountry = new Gson().fromJson(data_utf8, itemsListType);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //--
                ExpandableListView listView = (ExpandableListView)findViewById(R.id.expandeble_list);

                //Создаем набор данных для адаптера
                ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
                ArrayList<String> children1 = new ArrayList<String>();
                ArrayList<String> children2 = new ArrayList<String>();

                for (RequestJSON_CategoryList list : arrayCategory) {
                    children1.add(list.categoryName);
                }
                groups.add(children1);

                for (RequestJSON_CountryList list : arrayCountry) {
                    children2.add(list.countryName);
                }
                groups.add(children2);

                //Создаем адаптер и передаем context и список с данными
                ExpListAdapter adapter = new ExpListAdapter(getApplicationContext(), groups, arrayGroup, arrayCategory);
                listView.setAdapter(adapter);
                break;

            case "no_login_pass":
                Toast.makeText(FiltreActivity.this,
                        "Логін або пароль не вірний!", Toast.LENGTH_LONG).show();
                break;

            case "no_id":
                Toast.makeText(FiltreActivity.this,
                        "Ви не можете управляти чужим оголошенням!", Toast.LENGTH_LONG).show();
                break;

            case "no_EMAIL":
                Toast.makeText(FiltreActivity.this,
                        "Для цієї дії, Вам потрібно підтвердити свій е-mail!", Toast.LENGTH_LONG).show();
                break;

            default:
                Toast.makeText(FiltreActivity.this,
                        "Невідома помилка!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public class ExpListAdapter extends BaseExpandableListAdapter {
        private ArrayList<ArrayList<String>> mGroups;
        private Context mContext;
        private String[] mArrayGroup;
        private List<RequestJSON_CategoryList> mArrayCategory;

        public ExpListAdapter (Context context, ArrayList<ArrayList<String>> groups, String[] arrGroup,
                               List<RequestJSON_CategoryList> arrayCategory){
            mContext = context;
            mGroups = groups;
            mArrayGroup = arrGroup;
            mArrayCategory = arrayCategory;
        }

        @Override
        public int getGroupCount() {
            return mGroups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroups.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mGroups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mGroups.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                 ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
            }

            if (isExpanded){
                //Изменяем что-нибудь, если текущая Group раскрыта
            }
            else{
                //Изменяем что-нибудь, если текущая Group скрыта
            }

            TextView textGroup = (TextView) convertView.findViewById(android.R.id.text1);
            //textGroup.setText("Group " + Integer.toString(groupPosition));
            textGroup.setText(mArrayGroup[groupPosition]);

            return convertView;

        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_filtre, null);
            }

            CheckBox checkChild = (CheckBox) convertView.findViewById(R.id.checkBoxFiltre);
            checkChild.setText(mGroups.get(groupPosition).get(childPosition));

            checkChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(mContext, "button is press", Toast.LENGTH_SHORT).show();
                    if (checkChild.isChecked()) {
                        switch (groupPosition) {
                            case 0:
                                arrayCategory.get(childPosition).boolCheked = true;
                                break;

                            case 1:
                                arrayCountry.get(childPosition).boolCheked = true;
                                break;
                        }
                    } else
                    {
                        switch (groupPosition) {
                            case 0:
                                arrayCategory.get(childPosition).boolCheked = false;
                                break;
                            case 1:
                                arrayCountry.get(childPosition).boolCheked = false;
                                break;
                        }
                    }
                }
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    public static class RetrofitClientInstance {
        public static Retrofit getRetrofitInstance() {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60,TimeUnit.SECONDS).build();

            if (retrofit == null) {
                retrofit = new retrofit2.Retrofit.Builder()
                        .baseUrl(BASE_URL).client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*SparseBooleanArray blnRating  = listRating.getCheckedItemPositions();
        for (int i = 0; i < blnRating.size(); i++) {
            int key = blnRating.keyAt(i);
            if (blnRating.get(key)) {

            }
        }*/
    }

    @Override
    public void onBackPressed() {
        JSONArray arrayJSONCategory = new JSONArray();
        JSONArray arrayJSONCountry = new JSONArray();
        try {
            for (RequestJSON_CategoryList list : arrayCategory) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("categoryID", list.categoryID);
                jsonObject.put("boolCheked", list.boolCheked);
                jsonObject.put("categoryName", list.categoryName);
                arrayJSONCategory.put(jsonObject);
            }

            for (RequestJSON_CountryList list : arrayCountry) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("countryID", list.countryID);
                jsonObject.put("boolCheked", list.boolCheked);
                jsonObject.put("countryName", list.countryName);
                arrayJSONCountry.put(jsonObject);
            }

            Filtre_class filtre_class;
            filtre_class = new Filtre_class(true,
                    arrayJSONCategory.toString(), arrayJSONCountry.toString());

            Intent intent = new Intent(FiltreActivity.this, MainActivity.class);
            intent.putExtra("filtre", filtre_class);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finish();
    }
}