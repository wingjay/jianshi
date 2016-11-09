package com.wingjay.jianshi.sync;


public enum Operation {
  CREATE("create"),
  UPDATE("update"),
  DELETE("delete");

  private final String action;

  Operation(String action) {
    this.action = action;
  }

  public String getAction() {
    return action;
  }
}
