package com.example.covidtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CountryCodePicker countryCodePicker;
    TextView mtodaytotal, mtotal, mactive, mtodayactive, mrecovered, mtodayrecovered, mdeaths, mtodaydeaths;
    String country;
    TextView mfilter;
    Spinner spinner;
    String[] types = {"cases", "deaths", "recovered", "active"};
    private List<ModelClass> modelClassList;
    private List<ModelClass> modelClassList2;
    PieChart mpiechart;
    private RecyclerView recyclerView;
    com.example.covidtracker.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        countryCodePicker=findViewById(R.id.ccp);
        mtodayactive=findViewById(R.id.todayactive);
        mactive=findViewById(R.id.activecase);
        mdeaths=findViewById(R.id.totaldeath);
        mtodaydeaths=findViewById(R.id.todaydeath);
        mrecovered=findViewById(R.id.recoveredcase);
        mtodayrecovered=findViewById(R.id.todayrecovered);
        mtotal=findViewById(R.id.totalcase);
        mtodaytotal=findViewById(R.id.todaytotal);
        mpiechart=findViewById(R.id.piechart);
        spinner=findViewById(R.id.spinner);
        mfilter=findViewById(R.id.filter);
        recyclerView=findViewById(R.id.recyclerview);
        modelClassList=new ArrayList<>();
        modelClassList2=new ArrayList<>();

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);


        ApiUtilities.getAPIInterface().getCountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList2.addAll(response.body());
                //adapter.notify();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

        adapter= new Adapter(getApplicationContext(),modelClassList2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        countryCodePicker.setCountryForNameCode("India");
        country=countryCodePicker.getSelectedCountryName();

        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country=countryCodePicker.getSelectedCountryName();
                fetchdata();
            }
        });

        fetchdata();







    }

    private void fetchdata() {

    ApiUtilities.getAPIInterface().getCountrydata().enqueue(new Callback<List<ModelClass>>() {
        @Override
        public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
            Log.e("Data",response.body().toString());
            modelClassList.addAll(response.body());
            for (int i=0;i<modelClassList.size();i++)
            {
                if(modelClassList.get(i).getCountry().equals(country))
                {Log.e("ModalCountry", (modelClassList.get(i).getCountry()));
                 mactive.setText((modelClassList.get(i).getActive()));
                 mtodaydeaths.setText((modelClassList.get(i).getTodayDeaths()));
                 mtodayrecovered.setText((modelClassList.get(i).getRecovered()));
                 mtodaytotal.setText((modelClassList.get(i).getTodayCases()));
                 mtotal.setText((modelClassList.get(i).getCases()));
                 mdeaths.setText((modelClassList.get(i).getDeaths()));
                 mrecovered.setText((modelClassList.get(i).getRecovered()));

                 int active,total,recovered,deaths;
                 active=Integer.parseInt(modelClassList.get(i).getActive());
                    total=Integer.parseInt(modelClassList.get(i).getCases());
                    recovered=Integer.parseInt(modelClassList.get(i).getRecovered());
                    deaths=Integer.parseInt(modelClassList.get(i).getDeaths());

                    updateGraph(active,total,recovered,deaths);

                }
            }
        }

        @Override
        public void onFailure(Call<List<ModelClass>> call, Throwable t) {
            Log.e("Failed", "Failed");
        }
    });








    }

    private void updateGraph(int active, int total, int recovered, int deaths) {
        mpiechart.clearChart();
        mpiechart.addPieSlice(new PieModel("Confirm",total,Color.parseColor("#FFB701")));
        mpiechart.addPieSlice(new PieModel("Active",active,Color.parseColor("#FF4CAF50")));
        mpiechart.addPieSlice(new PieModel("Recovered",recovered,Color.parseColor("#38ACCD")));
        mpiechart.addPieSlice(new PieModel("Deaths",deaths,Color.parseColor(    "#F55c47")));
        mpiechart.startAnimation();

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        String  item=types[position];
        mfilter.setText(item);
        adapter.filter(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}