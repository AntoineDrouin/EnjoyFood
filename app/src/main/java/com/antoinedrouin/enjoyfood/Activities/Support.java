package com.antoinedrouin.enjoyfood.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.antoinedrouin.enjoyfood.R;

public class Support extends AppCompatActivity {

    EditText edtTitre, edtDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        edtTitre = (EditText) findViewById(R.id.edtTitre);
        edtDesc = (EditText) findViewById(R.id.edtDesc);
    }

    public void onClickDoc(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.varPdfManuel))));
    }

    public void onClickMail(View v) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {getString(R.string.varMailAdmin)});
        intent.putExtra(Intent.EXTRA_SUBJECT, edtTitre.getText().toString());
        intent.putExtra(Intent.EXTRA_TEXT, edtDesc.getText().toString());
        startActivity(Intent.createChooser(intent, ""));
    }
}

