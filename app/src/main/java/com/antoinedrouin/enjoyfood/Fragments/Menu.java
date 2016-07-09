package com.antoinedrouin.enjoyfood.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.antoinedrouin.enjoyfood.Activities.Consommable;
import com.antoinedrouin.enjoyfood.Activities.Etablissement;
import com.antoinedrouin.enjoyfood.Classes.CustomExpandableListAdapter;
import com.antoinedrouin.enjoyfood.Classes.ExpandableListData;
import com.antoinedrouin.enjoyfood.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Menu extends Fragment {

    Context context;
    public static Menu instMenu;
    String idEt;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    public static Menu newInstance() {
        Menu fragment = new Menu();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        instMenu = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        expandableListView = (ExpandableListView) view.findViewById(R.id.expLvMenu);
        ExpandableListData.getData(context);

        Bundle extras = Etablissement.getInstance().getIntent().getExtras();
        idEt = extras.getString(getString(R.string.extraEtabId), "");

        return view;
    }

    public void fillExpLstMenu(HashMap<String, List<String>> menu) {
        // Remplissage de l'expandableListView
        expandableListDetail = menu;
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(context, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

//        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(context, expandableListTitle.get(groupPosition) + " List Expanded.", Toast.LENGTH_SHORT).show();
//            }
//        });

//        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(context, expandableListTitle.get(groupPosition) + " List Collapsed.", Toast.LENGTH_SHORT).show();
//            }
//        });

        // Click sur un item
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(context, Consommable.class);
                intent.putExtra(getString(R.string.extraEtabId), idEt);
                intent.putExtra(getString(R.string.nameObject), expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition));
                startActivity(intent);

                return false;
            }
        });
    }

    public static Menu getInstance() {
        return instMenu;
    }
}
