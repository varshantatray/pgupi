package com.app.bumppypg.Models;

import java.io.Serializable;
import java.lang.String;

public class PG_Response_RES implements Serializable {
  private String msg;

  private Data data;

  private String status;

  public String getMsg() {
    return this.msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Data getData() {
    return this.data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public static class Data implements Serializable {
    private String Message;

    private String Id;

    public String getMessage() {
      return this.Message;
    }

    public void setMessage(String Message) {
      this.Message = Message;
    }

    public String getId() {
      return this.Id;
    }

    public void setId(String Id) {
      this.Id = Id;
    }
  }
}
