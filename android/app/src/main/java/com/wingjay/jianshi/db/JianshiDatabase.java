package com.wingjay.jianshi.db;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

@Database(name = JianshiDatabase.NAME, version = JianshiDatabase.VERSION)
public class JianshiDatabase {
  public static final String NAME = "jianshi";
  public static final int VERSION = 1;

  @Migration(version = 1, database = JianshiDatabase.class)
  public static class Migration1 extends BaseMigration {

    @Override
    public void migrate(DatabaseWrapper database) {
      DBMigrationUtil.migrateToDBFlow();
    }
  }

}
