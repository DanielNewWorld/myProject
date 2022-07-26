package ua.in.handycontacts2021;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import ua.in.handycontacts2021.model.RequestJSON_Registration;
import ua.in.handycontacts2021.network.DataService;
import ua.in.handycontacts2021.requests.GetRequest;

import static ua.in.handycontacts2021.model.DBHelper.keyLogin;
import static ua.in.handycontacts2021.model.DBHelper.keyPass;
import static ua.in.handycontacts2021.model.DBHelper.keyUserID;
import static ua.in.handycontacts2021.model.DBHelper.tableName;

public class DialogRegistration {
    Button btnRegYES, btnNO;
    TextView txtInfo, txtAccount;
    ProgressBar pbDialog;
    Dialog dialogAutorization;
    TextInputEditText inpLogin, inpPass, inpPass2;
    ImageView imgSee1, imgSee2;
    SpiceManager smSession = new SpiceManager(DataService.class);
    Context myContext;
    Activity myActivity;

    public void dialogView (final Context context, final Activity activity) {
        dialogAutorization = new Dialog(context);
        dialogAutorization.setContentView(R.layout.dialog_registartion);
        dialogAutorization.show();
        myContext = context;
        myActivity = activity;

    btnNO = (Button) dialogAutorization.findViewById(R.id.btnNO);
    txtInfo = (TextView) dialogAutorization.findViewById(R.id.txtDialogInfo);
    inpLogin = dialogAutorization.findViewById(R.id.inpLogin);
    inpPass = dialogAutorization.findViewById(R.id.inpPass);
    pbDialog = (ProgressBar) dialogAutorization.findViewById(R.id.pbDialog);
    inpPass2 = dialogAutorization.findViewById(R.id.inpPass2);
    btnRegYES = (Button) dialogAutorization.findViewById(R.id.btnRegYes);
    imgSee1 = (ImageView) dialogAutorization.findViewById(R.id.imgSee1);
    imgSee2 = (ImageView) dialogAutorization.findViewById(R.id.imgSee2);
    txtAccount = activity.findViewById(R.id.txtAccount);

    btnNO.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialogAutorization.dismiss();
        }
    });

    imgSee1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inpPass.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    inpPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    inpPass.setSelection(inpPass.getText().length());
                } else {
                    inpPass.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    inpPass.setSelection(inpPass.getText().length());
                }
            }
        });

    imgSee2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inpPass2.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    inpPass2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    inpPass2.setSelection(inpPass2.getText().length());
                } else {
                    inpPass2.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    inpPass2.setSelection(inpPass2.getText().length());
                }
            }
        });

    btnRegYES.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(activity);
                        dialog.setContentView(R.layout.dialog_quest_yes_no);
                        dialog.show();

                        Button btnYES = (Button) dialog.findViewById(R.id.btnYes);
                        Button btnNO = (Button) dialog.findViewById(R.id.btnNO);
                        TextView txtInfo1 = (TextView) dialog.findViewById(R.id.txtInfo);
                        TextView txtInfo2 = (TextView) dialog.findViewById(R.id.txtInfo2);

                        txtInfo1.setText("Зареєструвавшись Ви підтверджуєте надання згоди на обробку Ваших персональних даних.\n\n" +
                                "Ви підтверджуєте, що Ви повідомлений про права, встановлені Законом України «Про захист персональних даних».\n\n" +
                                "Зареєструвавшись Ви даєте згоду на те, що ми маємо право надавати доступ та передавати Ваші персональні дані третім особам без будь-яких додаткових повідомлень.");
                        txtInfo2.setText("Ви згодні?");

                        btnYES.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                        if (inpLogin.getText().toString().equals("") ) {
                            Toast.makeText(context, "Введіть свій e-mail!",
                                    Toast.LENGTH_LONG).show();
                            txtInfo.setText("Введіть свій e-mail!");
                            txtInfo.setTextColor(Color.RED);
                        } else {
                            if (inpPass.getText().toString().equals("")) {
                                Toast.makeText(context, "Введіть свій пароль!",
                                        Toast.LENGTH_LONG).show();
                                txtInfo.setText("Введіть свій пароль!");
                                txtInfo.setTextColor(Color.RED);
                            } else {
                                if (inpPass.getText().toString().equals(inpPass2.getText().toString())) {
                                    pbDialog.setVisibility(View.VISIBLE);
                                    txtInfo.setText("Працюємо...");
                                    txtInfo.setTextColor(Color.GREEN);
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("login", inpLogin.getText().toString());
                                        jsonObject.put("pass", inpPass.getText().toString());
                                        jsonObject.put("catalogID", 0);
                                        byte[] data = jsonObject.toString().getBytes("UTF-8");
                                        String data_base64 = Base64.encodeToString(data, Base64.DEFAULT);

                                        smSession.start(context);
                                        smSession.execute(new GetRequest("registration", data_base64),
                                                new GetListener());
                                    } catch (UnsupportedEncodingException | JSONException e) {
                                        e.printStackTrace();}
                                } else {
                                    Toast.makeText(context, "Паролі не співпадають!",
                                            Toast.LENGTH_LONG).show();
                                    txtInfo.setText("Паролі не співпадають!");
                                    txtInfo.setTextColor(Color.RED);
                                }
                            }}
                            }
                        });
                        btnNO.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                            }
                });
}

    class GetListener implements RequestListener<DBRequest> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            RetrofitError cause = (RetrofitError) spiceException.getCause();
            if (cause == null ||
                    cause.isNetworkError() ||
                    cause.getResponse() == null) {
                txtInfo.setText("Немає доступу до Інтернету або сервер недоступний!");
                txtInfo.setTextColor(Color.RED);
                pbDialog.setVisibility(View.INVISIBLE);
            }
            smSession.shouldStop();
            return;
        }

        @Override
        public void onRequestSuccess(DBRequest dbItems) {
            smSession.shouldStop();

            if (dbItems == null) {
                txtInfo.setText("Не вдалося! Повторіть");
                txtInfo.setTextColor(Color.RED);
                pbDialog.setVisibility(View.INVISIBLE);
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
                    pbDialog.setVisibility(View.INVISIBLE);
                    break;

                case "reg_dublicat":
                    txtInfo.setText("Така эл. пошта вже існує!");
                    txtInfo.setTextColor(Color.RED);
                    pbDialog.setVisibility(View.INVISIBLE);
                    break;

                case "reg_YES":
                    pbDialog.setVisibility(View.INVISIBLE);
                    txtInfo.setText("Підтвердіть свою електронну адресу");

                    try {
                        data_utf8 = new String (data, "UTF-8");

                        Type itemsListType = new TypeToken<List<RequestJSON_Registration>>() {}.getType();
                        List<RequestJSON_Registration> arrayList;
                        arrayList = new Gson().fromJson(data_utf8, itemsListType);

                        DBHelper dbHelper;
                        SQLiteDatabase db;
                        ContentValues cv;
                        long rowID;
                        dbHelper = new DBHelper(myContext);
                        db = dbHelper.getWritableDatabase();
                        cv = new ContentValues();
                        cv.put(keyLogin, inpLogin.getText().toString());
                        cv.put(keyPass, inpPass.getText().toString());
                        cv.put(keyUserID, arrayList.get(0).userID);
                        rowID = db.update(tableName, cv, null, null);
                        dbHelper.close();

                        txtAccount.setText("Ви увійшли як " + inpLogin.getText().toString());
                        dialogAutorization.dismiss();

                        final Dialog dialogInfo = new Dialog(myContext);
                        dialogInfo.setContentView(R.layout.dialog_info_ok);
                        dialogInfo.show();

                        Button btnYES = (Button) dialogInfo.findViewById(R.id.btnYes);
                        btnYES.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogInfo.dismiss();
                            }
                        });

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        //} catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "reg_NO":
                    txtInfo.setText("Помилка реєстрації!");
                    txtInfo.setTextColor(Color.RED);
                    pbDialog.setVisibility(View.INVISIBLE);
                    break;

                case "no_login_pass":
                    txtInfo.setText("Логін або пароль не вірний!");
                    txtInfo.setTextColor(Color.RED);
                    pbDialog.setVisibility(View.GONE);
                    Toast.makeText(myContext,
                            "Логін або пароль не вірний!", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                case "no_id":
                    txtInfo.setText("Ви не можете управляти чужим оголошенням!");
                    txtInfo.setTextColor(Color.RED);
                    pbDialog.setVisibility(View.GONE);
                    Toast.makeText(myContext,
                            "Ви не можете управляти чужим оголошенням!", Toast.LENGTH_LONG).show();
                    //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                    break;

                default:
                    txtInfo.setText("Невідома помилка! " + chekError);
                    txtInfo.setTextColor(Color.RED);
                    pbDialog.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }
}