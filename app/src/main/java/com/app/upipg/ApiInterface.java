package com.app.upipg;

import com.app.upipg.Models.PG_Collect_REQUEST;
import com.app.upipg.Models.PG_Collect_RES;
import com.app.upipg.Models.PG_INPUT_REQ;
import com.app.upipg.Models.PG_INPUT_RES;
import com.app.upipg.Models.PG_Response_REQ;
import com.app.upipg.Models.PG_Response_RES;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("initiateTransactionSDK.php")
    Call<PG_Collect_RES> USER_DATA(@Body PG_Collect_REQUEST mpin_req);



    @POST("generateTransactionId.php")
    Call<PG_INPUT_RES> USER_INPUT(@Body PG_INPUT_REQ req);




    @POST("updateTransaction.php")
    Call<PG_Response_RES> Transaction_response(@Body PG_Response_REQ req);



}
