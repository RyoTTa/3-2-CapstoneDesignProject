package com.example.share.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.share.Data.Item;
import com.example.share.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

public class UpdateItemActivity extends AppCompatActivity {

    String UserEmail;
    String UserName;

    private static final String TAG_TEXT = "text";
    private static final String TAG_IMAGE = "image";
    private final int GET_LOCATION_INFO = 100;
    private final int GET_GALLERY_IMAGE = 200;
    private final int GET_DATE_INFO = 300;

    /** UI components */
    ImageView photo;
    TextView category;
    EditText title;
    TextView location;
    EditText price;
    TextView date;
    EditText content;
    ImageView register;
    List<Map<String, Object>> categorylist;

    /** item info */
    private Item item;
    private String item_id;
    String send_category;
    String latitude;
    String longitude;
    String start_date;
    String end_date;
    Uri selectedImageUri;
    String path;
    String item_category_to_show ="";

    int[] image = {R.drawable.select, R.drawable.select, R.drawable.select, R.drawable.select, R.drawable.select, R.drawable.select};
    String[] text = {"장소", "공구", "음향기기", "의료", "유아용품", "기타"};
    String[] text_send = {"place", "tool", "sound_equipment", "medical_equipment", "baby_goods", "etc"};

    /** util */
    private SimpleDateFormat sdf;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_item);
        Intent Registerintent = getIntent();
        item = (Item)Registerintent.getSerializableExtra("item_object");
        item_id = item.getItem_id();

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        UserEmail = pref.getString("user_email",null);
        UserName = pref.getString("user_name",null);

        photo = (ImageView) findViewById(R.id.register_photo);
        category = (TextView) findViewById(R.id.register_category);
        title = (EditText) findViewById(R.id.register_title);
        price = (EditText) findViewById(R.id.register_price);
        location = (TextView) findViewById(R.id.register_location);
        content = (EditText) findViewById(R.id.register_content);

        register = (ImageView) findViewById(R.id.btnregister);
        date = (TextView) findViewById(R.id.register_date);
        categorylist = new ArrayList<>();

        for (int i = 0; i < image.length; i++) {
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put(TAG_IMAGE, image[i]);
            categoryMap.put(TAG_TEXT, text[i]);
            categorylist.add(categoryMap);
        }

        send_category = item.getCategory(); //set send_category
        for (int i = 0; i < text.length; i++) {
            if(text_send[i].equals(send_category)){
                item_category_to_show = text[i];
            }
        }

        category.setText(item_category_to_show);
        title.setText(item.getItem_name());
        price.setText(item.getItem_price_per_day());

        sdf = new SimpleDateFormat("yyyy-MM-dd");
        start_date = sdf.format(item.getAvailableFrom());
        end_date = sdf.format(item.getAvailableTo());
        date.setText(start_date+"~"+end_date);

        String address = getCurrentAddress(new LatLng(item.getLatitude(),item.getLongitude()));
        latitude = String.valueOf(item.getLatitude());
        longitude =String.valueOf(item.getLongitude());
        location.setText(address);
        content.setText(item.getContent());

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photosetting();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem();
            }
        });

    }

    public void photosetting() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, GET_GALLERY_IMAGE);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            path = getPathFromURI(data.getData());
            selectedImageUri = data.getData();
            photo.setImageURI(selectedImageUri);
        } else if (requestCode == GET_LOCATION_INFO && data != null ) {
            String address = "   " + data.getStringExtra("address");
            location.setText(address);
            latitude = data.getStringExtra("latitude");
            longitude = data.getStringExtra("longitude");
            Log.d("위치",latitude + longitude);
        } else if (requestCode == GET_DATE_INFO && data != null) {
            String selectdate = "   " + data.getStringExtra("date");
            start_date = data.getStringExtra("startdate");
            end_date = data.getStringExtra("enddate");
            Log.d("날짜",start_date + end_date);
            date.setText(selectdate);
        }

    }

    public void updateItem() {
        String result="false";

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Log.d(this.getClass().getName(),send_category);

        try {
            //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("_id",item_id);
            jsonObject.accumulate("name",title.getText().toString());
            jsonObject.accumulate("price_per_date",price.getText().toString());
            jsonObject.accumulate("latitude",latitude);
            jsonObject.accumulate("longitude",longitude);
            jsonObject.accumulate("available_date_start",start_date);
            jsonObject.accumulate("available_date_end",end_date);
            jsonObject.accumulate("category",send_category);
            jsonObject.accumulate("contents",content.getText().toString());
            jsonObject.accumulate("executed_user",UserEmail);
            jsonObject.accumulate("old_image_path",UserEmail+item.getItem_name()+".jpeg");
            jsonObject.accumulate("new_image_path",UserEmail+title.getText().toString()+start_date+end_date+".jpeg");

            JSONObject jsonObject2 = new JSONObject();
            File file = new File(path);

            jsonObject2.accumulate("image_path",UserEmail+title.getText().toString()+start_date+end_date+".jpeg");
            Bitmap temp = decodeFile(file);
            jsonObject2.accumulate("data",getStringFromBitmap(temp));
            Log.d("file path : ",getStringFromBitmap(temp)+"testtesttest",null);

            HttpURLConnection con = null;
            HttpURLConnection con2 = null;
            BufferedReader reader = null;
            BufferedReader reader2 = null;

            try{
                URL url = new URL("http://ec2-15-164-51-129.ap-northeast-2.compute.amazonaws.com:3000/item_update");
                URL url2 = new URL("http://ec2-15-164-51-129.ap-northeast-2.compute.amazonaws.com:3000/set_image");
                //연결을 함
                con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("POST");//POST방식으로 보냄
                con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송


                con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                con.connect();

                //서버로 보내기위해서 스트림 만듬
                OutputStream outStream = con.getOutputStream();
                //버퍼를 생성하고 넣음
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();//버퍼를 받아줌

                //서버로 부터 데이터를 받음
                InputStream stream = con.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                result = buffer.toString();

                //image transmission
                con2 = (HttpURLConnection) url2.openConnection();

                con2.setRequestMethod("POST");//POST방식으로 보냄
                con2.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                con2.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송

                con2.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                con2.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                con2.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                con2.connect();

                //서버로 보내기위해서 스트림 만듬
                OutputStream outStream2 = con2.getOutputStream();
                //버퍼를 생성하고 넣음
                BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(outStream2));
                writer2.write(jsonObject2.toString());
                writer2.flush();
                writer2.close();//버퍼를 받아줌

                //서버로 부터 데이터를 받음
                InputStream stream2 = con2.getInputStream();
                reader2 = new BufferedReader(new InputStreamReader(stream2));

                StringBuffer buffer2 = new StringBuffer();

                String line2 = "";
                while((line2 = reader2.readLine()) != null){
                    buffer2.append(line2);
                }
                result = buffer2.toString();

            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(con != null){
                    con.disconnect();
                }
                try {
                    if(reader != null){
                        reader.close();//버퍼를 닫아줌
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("전송 요청",result);
        if(result.equals("true")) {
            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(UpdateItemActivity.this);
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();     //닫기
                    finish();
                }
            });
            alert.setMessage("물품 변경 완료");
            alert.show();
        }

    }

    public void selectCategory(View v){
        CreateListDialogCategory();
    }

    public void CreateListDialogCategory(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(view);

        final ListView listview = (ListView)view.findViewById(R.id.listview_alterdialog_list);
        final AlertDialog dialog = builder.create();


        SimpleAdapter simpleAdapter = new SimpleAdapter(this, categorylist,
                R.layout.alert_dialog_row,
                new String[]{TAG_IMAGE, TAG_TEXT},
                new int[]{R.id.alertDialogItemImageView, R.id.alertDialogItemTextView});


        listview.setAdapter(simpleAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(this.getClass().getName(),text[position]);
                send_category = text_send[position];
                category.setText("   "+text[position]);
                category.setGravity(Gravity.CENTER_VERTICAL);
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    public void selectDate(View v) {
        Intent intent = new Intent(getApplicationContext(),SelectDateActivity.class);
        startActivityForResult(intent,GET_DATE_INFO);
    }

    public void selectLocation(View v) {
        Intent intent = new Intent(getApplicationContext(),MapsMarkerRegiActivity.class);
        startActivityForResult(intent,GET_LOCATION_INFO);
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0);
        }

    }
    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=600;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

}