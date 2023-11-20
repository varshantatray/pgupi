package com.app.upipg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.bumppypg.Models.PG_INPUT_REQ;
import com.app.bumppypg.Models.PG_INPUT_RES;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputActivity extends AppCompatActivity {
    TextView txtClick;
    String TransactionID;
    String UPI_ID;
    String Currency_ID;
    String Notes,RefID;
    String BrandName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input);
        txtClick = findViewById(R.id.txtClick);

        Verify_m_pin();

    }


    //call

    private void Verify_m_pin() {
        SharedPreferences prefs = InputActivity.this.getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        RefID="8";
        PG_INPUT_REQ PG_REQ = new PG_INPUT_REQ();
        PG_REQ.setMerchantId("2");
        PG_REQ.setMerchantUserName("rajnish@example.com");
        PG_REQ.setMerchantSecret("wetcvb346346dfgdryer5ye5y");
        PG_REQ.setMerchantRefId(RefID);
        PG_REQ.setMerchantTransactionAmount("10");
        //set data req

        ApiInterface apiInterface = ApiClient.PG_BASE().create(ApiInterface.class);
        Call<PG_INPUT_RES> apicall = apiInterface.USER_INPUT(PG_REQ);
        apicall.enqueue(new Callback<PG_INPUT_RES>() {
            @Override
            public void onResponse(Call<PG_INPUT_RES> call, Response<PG_INPUT_RES> response) {

                try {


                    if (response.body().getStatus().equals("00")) {
                        TransactionID = response.body().getData().getTid();
                        UPI_ID = response.body().getData().getUpiid();
                        Currency_ID = response.body().getData().getCurrency();
                        Notes = response.body().getData().getNotes();
                        BrandName = response.body().getData().getBrandName();


                        txtClick.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(InputActivity.this, MainActivity.class);
                                i.putExtra("trId", TransactionID);
                                i.putExtra("trAm", "10");
                                i.putExtra("trUpiId", "9891167366@icici");
                                i.putExtra("trNotes", Notes);
                                i.putExtra("trcur", Currency_ID);
                                i.putExtra("trOrId", "121212");

                                i.putExtra("merchant_id", "2");
                                i.putExtra("merchant_name", "rajnish@example.com");
                                i.putExtra("BRname", BrandName);
                                //req
                                i.putExtra("merchant_ref_id", RefID);
                                i.putExtra("merchant_secret", "wetcvb346346dfgdryer5ye5y");
                                startActivity(i);
                            }
                        });


                    } else {

                        Toast.makeText(InputActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(Call<PG_INPUT_RES> call, Throwable t) {
                Toast.makeText(InputActivity.this, "onFailure.....", Toast.LENGTH_SHORT).show();
                System.out.println("rmrmrmrmrmrm");
                //   Toast.makeText(M_pinLoginCheck.this, "Failed to make the API call: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Log.e("API_CALL_FAILURE", "Failed to make the API call: " + t.getMessage());
            }
        });
    }


}