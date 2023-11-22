package com.app.upipg;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.upipg.Models.PG_Collect_REQUEST;
import com.app.upipg.Models.PG_Collect_RES;
import com.app.upipg.Models.PG_Response_REQ;
import com.app.upipg.Models.PG_Response_RES;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SDK_PG_UPI_Activity extends AppCompatActivity {

    //UPI Activity Strat

    String strorderid;
    private final Gson gson = new Gson();
    private SDK_PG_UPI_Activity.SuccessHandler successHandler;
    private SDK_PG_UPI_Activity.SuccessHandler failureHandler;
    private String FAILURE = "FAILURE";

    private static final int REQUEST_CODE_PHONEPE = 1002;
    private static final int REQUEST_CODE_PAYTM = 1003;
    private static final int REQUEST_CODE_GOOGLE_PAY = 1001;
    private static final int REQUEST_CODE_UPI_PAYMENT = 0;
    LinearLayout PGLayout;
    TextView ipaddress,time;
    LinearLayout creditCardLayout,debitCardLayout,netbanking,phonepe,gpay,paytm,otherupi;
    String strmerchant_secret,strmerchnat_ref_id,strAmount,strUpiId,strTransactionId,strNotes,strCurrency,strmerchnat_name,strmerchnat_id,strbrandname,formattedDate;
    ImageView app_logo;


    //UPI Activity End




    String strupiid,strtTid,stramount;
    double latitude;
    double longitude;
    float pecentge = 18;

    String ipAddress, deviceId;

    private static final int PERMISSION_REQUEST_CODE = 1;
    TextView brand_name,merchant_name,txtUserAmount;
    EditText txtUserMobile;
    Button proceed;
    LinearLayout all_screen_upi,info_screen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pg_info);

        brand_name = findViewById(R.id.brand_name);
        merchant_name = findViewById(R.id.merchant_name);
        txtUserAmount = findViewById(R.id.txtUserAmount);
        txtUserMobile = findViewById(R.id.txtUserMobile);
        all_screen_upi = findViewById(R.id.all_screen_upi);
        info_screen = findViewById(R.id.info_screen);


        proceed = findViewById(R.id.proceed);


        Intent i = getIntent();
        strAmount = i.getExtras().getString("trAm");
        strUpiId = i.getExtras().getString("trUpiId");
        strTransactionId = i.getExtras().getString("trId");
        strNotes = i.getExtras().getString("trNotes");
        strCurrency = i.getExtras().getString("trcur");
        strbrandname = i.getExtras().getString("BRname");
        strmerchnat_name = i.getExtras().getString("merchant_name");
        strmerchnat_id = i.getExtras().getString("merchant_id");
        strmerchnat_ref_id = i.getExtras().getString("merchant_ref_id");
        strmerchant_secret = i.getExtras().getString("merchant_secret");
        if (i.getExtras().getString("trOrId") != null) {
            strorderid = i.getExtras().getString("trOrId");
        }


        merchant_name.setText(strmerchnat_name);
        txtUserAmount.setText("₹ "+strAmount);
        brand_name.setText("Brand Name : "+strbrandname);




        // Check for location permissions at runtime (if not granted)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                getLocation();
            } else {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        } else {
            getLocation();
        }

        // Get IP address
        ipAddress = getIPAddress();
        Date currentDate = new Date();

        // Define the desired date format
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

        // Format the current date as a string in the desired format
        formattedDate = dateFormatter.format(currentDate);

        System.out.println("Current Date: " + formattedDate);




        System.out.println("ipAddress"+ipAddress+"        \n    "+Build.DEVICE+"\n"+Build.USER+"\n"+Build.SERIAL+"\n"+Build.FINGERPRINT);



        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtUserMobile.getText().toString().length()!=10){
                    txtUserMobile.setError("Enter Mobile Number");
                    txtUserMobile.setFocusable(true);
                }else {

                    PG_Collect();


                }
            }
        });


    }

    // Check location permission at runtime (for Android 6.0 and above)
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // Request location permission callback
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                // Handle permission denied
                Log.e("Permission", "Location permission denied");
            }
        }
    }

    // Get current location
    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);
                } else {
                    Log.e("Location", "Unable to retrieve location");
                }
            }
        }
    }

    // Get device IP address

    private String getIPAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    System.out.println(inetAddress+" IP Address");
                    // Check if it's not the loopback address and if it's an IPv4 address
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }





    //call

    private void PG_Collect(){
        SharedPreferences prefs = SDK_PG_UPI_Activity.this.getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        PG_Collect_REQUEST Collect_REQ= new PG_Collect_REQUEST();
        Collect_REQ.setIpaddress(ipAddress);
        Collect_REQ.setLatitude(String.valueOf(latitude));
        Collect_REQ.setLongitude(String.valueOf(longitude));
        Collect_REQ.setAppId(Build.FINGERPRINT);
        Collect_REQ.setMerchantId(strmerchnat_id);
        Collect_REQ.setMerchantRefId(strTransactionId);
        Collect_REQ.setMerchantSecret(strmerchant_secret);
        Collect_REQ.setMerchantTransactionAmount(strAmount);
        Collect_REQ.setMerchantUserName(strmerchnat_name);
        Collect_REQ.setMobileNumber(txtUserMobile.getText().toString());

        //set data req

        ApiInterface apiInterface= ApiClient.PG_BASE().create(ApiInterface.class);
        Call<PG_Collect_RES> apicall= apiInterface.USER_DATA(Collect_REQ);
        apicall.enqueue(new Callback<PG_Collect_RES>() {
            @Override
            public void onResponse(Call<PG_Collect_RES> call, Response<PG_Collect_RES> response) {

                try {


                    if (response.body().getStatus().equals("00")) {

/*
                        Intent i = new Intent(SDK_PG_UPI_Activity.this,SDK_PG_UPI_Activity.class);
                        i.putExtra("trAm",strAmount);
                        i.putExtra("trUpiId",strUpiId);
                        i.putExtra("trId",strTransactionId);
                        i.putExtra("trNotes",strNotes);
                        i.putExtra("trcur",strCurrency);
                        i.putExtra("trOrId",strorderid);
                        i.putExtra("BRname",strbrandname);
                        i.putExtra("merchant_name",strmerchnat_name);
                        i.putExtra("merchant_id",strmerchnat_id);
                        i.putExtra("merchant_secret",strmerchant_secret);
                        i.putExtra("merchnat_ref_id",strTransactionId);
                        i.putExtra("mobile",txtUserMobile.getText().toString());
                        i.putExtra("ip_address",ipAddress);
                        i.putExtra("date_time",formattedDate);
                        startActivity(i);
*/

                        UPI_INTENT_ACTIVITY();




                    } else {


                    }

                }catch (Exception e){

                }

            }

            @Override
            public void onFailure(Call<PG_Collect_RES> call, Throwable t) {
                Toast.makeText(SDK_PG_UPI_Activity.this, "onFailure.....", Toast.LENGTH_SHORT).show();
                //   Toast.makeText(M_pinLoginCheck.this, "Failed to make the API call: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Log.e("API_CALL_FAILURE", "Failed to make the API call: " + t.getMessage());
            }
        });
    }

    public  void UPI_INTENT_ACTIVITY(){

        all_screen_upi.setVisibility(View.VISIBLE);
        info_screen.setVisibility(View.GONE);

        ipaddress.setText("IP Address : "+ipAddress);

        time.setText("Accesss Time : "+ formattedDate);
        brand_name.setText("Brand Name : "+ strbrandname);
        merchant_name.setText(strmerchnat_name);



        phonepe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payUsingPhonepeUpi(strbrandname, strUpiId, strNotes, strAmount,strCurrency);

            }
        });

        gpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payUsingUpi(strbrandname, strUpiId, strNotes, strAmount,strCurrency);

            }
        });

        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent with the ACTION_VIEW action and the Paytm URI
                Uri paytmUri = Uri.parse("upi://pay")
                        .buildUpon()
                        .appendQueryParameter("pa", strUpiId.trim()) // Payee VPA (Virtual Payment Address)
                        .appendQueryParameter("pn", strbrandname.trim()) // Payee name
                        .appendQueryParameter("mc", "000000") // Merchant code (optional)
                        .appendQueryParameter("tr", strorderid) // Transaction ID
                        .appendQueryParameter("tn", strNotes.trim()) // Transaction note
                        .appendQueryParameter("am", strAmount.trim()) // Transaction amount
                        .appendQueryParameter("mode", "04".trim()) // Payment mode (optional)
                        .appendQueryParameter("tid", strTransactionId) // Terminal ID (optional)
                        .build();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(paytmUri);
                intent.setPackage("net.one97.paytm");
                // Check if the user has the Paytm app installed
                try {
                    startActivityForResult(intent, REQUEST_CODE_PAYTM);
                } catch (Exception e) {

                    Toast.makeText(SDK_PG_UPI_Activity.this, "PayTm app is not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        otherupi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payUsingOtherUpi(strbrandname, strUpiId, strNotes, strAmount,strCurrency);

            }
        });


    }



    void payUsingUpi(String name, String upiId, String note, String amount,String strCurrency) {
        Log.e("main", "name" + name + "--up--" + "--" + note + "--" + amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", strCurrency)
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        upiPayIntent.setPackage("com.google.android.apps.nbu.paisa.user");
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, REQUEST_CODE_GOOGLE_PAY);
        } else {
            Toast.makeText(this, "Google Pay app is not installed", Toast.LENGTH_SHORT).show();
        }


    }

    void payUsingPhonepeUpi(String name, String upiId, String note, String amount,String strCurrency) {
        Log.e("main", "name" + name + "--up--" + "--" + note + "--" + amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", strCurrency)
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        upiPayIntent.setPackage("com.phonepe.app");

        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, REQUEST_CODE_PHONEPE);
        } else {
            Toast.makeText(this, "No UPI app Found", Toast.LENGTH_SHORT).show();
        }
    }

    void payUsingOtherUpi(String name, String upiId, String note, String amount,String strCurrency) {
        Log.e("main", "name" + name + "--up--" + "--" + note + "--" + amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", strCurrency)
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, REQUEST_CODE_UPI_PAYMENT);
        } else {
            Toast.makeText(this, "No UPI app Found", Toast.LENGTH_SHORT).show();
        }
    }
    //charges


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final JSONObject responseData = new JSONObject();

        try {
            if (requestCode == REQUEST_CODE_GOOGLE_PAY) {
                if (data != null && data.getExtras() != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String status = data.getStringExtra("Status").trim();
                        String responseMessage = bundle.getString("response");

                        responseData.put("status", status);
                        responseData.put("message", responseMessage);

                        SDK_PG_UPI_Activity.TransactionResult transactionResult = new SDK_PG_UPI_Activity.TransactionResult(status, responseMessage);

                        if ("SUCCESS".equals(status)) {
                            collectData(transactionResult);

                        } else {
                            collectData(transactionResult);

                        }
                    }
                }
            } else {
                responseData.put("message", "Request Code Mismatch");
                responseData.put("status", FAILURE);
                if (failureHandler != null) {
                    failureHandler.invoke(gson.toJson(responseData));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (requestCode == REQUEST_CODE_PHONEPE) {
                if (data != null && data.getExtras() != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String status = data.getStringExtra("Status").trim();
                        String responseMessage = bundle.getString("response");

                        responseData.put("status", status);
                        responseData.put("message", responseMessage);

                        SDK_PG_UPI_Activity.TransactionResult transactionResult = new SDK_PG_UPI_Activity.TransactionResult(status, responseMessage);

                        if ("SUCCESS".equals(status)) {
                            collectData(transactionResult);

                        } else {
                            collectData(transactionResult);

                        }
                    }
                }
            } else {
                responseData.put("message", "Request Code Mismatch");
                responseData.put("status", FAILURE);
                if (failureHandler != null) {
                    failureHandler.invoke(gson.toJson(responseData));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (requestCode == REQUEST_CODE_PAYTM) {
                if (data != null && data.getExtras() != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String status = data.getStringExtra("Status").trim();
                        String responseMessage = bundle.getString("response");

                        responseData.put("status", status);
                        responseData.put("message", responseMessage);

                        SDK_PG_UPI_Activity.TransactionResult transactionResult = new SDK_PG_UPI_Activity.TransactionResult(status, responseMessage);

                        if ("SUCCESS".equals(status)) {
                            collectData(transactionResult);

                        } else {
                            collectData(transactionResult);

                        }
                    }
                }
            } else {
                responseData.put("message", "Request Code Mismatch");
                responseData.put("status", FAILURE);
                if (failureHandler != null) {
                    failureHandler.invoke(gson.toJson(responseData));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (requestCode == REQUEST_CODE_UPI_PAYMENT) {
                if (data != null && data.getExtras() != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String status = data.getStringExtra("Status").trim();
                        String responseMessage = bundle.getString("response");

                        responseData.put("status", status);
                        responseData.put("message", responseMessage);

                        SDK_PG_UPI_Activity.TransactionResult transactionResult = new SDK_PG_UPI_Activity.TransactionResult(status, responseMessage);

                        if ("SUCCESS".equals(status)) {
                            collectData(transactionResult);

                        } else {
                            collectData(transactionResult);

                        }
                    }
                }
            } else {
                responseData.put("message", "Request Code Mismatch");
                responseData.put("status", FAILURE);
                if (failureHandler != null) {
                    failureHandler.invoke(gson.toJson(responseData));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void collectData(SDK_PG_UPI_Activity.TransactionResult transactionResult) {
        // Add your data collection logic here
        Log.d("DataCollection", "Status: " + transactionResult.getStatus() + ", Message: " + transactionResult.getMessage());
        Response(transactionResult.getStatus(),transactionResult.getMessage());
    }

    public interface SuccessHandler {
        void invoke(String jsonData);
    }

    private static class TransactionResult {
        private String status;
        private String message;

        public TransactionResult(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }




    //call

    private void Response(String MerchantTransactionStatus,String StrResponse){

        PG_Response_REQ PG_REQ= new PG_Response_REQ();
        PG_REQ.setMerchantId(strmerchnat_id);
        PG_REQ.setMerchantRefId(strTransactionId);
        PG_REQ.setMerchantSecret(strmerchant_secret);
        PG_REQ.setMerchantUserName(strmerchnat_name);
        PG_REQ.setMerchantTransactionStatus(MerchantTransactionStatus);
        PG_REQ.setResponse(StrResponse);
        //set data req

        ApiInterface apiInterface= ApiClient.PG_BASE().create(ApiInterface.class);
        Call<PG_Response_RES> apicall= apiInterface.Transaction_response(PG_REQ);
        apicall.enqueue(new Callback<PG_Response_RES>() {
            @Override
            public void onResponse(Call<PG_Response_RES> call, Response<PG_Response_RES> response) {

                try {


                    if (response.body().getStatus().equals("00")) {


                        //    if (response.body().getData().)

/*
Intent i =new Intent(SDK_PG_UPI_Activity.this,CompleteTransaction.class);
*/

                    } else {


                    }

                }catch (Exception e){

                }

            }

            @Override
            public void onFailure(Call<PG_Response_RES> call, Throwable t) {
                Toast.makeText(SDK_PG_UPI_Activity.this, "onFailure.....", Toast.LENGTH_SHORT).show();
            }
        });
    }


}