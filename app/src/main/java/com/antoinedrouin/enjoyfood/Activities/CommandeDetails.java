package com.antoinedrouin.enjoyfood.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.antoinedrouin.enjoyfood.Classes.ServerSide;
import com.antoinedrouin.enjoyfood.Classes.Utilitaire;
import com.antoinedrouin.enjoyfood.R;

import java.util.ArrayList;
import java.util.List;

public class CommandeDetails extends AppCompatActivity {

    Context context;
    static CommandeDetails instCommandeDetails;
    SharedPreferences pref;

    TextView txtNom, txtPrenom, txtAdress, txtTel, txtPrixLivr, txtPrix;
    Spinner spinEtat;
    EditText edtRemarque;
    RelativeLayout layoutLoading;
    ListView lvCom;
    Button btnCom;

    String idCom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commande_details);

        context = getApplicationContext();
        instCommandeDetails = this;

        txtNom = (TextView) findViewById(R.id.txtNomCom);
        txtPrenom = (TextView) findViewById(R.id.txtPrenomCom);
        txtAdress = (TextView) findViewById(R.id.txtAdressCom);
        txtTel = (TextView) findViewById(R.id.txtTelCom);
        txtPrixLivr = (TextView) findViewById(R.id.txtPrixLivrCom);
        txtPrix = (TextView) findViewById(R.id.txtPrixCom);
        spinEtat = (Spinner) findViewById(R.id.spinEtat);
        edtRemarque = (EditText) findViewById(R.id.edtRemarqueCom);
        lvCom = (ListView) findViewById(R.id.lvCom);
        btnCom = (Button) findViewById(R.id.btnCom);
        layoutLoading = (RelativeLayout) findViewById(R.id.loadingPanel);
        layoutLoading.setVisibility(View.VISIBLE);

        Bundle extras = getIntent().getExtras();
        idCom = extras.getString(getString(R.string.extraIdCom), "");

        pref = PreferenceManager.getDefaultSharedPreferences(context);
        // Si le client est un manager, il a le droit de modifier l'état de la commande
        spinEtat.setClickable(pref.getString(getString(R.string.prefCompte), "").equals(getString(R.string.varGerant)));
        try {
            ServerSide getCommandeInfos = new ServerSide(context);
            getCommandeInfos.execute(getString(R.string.getCommandeInfos), getString(R.string.read), idCom);
        }
        catch (Exception e) {
            layoutLoading.setVisibility(View.GONE);
        }
    }

    public void showCommande(String[] com) {
        txtNom.setText(com[0]);
        txtPrenom.setText(com[1]);
        txtAdress.setText(com[2]);
        txtTel.setText(com[3]);
        txtPrixLivr.setText(txtPrixLivr.getText().toString() + " " + com[4] + getString(R.string.txtCurrency));
        txtPrix.setText(txtPrix.getText().toString() +" " + com[5] + getString(R.string.txtCurrency));

        for (int i = 0; i < spinEtat.getCount(); i++) {
            if (spinEtat.getItemAtPosition(i).equals(com[6])) {
                spinEtat.setSelection(i);
                break;
            }
        }

        edtRemarque.setText(com[7]);

        ServerSide getArticles = new ServerSide(context);
        getArticles.execute(getString(R.string.getArticles), getString(R.string.read), idCom);
    }

    public void showArticles(String[][] art) {
        List<String> listAr = new ArrayList<>();

        for (String[] ar : art) {
            listAr.add(ar[0] + " : " + ar[2] + context.getString(R.string.txtCurrency) + " x " + ar[1]);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                context,
                R.layout.listitem,
                listAr);

        lvCom.setAdapter(arrayAdapter);
        lvCom.getLayoutParams().height = arrayAdapter.getCount() * 150;
        layoutLoading.setVisibility(View.GONE);
    }

    public void onClickChangeCom(View v) {
        try {
            ServerSide updateCommande = new ServerSide(context);
            updateCommande.execute(getString(R.string.updateCommande), getString(R.string.write), idCom, spinEtat.getSelectedItem().toString(), edtRemarque.getText().toString());
        }
        catch (Exception e) {
            layoutLoading.setVisibility(View.GONE);
        }
    }

    public void changeCom() {
        Toast.makeText(context, getString(R.string.orderUpdated), Toast.LENGTH_SHORT).show();
        layoutLoading.setVisibility(View.GONE);
    }

    public static CommandeDetails getInstance() {
        return instCommandeDetails;
    }
}