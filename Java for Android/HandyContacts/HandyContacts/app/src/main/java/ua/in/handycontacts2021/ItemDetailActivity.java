  package ua.in.handycontacts2021;

  import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.in.handycontacts2021.model.DBHelper;
import ua.in.handycontacts2021.model.DBRequest;
import ua.in.handycontacts2021.network.APIUploadFile;

import static ua.in.handycontacts2021.model.DBHelper.keyCatalogID;
import static ua.in.handycontacts2021.model.DBHelper.keyCatalogMaster;
import static ua.in.handycontacts2021.model.DBHelper.keyLogin;
import static ua.in.handycontacts2021.model.DBHelper.keyPass;
import static ua.in.handycontacts2021.model.DBHelper.tableName;
import static ua.in.handycontacts2021.network.DataService.BASE_URL;

  public class ItemDetailActivity extends AppCompatActivity {

    ImageView imgCatalogLogo;
    TextView txtAccount;
    CollapsingToolbarLayout appBarLayout;
    ItemDetailFragment fragment;

    private static Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        imgCatalogLogo = (ImageView) findViewById(R.id.imgCatalogLogo);
        txtAccount = (TextView) findViewById(R.id.txtAccount);

        appBarLayout = (CollapsingToolbarLayout) this.findViewById(R.id.toolbar_layout);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + tableName;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst(); // переходим на первую строку
        // извлекаем данные из курсора
        String login = c.getString(c.getColumnIndex(keyLogin));
        String pass = c.getString(c.getColumnIndex(keyPass));
        int catalogID = c.getInt(c.getColumnIndex(keyCatalogID));
        c.close();
        dbHelper.close();

        Picasso.with(this)
                .load(BASE_URL + "/script/hcontacts/uploads/" +
                        String.valueOf(catalogID) + "_adv_logo.png")
                .placeholder(R.drawable.avatar_no)
                .into(imgCatalogLogo);

        /*Picasso.with(this)
                .load(BASE_URL_API+"script/uploads/namefile5.png")
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imgCatalogLogo);*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper dbHelper;
                SQLiteDatabase db;
                dbHelper = new DBHelper(ItemDetailActivity.this);
                db = dbHelper.getReadableDatabase();
                String query = "SELECT * FROM " + tableName;
                Cursor c = db.rawQuery(query, null);
                c.moveToFirst(); // переходим на первую строку
                String login = c.getString(c.getColumnIndex(keyLogin));
                c.close();
                dbHelper.close();

                if (login.equals("anonym")) {
                    DialogAutorization dialogAutorization = new DialogAutorization();
                    dialogAutorization.dialogView(ItemDetailActivity.this, ItemDetailActivity.this);
                } else {
                    if (catalogID == 0) {
                        Toast.makeText(ItemDetailActivity.this, "Ви не ввели назву компанії!",
                                Toast.LENGTH_LONG).show();
                        appBarLayout.setTitle("Ви не ввели назву компанії!");
                    } else {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, 1);
                    }
                }
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_catalogFIO,
                    getIntent().getStringExtra(ItemDetailFragment.ARG_catalogFIO));

            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            //cv.put(keyCatalogID, item.catalogID);
            cv.put(keyCatalogMaster, "new");
            long rowID = db.update(tableName, cv, null, null);
            //long rowID = db.insert(tableName, keyCatalogMaster, cv);
            dbHelper.close();

            fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }

    public byte[] getByteArrayfromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        return bos.toByteArray();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    DBHelper dbHelper = new DBHelper(this);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    String query = "SELECT * FROM " + tableName;
                    Cursor c = db.rawQuery(query, null);
                    c.moveToFirst(); // переходим на первую строку
                    // извлекаем данные из курсора
                    String login = c.getString(c.getColumnIndex(keyLogin));
                    String pass = c.getString(c.getColumnIndex(keyPass));
                    int catalogID = c.getInt(c.getColumnIndex(keyCatalogID));
                    c.close();
                    dbHelper.close();

                    Bitmap bitmap = null;
                    try {
                        Uri selectedImageUri = imageReturnedIntent.getData();
                        String selectedImagePath = null;
                        Cursor cursor = this.getContentResolver().query(
                                selectedImageUri, null, null, null, null);
                        if (cursor == null) {
                            selectedImagePath = selectedImageUri.getPath();
                        } else {
                            cursor.moveToFirst();
                            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                            selectedImagePath = cursor.getString(idx);
                        }
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        cursor.close();

                        //file = new File(selectedImagePath);

                        float aspectRatio = bitmap.getWidth() /
                                (float) bitmap.getHeight();
                        int width = 400;
                        int height = Math.round(width / aspectRatio);

                        Bitmap bmpp = Bitmap.createScaledBitmap(
                                bitmap, width, height, false);

                        //imgCatalogLogo.setImageBitmap(bmpp);
                        //imgProduct.setImageURI(selectedImageUri);
                        //---

                        APIUploadFile service = RetrofitClientInstance.getRetrofitInstance()
                                .create(APIUploadFile.class);

                        RequestBody requestFile =
                                RequestBody.create(
                                        MediaType.parse("image/png"),
                                        getByteArrayfromBitmap(bmpp)
                                );

                        MultipartBody.Part body =
                                MultipartBody.Part.createFormData("photo",
                                        String.valueOf(catalogID) + "_adv_logo.png",
                                        requestFile);

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject = new JSONObject();
                            jsonObject.put("catalogID", catalogID);
                            jsonObject.put("login", login);
                            jsonObject.put("pass", pass);
                            byte[] data2 = jsonObject.toString().getBytes("UTF-8");
                            String data_base64 = Base64.encodeToString(data2, Base64.DEFAULT);
                            Call<DBRequest> call = service.uploadImage(body);
                                    //"adv_logo_upload", data_base64);

                        call.enqueue(new Callback<DBRequest>() {
                            @Override
                            public void onResponse(Call<DBRequest> call,
                                                   Response<DBRequest> response) {
                                if(response.isSuccessful()) {
                                    DBRequest changesList = response.body();
                                    /*Toast.makeText(ItemDetailActivity.this,
                                            changesList.dbChekError, Toast.LENGTH_SHORT).show();*/

                                    switch (changesList.dbChekError) {
                                        case "upload_no":  //
                                            appBarLayout.setTitle("Помилка завантаження зображення!");
                                            Toast.makeText(ItemDetailActivity.this,
                                                    "Помилка завантаження зображення!", Toast.LENGTH_LONG).show();
                                            break;

                                        case "upload_yes":  //
                                            appBarLayout.setTitle("Зображення успішно збереженно!");
                                            Toast.makeText(ItemDetailActivity.this,
                                                    "Зображення успішно збереженно!", Toast.LENGTH_LONG).show();
                                            break;

                                        case "no_login_pass":
                                            appBarLayout.setTitle("Логін або пароль не вірний!");
                                            Toast.makeText(ItemDetailActivity.this,
                                                    "Логін або пароль не вірний!", Toast.LENGTH_LONG).show();
                                            //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                                            break;

                                        case "no_id":
                                            appBarLayout.setTitle("Ви не можете управляти чужим оголошенням!");
                                            Toast.makeText(ItemDetailActivity.this,
                                                    "Ви не можете управляти чужим оголошенням!", Toast.LENGTH_LONG).show();
                                            //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                                            break;

                                        case "no_EMAIL":
                                            appBarLayout.setTitle("Для цієї дії, Вам потрібно підтвердити свій е-mail!");
                                            Toast.makeText(ItemDetailActivity.this,
                                                    "Для цієї дії, Вам потрібно підтвердити свій е-mail!", Toast.LENGTH_LONG).show();
                                            //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                                            break;

                                        default:
                                            appBarLayout.setTitle("Невідома помилка! " + changesList.dbChekError);
                                            Toast.makeText(ItemDetailActivity.this,
                                                    "Невідома помилка!", Toast.LENGTH_LONG).show();
                                            break;
                                    }

                                    //imgCatalogLogo.setImageBitmap(bmpp);
                                    //imgProduct.setImageURI(selectedImageUri);
                                    Picasso.with(ItemDetailActivity.this)
                                            .load(BASE_URL + "/script/hcontacts/uploads/" +
                                                    String.valueOf(catalogID) + "_adv_logo.png")
                                            .placeholder(R.drawable.avatar_no)
                                            .networkPolicy(NetworkPolicy.NO_CACHE)
                                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                                            .into(imgCatalogLogo);

                                    } else {
                                    Toast.makeText(ItemDetailActivity.this,
                                            response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                                }

                                /*if (response.code() == 204){
                                    //returnToTwa(response.body());
                                } else {
                                    Toast.makeText(Main.this, "Upload Error: " + response.message(),
                                            Toast.LENGTH_SHORT).show();
                                }*/
                            }

                            @Override
                            public void onFailure(Call<DBRequest> call, Throwable t) {
                                Toast.makeText(ItemDetailActivity.this,
                                        "Щось пішло не так... Будь ласка, спробуйте пізніше!",
                                        Toast.LENGTH_SHORT).show();
                                    }
                                });

                                } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();
                            }

                        } catch (FileNotFoundException e) {
                            //print("FNF");
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                        }
                }
                break;

            case 2:
                if (resultCode == RESULT_OK) {
                    DBHelper dbHelper = new DBHelper(this);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    String query = "SELECT * FROM " + tableName;
                    Cursor c = db.rawQuery(query, null);
                    c.moveToFirst(); // переходим на первую строку
                    // извлекаем данные из курсора
                    String login = c.getString(c.getColumnIndex(keyLogin));
                    String pass = c.getString(c.getColumnIndex(keyPass));
                    int catalogID = c.getInt(c.getColumnIndex(keyCatalogID));
                    c.close();
                    dbHelper.close();

                    Bitmap bitmap = null;
                    try {
                        Uri selectedImageUri = imageReturnedIntent.getData();
                        String selectedImagePath = null;
                        Cursor cursor = this.getContentResolver().query(
                                selectedImageUri, null, null, null, null);
                        if (cursor == null) {
                            selectedImagePath = selectedImageUri.getPath();
                        } else {
                            cursor.moveToFirst();
                            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                            selectedImagePath = cursor.getString(idx);
                        }
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        cursor.close();

                        //file = new File(selectedImagePath);

                        float aspectRatio = bitmap.getWidth() /
                                (float) bitmap.getHeight();
                        int width = 720;
                        int height = Math.round(width / aspectRatio);

                        Bitmap bmpp = Bitmap.createScaledBitmap(
                                bitmap, width, height, false);
                        //---

                        APIUploadFile service = RetrofitClientInstance.getRetrofitInstance()
                                .create(APIUploadFile.class);

                        RequestBody requestFile =
                                RequestBody.create(
                                        MediaType.parse("image/png"),
                                        getByteArrayfromBitmap(bmpp)
                                );

                        MultipartBody.Part body =
                                MultipartBody.Part.createFormData("photo",
                                        String.valueOf(catalogID) + "_adv_photo_1.png",
                                        requestFile);

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject = new JSONObject();
                            jsonObject.put("catalogID", catalogID);
                            jsonObject.put("login", login);
                            jsonObject.put("pass", pass);
                            byte[] data2 = jsonObject.toString().getBytes("UTF-8");
                            String data_base64 = Base64.encodeToString(data2, Base64.DEFAULT);
                            Call<DBRequest> call = service.uploadImage(body);
                                    //"adv_photo_upload", data_base64);

                            call.enqueue(new Callback<DBRequest>() {
                                @Override
                                public void onResponse(Call<DBRequest> call,
                                                       Response<DBRequest> response) {
                                    if(response.isSuccessful()) {
                                        DBRequest changesList = response.body();
                                    /*Toast.makeText(ItemDetailActivity.this,
                                            changesList.dbChekError, Toast.LENGTH_SHORT).show();*/

                                        switch (changesList.dbChekError) {
                                            case "upload_no":  //
                                                appBarLayout.setTitle("Помилка завантаження зображення!");
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Помилка завантаження зображення!", Toast.LENGTH_LONG).show();
                                                break;

                                            case "upload_yes":  //
                                                appBarLayout.setTitle("Зображення успішно збереженно!");
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Зображення успішно збереженно!", Toast.LENGTH_LONG).show();
                                                break;

                                            case "no_login_pass":
                                                appBarLayout.setTitle("Логін або пароль не вірний!");
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Логін або пароль не вірний!", Toast.LENGTH_LONG).show();
                                                //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                                                break;

                                            case "no_id":
                                                appBarLayout.setTitle("Ви не можете управляти чужим оголошенням!");
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Ви не можете управляти чужим оголошенням!", Toast.LENGTH_LONG).show();
                                                //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                                                break;

                                            case "no_EMAIL":
                                                appBarLayout.setTitle("Для цієї дії, Вам потрібно підтвердити свій е-mail!");
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Для цієї дії, Вам потрібно підтвердити свій е-mail!", Toast.LENGTH_LONG).show();
                                                //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                                                break;

                                            default:
                                                appBarLayout.setTitle("Невідома помилка! " + changesList.dbChekError);
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Невідома помилка!", Toast.LENGTH_LONG).show();
                                                break;
                                        }

                                        ImageView imgPhoto1;
                                        imgPhoto1 = (ImageView) fragment.getView().findViewById(R.id.imgPhoto1);
                                        //imgProduct.setImageURI(selectedImageUri);
                                        Picasso.with(ItemDetailActivity.this)
                                                .load(BASE_URL + "/script/hcontacts/uploads/" +
                                                        String.valueOf(catalogID) + "_adv_photo_1.png")
                                                .placeholder(R.drawable.avatar_no)
                                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                                .into(imgPhoto1);
                                        imgPhoto1.setImageBitmap(bmpp);

                                    } else {
                                        Toast.makeText(ItemDetailActivity.this,
                                                response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                                    }

                                /*if (response.code() == 204){
                                    //returnToTwa(response.body());
                                } else {
                                    Toast.makeText(Main.this, "Upload Error: " + response.message(),
                                            Toast.LENGTH_SHORT).show();
                                }*/
                                }

                                @Override
                                public void onFailure(Call<DBRequest> call, Throwable t) {
                                    Toast.makeText(ItemDetailActivity.this,
                                            "Щось пішло не так... Будь ласка, спробуйте пізніше!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (FileNotFoundException e) {
                        //print("FNF");
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                    }
                }
                break;

            case 3:
                if (resultCode == RESULT_OK) {
                    DBHelper dbHelper = new DBHelper(this);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    String query = "SELECT * FROM " + tableName;
                    Cursor c = db.rawQuery(query, null);
                    c.moveToFirst(); // переходим на первую строку
                    // извлекаем данные из курсора
                    String login = c.getString(c.getColumnIndex(keyLogin));
                    String pass = c.getString(c.getColumnIndex(keyPass));
                    int catalogID = c.getInt(c.getColumnIndex(keyCatalogID));
                    c.close();
                    dbHelper.close();

                    Bitmap bitmap = null;
                    try {
                        Uri selectedImageUri = imageReturnedIntent.getData();
                        String selectedImagePath = null;
                        Cursor cursor = this.getContentResolver().query(
                                selectedImageUri, null, null, null, null);
                        if (cursor == null) {
                            selectedImagePath = selectedImageUri.getPath();
                        } else {
                            cursor.moveToFirst();
                            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                            selectedImagePath = cursor.getString(idx);
                        }
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        cursor.close();

                        //file = new File(selectedImagePath);

                        float aspectRatio = bitmap.getWidth() /
                                (float) bitmap.getHeight();
                        int width = 720;
                        int height = Math.round(width / aspectRatio);

                        Bitmap bmpp = Bitmap.createScaledBitmap(
                                bitmap, width, height, false);
                        //---

                        APIUploadFile service = RetrofitClientInstance.getRetrofitInstance()
                                .create(APIUploadFile.class);

                        RequestBody requestFile =
                                RequestBody.create(
                                        MediaType.parse("image/png"),
                                        getByteArrayfromBitmap(bmpp)
                                );

                        MultipartBody.Part body =
                                MultipartBody.Part.createFormData("photo",
                                        String.valueOf(catalogID) + "_adv_photo_2.png",
                                        requestFile);

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject = new JSONObject();
                            jsonObject.put("catalogID", catalogID);
                            jsonObject.put("login", login);
                            jsonObject.put("pass", pass);
                            byte[] data2 = jsonObject.toString().getBytes("UTF-8");
                            String data_base64 = Base64.encodeToString(data2, Base64.DEFAULT);
                            Call<DBRequest> call = service.uploadImage(body);
                                    //"adv_photo_upload", data_base64);

                            call.enqueue(new Callback<DBRequest>() {
                                @Override
                                public void onResponse(Call<DBRequest> call,
                                                       Response<DBRequest> response) {
                                    if(response.isSuccessful()) {
                                        DBRequest changesList = response.body();
                                    /*Toast.makeText(ItemDetailActivity.this,
                                            changesList.dbChekError, Toast.LENGTH_SHORT).show();*/

                                        switch (changesList.dbChekError) {
                                            case "upload_no":  //
                                                appBarLayout.setTitle("Помилка завантаження зображення!");
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Помилка завантаження зображення!", Toast.LENGTH_LONG).show();
                                                break;

                                            case "upload_yes":  //
                                                appBarLayout.setTitle("Зображення успішно збереженно!");
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Зображення успішно збереженно!", Toast.LENGTH_LONG).show();
                                                break;

                                            case "no_login_pass":
                                                appBarLayout.setTitle("Логін або пароль не вірний!");
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Логін або пароль не вірний!", Toast.LENGTH_LONG).show();
                                                //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                                                break;

                                            case "no_id":
                                                appBarLayout.setTitle("Ви не можете управляти чужим оголошенням!");
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Ви не можете управляти чужим оголошенням!", Toast.LENGTH_LONG).show();
                                                //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                                                break;

                                            case "no_EMAIL":
                                                appBarLayout.setTitle("Для цієї дії, Вам потрібно підтвердити свій е-mail!");
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Для цієї дії, Вам потрібно підтвердити свій е-mail!", Toast.LENGTH_LONG).show();
                                                //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                                                break;

                                            default:
                                                appBarLayout.setTitle("Невідома помилка! " + changesList.dbChekError);
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Невідома помилка!", Toast.LENGTH_LONG).show();
                                                break;
                                        }

                                        ImageView imgPhoto2;
                                        imgPhoto2 = (ImageView) fragment.getView().findViewById(R.id.imgPhoto2);
                                        //imgProduct.setImageURI(selectedImageUri);
                                        Picasso.with(ItemDetailActivity.this)
                                                .load(BASE_URL + "/script/hcontacts/uploads/" +
                                                        String.valueOf(catalogID) + "_adv_photo_2.png")
                                                .placeholder(R.drawable.avatar_no)
                                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                                .into(imgPhoto2);
                                        imgPhoto2.setImageBitmap(bmpp);

                                    } else {
                                        Toast.makeText(ItemDetailActivity.this,
                                                response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                                    }

                                /*if (response.code() == 204){
                                    //returnToTwa(response.body());
                                } else {
                                    Toast.makeText(Main.this, "Upload Error: " + response.message(),
                                            Toast.LENGTH_SHORT).show();
                                }*/
                                }

                                @Override
                                public void onFailure(Call<DBRequest> call, Throwable t) {
                                    Toast.makeText(ItemDetailActivity.this,
                                            "Щось пішло не так... Будь ласка, спробуйте пізніше!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (FileNotFoundException e) {
                        //print("FNF");
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                    }
                }
                break;

            case 4:
                if (resultCode == RESULT_OK) {
                    DBHelper dbHelper = new DBHelper(this);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    String query = "SELECT * FROM " + tableName;
                    Cursor c = db.rawQuery(query, null);
                    c.moveToFirst(); // переходим на первую строку
                    // извлекаем данные из курсора
                    String login = c.getString(c.getColumnIndex(keyLogin));
                    String pass = c.getString(c.getColumnIndex(keyPass));
                    int catalogID = c.getInt(c.getColumnIndex(keyCatalogID));
                    c.close();
                    dbHelper.close();

                    Bitmap bitmap = null;
                    try {
                        Uri selectedImageUri = imageReturnedIntent.getData();
                        String selectedImagePath = null;
                        Cursor cursor = this.getContentResolver().query(
                                selectedImageUri, null, null, null, null);
                        if (cursor == null) {
                            selectedImagePath = selectedImageUri.getPath();
                        } else {
                            cursor.moveToFirst();
                            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                            selectedImagePath = cursor.getString(idx);
                        }
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        cursor.close();

                        //file = new File(selectedImagePath);

                        float aspectRatio = bitmap.getWidth() /
                                (float) bitmap.getHeight();
                        int width = 720;
                        int height = Math.round(width / aspectRatio);

                        Bitmap bmpp = Bitmap.createScaledBitmap(
                                bitmap, width, height, false);
                        //---

                        APIUploadFile service = RetrofitClientInstance.getRetrofitInstance()
                                .create(APIUploadFile.class);

                        RequestBody requestFile =
                                RequestBody.create(
                                        MediaType.parse("image/png"),
                                        getByteArrayfromBitmap(bmpp)
                                );

                        MultipartBody.Part body =
                                MultipartBody.Part.createFormData("photo",
                                        String.valueOf(catalogID) + "_adv_photo_3.png",
                                        requestFile);

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject = new JSONObject();
                            jsonObject.put("catalogID", catalogID);
                            jsonObject.put("login", login);
                            jsonObject.put("pass", pass);
                            byte[] data2 = jsonObject.toString().getBytes("UTF-8");
                            String data_base64 = Base64.encodeToString(data2, Base64.DEFAULT);
                            Call<DBRequest> call = service.uploadImage(body);
                                    //"adv_photo_upload", data_base64);

                            call.enqueue(new Callback<DBRequest>() {
                                @Override
                                public void onResponse(Call<DBRequest> call,
                                                       Response<DBRequest> response) {
                                    if(response.isSuccessful()) {
                                        DBRequest changesList = response.body();
                                    /*Toast.makeText(ItemDetailActivity.this,
                                            changesList.dbChekError, Toast.LENGTH_SHORT).show();*/

                                        switch (changesList.dbChekError) {
                                            case "upload_no":  //
                                                appBarLayout.setTitle("Помилка завантаження зображення!");
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Помилка завантаження зображення!", Toast.LENGTH_LONG).show();
                                                break;

                                            case "upload_yes":  //
                                                appBarLayout.setTitle("Зображення успішно збереженно!");
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Зображення успішно збереженно!", Toast.LENGTH_LONG).show();
                                                break;

                                            case "no_login_pass":
                                                appBarLayout.setTitle("Логін або пароль не вірний!");
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Логін або пароль не вірний!", Toast.LENGTH_LONG).show();
                                                //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                                                break;

                                            case "no_id":
                                                appBarLayout.setTitle("Ви не можете управляти чужим оголошенням!");
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Ви не можете управляти чужим оголошенням!", Toast.LENGTH_LONG).show();
                                                //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                                                break;

                                            case "no_EMAIL":
                                                appBarLayout.setTitle("Для цієї дії, Вам потрібно підтвердити свій е-mail!");
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Для цієї дії, Вам потрібно підтвердити свій е-mail!", Toast.LENGTH_LONG).show();
                                                //Log.d(TAG, "За указанный период списания не найдены" + chekError);
                                                break;

                                            default:
                                                appBarLayout.setTitle("Невідома помилка! " + changesList.dbChekError);
                                                Toast.makeText(ItemDetailActivity.this,
                                                        "Невідома помилка!", Toast.LENGTH_LONG).show();
                                                break;
                                        }

                                        ImageView imgPhoto3;
                                        imgPhoto3 = (ImageView) fragment.getView().findViewById(R.id.imgPhoto3);
                                        //imgProduct.setImageURI(selectedImageUri);
                                        Picasso.with(ItemDetailActivity.this)
                                                .load(BASE_URL + "/script/hcontacts/uploads/" +
                                                        String.valueOf(catalogID) + "_adv_photo_3.png")
                                                .placeholder(R.drawable.avatar_no)
                                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                                .into(imgPhoto3);
                                        imgPhoto3.setImageBitmap(bmpp);

                                    } else {
                                        Toast.makeText(ItemDetailActivity.this,
                                                response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                                    }

                                /*if (response.code() == 204){
                                    //returnToTwa(response.body());
                                } else {
                                    Toast.makeText(Main.this, "Upload Error: " + response.message(),
                                            Toast.LENGTH_SHORT).show();
                                }*/
                                }

                                @Override
                                public void onFailure(Call<DBRequest> call, Throwable t) {
                                    Toast.makeText(ItemDetailActivity.this,
                                            "Щось пішло не так... Будь ласка, спробуйте пізніше!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (FileNotFoundException e) {
                        //print("FNF");
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                    }
                }
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        /*Context context = this;
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);*/
        finish();
    }

}