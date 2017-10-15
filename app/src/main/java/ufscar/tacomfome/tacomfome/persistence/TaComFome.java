package ufscar.tacomfome.tacomfome.persistence;


import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class TaComFome extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
