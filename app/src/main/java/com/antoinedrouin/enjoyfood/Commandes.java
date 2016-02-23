package com.antoinedrouin.enjoyfood;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class Commandes extends Activity {

    Context context;

    static Commandes instCom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commandes);

        context = getApplicationContext();

        instCom = this;
    }

    public void searchInLv() {

    }

    public static Commandes getInstance() {
        return instCom;
    }
}
