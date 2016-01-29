package com.antoinedrouin.enjoyfood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Compte extends AppCompatActivity {

    Context context;
    static Compte instCompte;

    SharedPreferences pref;
    SharedPreferences.Editor edit;

    String pseudo, compte;

    LinearLayout layoutMdp, mainLayoutCoord, layoutCoord;
    EditText edtOldMdp, edtNewMdp1, edtNewMdp2, edtNewVille, edtNewCp, edtNewTel, edtNewAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compte);

        context = getApplicationContext();
        instCompte = this;
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        edit = pref.edit();

        pseudo = pref.getString(getString(R.string.prefUser), "");
        compte = pref.getString(getString(R.string.prefCompte), "");

        layoutMdp = (LinearLayout) findViewById(R.id.layoutMdp);
        mainLayoutCoord = (LinearLayout) findViewById(R.id.mainLayoutCoord);
        layoutCoord = (LinearLayout) findViewById(R.id.layoutCoord);

        edtOldMdp = (EditText) findViewById(R.id.edtOldMdp);
        edtNewMdp1 = (EditText) findViewById(R.id.edtNewMdp1);
        edtNewMdp2 = (EditText) findViewById(R.id.edtNewMdp2);
        edtNewVille = (EditText) findViewById(R.id.edtNewVille);
        edtNewCp = (EditText) findViewById(R.id.edtNewCp);
        edtNewTel = (EditText) findViewById(R.id.edtNewTel);
        edtNewAd = (EditText) findViewById(R.id.edtNewAdresse);

        if (compte.equals(getString(R.string.varGerant)))
            mainLayoutCoord.setVisibility(View.GONE);
        else {
            edtNewVille.setText(pref.getString(getString(R.string.prefVille), ""));
            edtNewCp.setText(pref.getString(getString(R.string.prefCp), ""));
            edtNewTel.setText(pref.getString(getString(R.string.prefTel), ""));
            edtNewAd.setText(pref.getString(getString(R.string.prefAdresse), ""));
        }
    }

    public void onClickDeco (View v) {
        deco();
        Toast.makeText(context, getString(R.string.disconnectionSuccess), Toast.LENGTH_SHORT).show();
    }

    public void onClickMdpChange(View v) {
        if (layoutMdp.getVisibility() == View.VISIBLE)
            layoutMdp.setVisibility(View.GONE);
        else
            layoutMdp.setVisibility(View.VISIBLE);
    }

    public void onClickMdpChangeValider(View v) {
        String oldMdp, newMdp1, newMdp2, error;

        error = "";
        oldMdp = edtOldMdp.getText().toString();
        newMdp1 = edtNewMdp1.getText().toString();
        newMdp2 = edtNewMdp2.getText().toString();

        if (oldMdp.length() < 3 || newMdp1.length() < 3 || newMdp2.length() < 3 )
            error = getString(R.string.errorMdpLength);
        else if (!newMdp1.equals(newMdp2))
            error = getString(R.string.errorMdpNotSame);
        else if (oldMdp.equals(newMdp1))
            error = getString(R.string.errorMdpSame);

        if (!error.equals(""))
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
        else {
            if (!pref.getString(getString(R.string.prefMdp), "").equals(oldMdp))
                Toast.makeText(context, getString(R.string.errorOldMdp), Toast.LENGTH_SHORT).show();
            else {
                // Changement du mot de passe
                ServerSide changePassword = new ServerSide(context);
                changePassword.execute(getString(R.string.changeMdp), getString(R.string.write), pseudo, newMdp1);
            }
        }
    }

    public void onClickCoordChange(View v) {
        if (layoutCoord.getVisibility() == View.VISIBLE)
            layoutCoord.setVisibility(View.GONE);
        else
            layoutCoord.setVisibility(View.VISIBLE);
    }

    public void onClickCoordChangeValider(View v) {
        try {
            new AlertDialog.Builder(this)
                .setMessage("Really ?"/*getString(R.string.smsWarning)*/)
                .setCancelable(false)
                .setNegativeButton(getString(R.string.varNo), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, getString(R.string.smsCancel), Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton(getString(R.string.varYes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String ville, cp, tel, ad;

                        ville = edtNewVille.getText().toString();
                        cp = edtNewCp.getText().toString();
                        tel = edtNewTel.getText().toString();
                        ad = edtNewAd.getText().toString();

                        // Changement des coordonnées
                        ServerSide changeCoordClient = new ServerSide(context);
                        changeCoordClient.execute(getString(R.string.changeCoordClient), getString(R.string.write), pseudo, ville, cp, tel, ad);

                        // Envois un sms de confirmation
//                        SmsManager smsManager = SmsManager.getDefault();
//                        smsManager.sendTextMessage("0677020760", null, getString(R.string.smsConfirmChange), null, null);
                    }
                })
                .show();
        }
        catch (Exception e) {
            Toast.makeText(context, getString(R.string.smsError), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void onClickSupprCompte(View v) {
        new AlertDialog.Builder(this)
            .setMessage(getString(R.string.eraseCompteMessage))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.varNo), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(context, getString(R.string.eraseCompteFail), Toast.LENGTH_SHORT).show();
                }
            })
            .setPositiveButton(getString(R.string.varYes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ServerSide eraseCompte = new ServerSide(context);
                    eraseCompte.execute(getString(R.string.eraseCompte), getString(R.string.write), pseudo);
                }
            })
            .show();
    }

    public void deco() {
        edit.clear();
        edit.apply();
        finish();
    }

    public void okMdp() {
        layoutMdp.setVisibility(View.GONE);
        edit.putString(getString(R.string.prefMdp), edtNewMdp1.getText().toString());
        edit.apply();

        edtOldMdp.setText("");
        edtNewMdp1.setText("");
        edtNewMdp2.setText("");
        Toast.makeText(context, getString(R.string.changeMdpSuccess), Toast.LENGTH_SHORT).show();
    }

    public void okCoord() {
        layoutCoord.setVisibility(View.GONE);
        edit.putString(getString(R.string.prefVille), edtNewVille.getText().toString());
        edit.putString(getString(R.string.prefCp), edtNewCp.getText().toString());
        edit.putString(getString(R.string.prefTel), edtNewTel.getText().toString());
        edit.putString(getString(R.string.prefAdresse), edtNewAd.getText().toString());
        edit.apply();

        Toast.makeText(context, getString(R.string.changeCoordSuccess), Toast.LENGTH_SHORT).show();
    }

    public static Compte getInstance() {
        return instCompte;
    }

}
