package database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DbHelper extends SQLiteOpenHelper {

    //редактировать несколько строк в зажатой клавишей alt

    private static final String DB_NAME = "ilpatio.db";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 4;

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        this.mContext = context;

        Log.d("TEST_DATABASE", "start");
        copyDataBase();
/*        this.getReadableDatabase();*/
    }

    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            Log.d("TEST_DATABASE", "db is upgrating");
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists()){
                dbFile.delete();
                Log.d("TEST_DATABASE", "db is deleted: " + dbFile.exists());
            }
            copyDataBase();

            mNeedUpdate = false;
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            Log.d("TEST_DATABASE", "copy db");
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("TEST_DATABASE", "old_version " + oldVersion + ", new_version"  + newVersion);
        if (newVersion > oldVersion){
            mNeedUpdate = true;
            try {
                updateDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}