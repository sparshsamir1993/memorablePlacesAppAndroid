package com.example.memorableplaces;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> poiList;
    ListView poiListV;

    public void addPlaceClick(View view){
        Intent toMapScreen = new Intent(getApplicationContext(), MapsActivity.class);
        startActivityForResult(toMapScreen, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.i("I am Back !! --- ", data.getStringArrayListExtra("poiList").toString());
        if(requestCode ==1){
            if(resultCode == RESULT_OK){

               String j =  data.getStringExtra("poiList");
               Log.i("String is  --- ", j);
               try{
                   JSONArray jsonArr = new JSONArray(j);
                   for(int i = 0 ; i< jsonArr.length(); i++){

                       JSONObject json = jsonArr.getJSONObject(i);
                       poiList.add(json.getString("name"));
                   }
                   updateList();
               }catch(Exception e){
                   e.printStackTrace();
               }
            }
        }
    }

    public void updateList(){
        ArrayAdapter<String> poiAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, poiList);
        poiListV.setAdapter(poiAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(poiList == null){
            poiList = new ArrayList<String>();
        }
        poiListV = findViewById(R.id.placesList);

        ArrayAdapter<String> poiAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, poiList);
        poiListV.setAdapter(poiAdapter);


    }
}
