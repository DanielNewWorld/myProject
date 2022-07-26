package ua.in.handycontacts2021;

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
import ua.in.handycontacts2021.model.RequestJSON_UsersList;
import ua.in.handycontacts2021.network.DataService;
import ua.in.handycontacts2021.requests.GetRequest;

import static ua.in.handycontacts2021.model.DBHelper.keyCatalogMaster;
import static ua.in.handycontacts2021.model.DBHelper.keyLogin;
import static ua.in.handycontacts2021.model.DBHelper.keyPass;
import static ua.in.handycontacts2021.model.DBHelper.keyUserID;
import static ua.in.handycontacts2021.model.DBHelper.tableName;

/**
 * A fragment representing a single Item detail screen.
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class MasterDataBaseUsersDetailFragment extends Fragment {

    Button btnDELUsers, btnExitUsers;
    ProgressBar pbUsers;
    SpiceManager smConnect = new SpiceManager(DataService.class);
    TextView txtLogin, txtStatusID, txtInfo, txtPolicyLink;
    int catalogID = 0;
    String login = "", pass = "";
    List<RequestJSON_UsersList> copyList;

    public MasterDataBaseUsersDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.tbMasterNewCatalogTOP);
            if (appBarLayout != null) {
                appBarLayout.setTitle(login); }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.master_database_users_detail, container, false);

        btnDELUsers = rootView.findViewById(R.id.btnDELUsers);
        btnExitUsers = rootView.findViewById(R.id.btnExitUsers);
        txtLogin = rootView.findViewById(R.id.txtLogin);
        txtPolicyLink = rootView.findViewById(R.id.txtPolicyLink);
        txtStatusID = rootView.findViewById(R.id.txtStatusID);
        txtStatusID.setClickable(false);
        txtInfo = rootView.findViewById(R.id.txtInfo);
        pbUsers = rootView.findViewById(R.id.pbUsers);

        View.OnClickListener onClickPolicy = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper;
                SQLiteDatabase db;
                dbHelper = new DBHelper(getContext());
                ContentValues cv = new ContentValues();
                db = dbHelper.getWritableDatabase();
                cv.put(keyCatalogMaster, "policy");
                //cv.put(keyBack, "usersDetail");
                long rowID = db.update(tableName, cv, null, null);
                dbHelper.close();

                Context context = v.getContext();
                Intent intent = new Intent(context, MasterDetaBaseActivity.class);
                context.startActivity(intent);
            }
        };
        txtPolicyLink.setOnClickListener(onClickPolicy);

        View.OnClickListener onClickStatus = new View.OnClickListener() {
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
                    pbUsers.setVisibility(View.VISIBLE);
                    txtInfo.setText("Йде відправлення листа на Вашу пошту...");
                    txtInfo.setTextColor(Color.GREEN);

                    try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("catalogID", 0);
                    jsonObject.put("login", login);
                    jsonObject.put("pass", pass);
                    byte[] data = jsonObject.toString().getBytes("UTF-8");
                    String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);
                    smConnect.execute(new GetRequest("mail_send", data_base64), new GetAddListener());
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();}
                }
            }
        };
        txtStatusID.setOnClickListener(onClickStatus);

        btnExitUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper;
                SQLiteDatabase db;
                ContentValues cv;
                long rowID;
                dbHelper = new DBHelper(getContext());
                db = dbHelper.getWritableDatabase();
                cv = new ContentValues();
                cv.put(keyLogin, "anonym");
                cv.put(keyPass, "");
                cv.put(keyUserID, 0);
                rowID = db.update(tableName, cv, null, null);
                //long rowID = db.insert(tableName, keyCatalogMaster, cv);
                dbHelper.close();

                txtLogin.setText("Ви вийшли з аккаунту!");
                txtLogin.setTextColor(Color.RED);
                txtStatusID.setText("Ви вийшли з аккаунту!");
                txtStatusID.setTextColor(Color.RED);
            }
        });

        View.OnClickListener onClickDEL = new View.OnClickListener() {
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
                    TextView txtDelInfo = (TextView) dialogDel.findViewById(R.id.txtInfo);

                    txtDelInfo.setText("Ваш аккаунт буде видалено безповоротно!");

                    btnYES.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogDel.dismiss();

                            pbUsers.setVisibility(View.VISIBLE);
                            txtInfo.setText("Йде видалення...");
                            txtInfo.setTextColor(Color.GREEN);

                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("catalogID", 0);
                                jsonObject.put("login", login);
                                jsonObject.put("pass", pass);
                                byte[] data = jsonObject.toString().getBytes("UTF-8");
                                String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);
                                smConnect.execute(new GetRequest("users_del", data_base64), new GetAddListener());
                            } catch (UnsupportedEncodingException | JSONException e) {
                                e.printStackTrace();}
                        }
                    });

                    btnNO.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogDel.dismiss();
                        }
                    });
                }
            }
        };
        btnDELUsers.setOnClickListener(onClickDEL);

        return rootView;
    }

    class GetAddListener implements RequestListener<DBRequest> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            RetrofitError cause = (RetrofitError) spiceException.getCause();
            if (cause == null ||
                    cause.isNetworkError() ||
                    cause.getResponse() == null) {

                txtInfo.setText("Немає доступу до Інтернету або сервер недоступний!");
                txtInfo.setTextColor(Color.RED);
                Toast.makeText(getActivity(),
                        "Немає доступу до Інтернету або сервер недоступний!", Toast.LENGTH_LONG).show();
                pbUsers.setVisibility(View.INVISIBLE);
            }
            return;
        }

        @Override
        public void onRequestSuccess(DBRequest dbItems) {

            if (dbItems == null) {
                txtInfo.setText("Не вдалося зберегти! Повторіть");
                txtInfo.setTextColor(Color.RED);
                Toast.makeText(getActivity(),
                        "Не вдалося зберегти! Повторіть", Toast.LENGTH_LONG).show();
                pbUsers.setVisibility(View.INVISIBLE);
                return;
            }

            String chekError;
            chekError = dbItems.dbChekError;

            String data_utf8;
            byte[] data = Base64.decode(dbItems.dbDATA, Base64.DEFAULT);

            switch (chekError) {
                case "-10":
                    txtInfo.setText("Помилка на сервері");
                    txtInfo.setTextColor(Color.RED);
                    pbUsers.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(),
                            "Помилка на сервері", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "user_list_YES":
                    //Log.d(TAG, "Посмотрели все тикеты" + chekError);
                    //newFragment.show(getActivity().getSupportFragmentManager(), "missiles");
                    pbUsers.setVisibility(View.INVISIBLE);
                    txtInfo.setTextColor(Color.GREEN);

                    try {
                        data_utf8 = new String (data, "UTF-8");

                        Type itemsListType = new TypeToken<List<RequestJSON_UsersList>>() {}.getType();
                        List<RequestJSON_UsersList> arrayUsers;
                        arrayUsers = new Gson().fromJson(data_utf8, itemsListType);

                        if (arrayUsers.get(0).admin == 0)
                        {txtInfo.setText("Ви увійшли як продавець");
                        } else {txtInfo.setText("Ви увійшли як адміністратор");}

                        if (arrayUsers.get(0).statusID == 1) {
                            txtStatusID.setText("Ваш e-mail активованний!");
                            txtStatusID.setTextColor(Color.GREEN);
                            txtStatusID.setClickable(false);
                        } else {
                            txtStatusID.setText("Ваш e-mail не активованний! Натисніть, щоб активувати...");
                            txtStatusID.setTextColor(Color.GREEN);
                            txtStatusID.setClickable(true);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        //} catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "no_login_pass":
                    txtInfo.setText("Логін або пароль не вірний!");
                    txtInfo.setTextColor(Color.RED);
                    pbUsers.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),
                            "Логін або пароль не вірний!", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "mail_send_yes":
                    txtInfo.setText("");
                    pbUsers.setVisibility(View.GONE);

                    final Dialog dialogInfo = new Dialog(getContext());
                    dialogInfo.setContentView(R.layout.dialog_info_ok);
                    dialogInfo.show();

                    Button btnYES = (Button) dialogInfo.findViewById(R.id.btnYes);
                    btnYES.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogInfo.dismiss();
                        }
                    });
                    break;

                case "del_dublicat":
                    txtInfo.setText("Цей аккаунт не можливо видалити, бо в ньому є оголошення!");
                    txtInfo.setTextColor(Color.RED);
                    pbUsers.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(),
                            "Цей аккаунт не можливо видалити, бо в ньому є оголошення!", Toast.LENGTH_LONG).show();
                    break;

                case "users_del_yes":
                    txtInfo.setText("Аккаунт видалено");
                    txtInfo.setTextColor(Color.RED);
                    txtLogin.setText("Аккаунт видалено");
                    txtLogin.setTextColor(Color.RED);
                    txtStatusID.setText("Аккаунт видалено");
                    txtStatusID.setTextColor(Color.RED);
                    pbUsers.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(),
                            "Аккаунт видалено!", Toast.LENGTH_LONG).show();

                    btnExitUsers.setClickable(false);
                    btnDELUsers.setClickable(false);

                    DBHelper dbHelper;
                    SQLiteDatabase db;
                    ContentValues cv;
                    long rowID;
                    dbHelper = new DBHelper(getContext());
                    db = dbHelper.getWritableDatabase();
                    cv = new ContentValues();
                    cv.put(keyLogin, "anonym");
                    cv.put(keyPass, "");
                    cv.put(keyUserID, 0);
                    rowID = db.update(tableName, cv, null, null);
                    //long rowID = db.insert(tableName, keyCatalogMaster, cv);
                    dbHelper.close();
                    break;

                default:
                    //Log.d(TAG, "Вы ещё не создавали тикеты: " + chekError);
                    Toast.makeText(getActivity(),
                            "Невідома помилка!", Toast.LENGTH_LONG).show();
                    txtInfo.setText("Невідома помилка! " + chekError);
                    txtInfo.setTextColor(Color.RED);
                    pbUsers.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        smConnect.start(getActivity());

        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + tableName;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst(); // переходим на первую строку
        // извлекаем данные из курсора
        login = c.getString(c.getColumnIndex(keyLogin));
        pass = c.getString(c.getColumnIndex(keyPass));
        c.close();
        dbHelper.close();

        txtLogin.setText(login);

        if (login.equals("anonym")) {
            DialogAutorization dialogAutorization = new DialogAutorization();
            dialogAutorization.dialogView(getContext(), getActivity());
        } else {
            txtInfo.setText("Йде оновлення...");
            txtInfo.setTextColor(Color.GREEN);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("catalogID", 0);
                jsonObject.put("login", login);
                jsonObject.put("pass", pass);

                byte[] data = jsonObject.toString().getBytes("UTF-8");
                String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);
                smConnect.execute(new GetRequest("users_list", data_base64),
                        new GetAddListener());
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();}
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        smConnect.shouldStop();
    }
}