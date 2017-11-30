package com.techpark.BonusHub.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.techpark.BonusHub.db.client_host.ClientHostDao;
import com.techpark.BonusHub.db.host.Host;
import com.techpark.BonusHub.db.host.HostDao;
import com.techpark.BonusHub.db.client.Client;
import com.techpark.BonusHub.db.client.ClientDao;
import com.techpark.BonusHub.db.client_host.ClientHost;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Created by Timur on 13-Apr-17.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    //имя файла базы данных который будет храниться в /data/data/APPNAME/DATABASE_NAME.db
    private static final String DATABASE_NAME ="bonus.db";

    //с каждым увеличением версии, при нахождении в устройстве БД с предыдущей версией будет выполнен метод onUpgrade();
    private static final int DATABASE_VERSION = 13;

    //ссылки на DAO соответсвующие сущностям, хранимым в БД
    private HostDao hostDao = null;
    private ClientDao clientDao = null;
    private ClientHostDao clientHostDao = null;

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
//        context.deleteDatabase("bonus.db");
    }

    //Выполняется, когда файл с БД не найден на устройстве
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
        try
        {
            TableUtils.createTable(connectionSource, Host.class);
            TableUtils.createTable(connectionSource, Client.class);
            TableUtils.createTable(connectionSource, ClientHost.class);
        }
        catch (SQLException e){
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }


    //Выполняется, когда БД имеет версию отличную от текущей
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer) {
        try {
            //Так делают ленивые, гораздо предпочтительнее не удаляя БД аккуратно вносить изменения
            TableUtils.dropTable(connectionSource, Host.class, true);
            TableUtils.dropTable(connectionSource, Client.class, true);
            TableUtils.dropTable(connectionSource, ClientHost.class, true);
            this.onCreate(db, connectionSource);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearTablesForClient(ConnectionSource connectionSource) {
        try {
            TableUtils.dropTable(connectionSource, Host.class, true);
            TableUtils.dropTable(connectionSource, ClientHost.class, true);
            TableUtils.createTable(connectionSource, Host.class);
            TableUtils.createTable(connectionSource, ClientHost.class);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }
    public void clearHostTable(ConnectionSource connectionSource) {
        try {
            TableUtils.dropTable(connectionSource, Host.class, true);
            TableUtils.createTable(connectionSource, Host.class);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

        //синглтон для HostDAO
    public HostDao getHostDAO() throws SQLException, java.sql.SQLException {
        if(hostDao == null){
            hostDao = new HostDao(getConnectionSource(), Host.class);
        }
        return hostDao;
    }
    //синглтон для ClientDAO
    public ClientDao getClientDAO() throws SQLException, java.sql.SQLException {
        if(clientDao == null){
            clientDao = new ClientDao(getConnectionSource(), Client.class);
        }
        return clientDao;
    }
    //синглтон для ClientHostDAO
    public ClientHostDao getClientHostDAO() throws SQLException, java.sql.SQLException {
        if(clientHostDao == null){
            clientHostDao = new ClientHostDao(getConnectionSource(), ClientHost.class);
        }
        return clientHostDao;
    }

    //выполняется при закрытии приложения
    @Override
    public void close(){
        super.close();
        hostDao = null;
    }
}
