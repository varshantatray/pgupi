package com.app.upipg.Models;

import java.io.Serializable;

public class PG_INPUT_REQ implements Serializable {
  private String merchantId;

  private String merchantRefId;

  private String merchantTransactionAmount;

  private String merchantUserName;

  private String merchantSecret;

  public String getMerchantId() {
    return this.merchantId;
  }

  public void setMerchantId(String merchantId) {
    this.merchantId = merchantId;
  }

  public String getMerchantRefId() {
    return this.merchantRefId;
  }

  public void setMerchantRefId(String merchantRefId) {
    this.merchantRefId = merchantRefId;
  }

  public String getMerchantTransactionAmount() {
    return this.merchantTransactionAmount;
  }

  public void setMerchantTransactionAmount(String merchantTransactionAmount) {
    this.merchantTransactionAmount = merchantTransactionAmount;
  }

  public String getMerchantUserName() {
    return this.merchantUserName;
  }

  public void setMerchantUserName(String merchantUserName) {
    this.merchantUserName = merchantUserName;
  }

  public String getMerchantSecret() {
    return this.merchantSecret;
  }

  public void setMerchantSecret(String merchantSecret) {
    this.merchantSecret = merchantSecret;
  }
}
