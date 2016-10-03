package com.antoinedrouin.enjoyfood.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.antoinedrouin.enjoyfood.Classes.ServerSide;
import com.antoinedrouin.enjoyfood.R;

public class CommandeDetails extends AppCompatActivity {

    Context context;
    static CommandeDetails instCommandeDetails;

    TextView txtNom, txtPrenom, txtAdress, txtTel, txtPrix;
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
        txtPrix = (TextView) findViewById(R.id.txtPrixCom);
        spinEtat = (Spinner) findViewById(R.id.spinEtat);
        edtRemarque = (EditText) findViewById(R.id.edtRemarqueCom);
        lvCom = (ListView) findViewById(R.id.lvCom);
        btnCom = (Button) findViewById(R.id.btnCom);
        layoutLoading = (RelativeLayout) findViewById(R.id.loadingPanel);
        layoutLoading.setVisibility(View.VISIBLE);

        Bundle extras = getIntent().getExtras();
        idCom = extras.getString(getString(R.string.extraIdCom), "");

        ServerSide getCommandeInfos = new ServerSide(context);
        getCommandeInfos.execute(getString(R.string.getCommandeInfos), getString(R.string.read), idCom);
    }

    public void showCommande(String[] com) {
        txtNom.setText(com[0]);
        txtPrenom.setText(com[1]);
        txtAdress.setText(com[2]);
        txtTel.setText(com[3]);
        txtPrix.setText(com[4]);
        // spinner à faire
        edtRemarque.setText(com[6]);

        getArticles();
    }

    public void getArticles() {

    }

    public void onClickChangeCom(View v) {
        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
    }

    public static CommandeDetails getInstance() {
        return instCommandeDetails;
    }
}
