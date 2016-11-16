/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.sync;


import org.json.JSONArray;
import org.json.JSONObject;

public class ChangedData {

  final JSONObject[] created;
  final JSONObject[] updated;
  final JSONObject[] deleted;

  ChangedData(JSONObject[] created, JSONObject[] updated, JSONObject[] deleted) {
    this.created = created;
    this.updated = updated;
    this.deleted = deleted;
  }

  static JSONObject[] parse(JSONObject root, String key) {
    JSONArray array = root.optJSONArray(key);
    if (array == null) {
      return new JSONObject[0];
    }

    JSONObject[] result = new JSONObject[array.length()];
    for (int i = 0; i < array.length(); i++) {
      JSONObject item = array.optJSONObject(i);
      item = item == null ? new JSONObject() : item;
      result[i] = item;
    }

    return result;
  }

  static ChangedData parse(JSONObject root) {
    return new ChangedData(parse(root, Operation.CREATE.getAction()),
        parse(root, Operation.UPDATE.getAction()), parse(root, Operation.DELETE.getAction()));
  }

  JSONObject[] getUpserted() {
    JSONObject[] result = new JSONObject[created.length + updated.length];
    int index = 0;
    for (JSONObject item : created) {
      result[index] = item;
      index++;
    }

    for (JSONObject item : updated) {
      result[index] = item;
      index++;
    }

    return result;
  }
}

