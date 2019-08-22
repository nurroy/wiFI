package com.example.indoorpositioning;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class Locate extends Activity {

	ArrayList<String> buildings;
	DatabaseHelper db;
	ArrayAdapter<String> arrayAdapter;
	ArrayList<PositionData> positionsData;
	String building;
	TextView result;
	Button locate;


	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.locate);
		db = new DatabaseHelper(this);
		buildings = db.getBuildings();
		locate = (Button) findViewById(R.id.locate);

		result = (TextView) findViewById(R.id.result);
        arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, buildings);

		locate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), Scan.class);
				intent.putExtra("isLearning", false);
				startActivityForResult(intent,0);
				
			}
		});
	
		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, buildings);
		// Set The Adapter
        if (buildings.size()==0) {
            Toast.makeText(this, "No building data available.", Toast.LENGTH_LONG).show();
            locate.setEnabled(false);
        }
        else{


		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
		builder.setTitle("Choose building");
		builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// the user clicked on colors[which]
				building = buildings.get(which);

							
				
			}
		});
		builder.show();
        }

	}



    @Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
        if(resultCode==RESULT_OK){


		PositionData positionData = (PositionData) intent
				.getSerializableExtra("PositionData");
		positionsData=db.getReadings(building);

		String closestPosition = null;

		ArrayList<Router> wifis = db.getFriendlyWifis(building);

		int min_distance = positionData.uDistance(positionsData.get(0), wifis);
		HashMap<String, Integer> rssi = positionData.getValues();
		HashMap<String, Integer> rssi1 = positionsData.get(0).getValues();
        int j=0;
        HashMap<String, String> dest = positionsData.get(0).getRouters();
		closestPosition = positionsData.get(0).getName();
		String res = "";
		res += closestPosition + "\n" + min_distance;
		res += "\n" + positionsData.get(0).toString();
        int dista =  0;
		if (positionsData.contains(dest)){
		    dista = Integer.parseInt(String.valueOf(rssi))-Integer.parseInt(String.valueOf(rssi1));
        }

        Log.v("dis:", String.valueOf(dista));

		for (int i = 1; i < positionsData.size(); i++) {
			int distance = positionData.uDistance(positionsData.get(i), wifis);
			Log.v("distance:", String.valueOf(rssi));
			Log.v("distance2:", String.valueOf(rssi1));
			res += "\n" + positionsData.get(i).getName() + "\n" + distance;
			res += "\n" + positionsData.get(i).toString();
			if (distance < min_distance) {
				min_distance = distance;
                j=i;

				closestPosition = positionsData.get(i).getName();

			}

		}
           if (min_distance == PositionData.MAX_DISTANCE){
                closestPosition="OUT OF RANGE";
                Toast.makeText(this,"You are out of range of the selected building",Toast.LENGTH_LONG).show();

            }
            result.setText("Nearest point :  "+ closestPosition);

            //////////////////////////////////////////////////
            min_distance = positionData.uDistance(positionsData.get(0), wifis);
            String closestPosition2 = null;

            closestPosition2 = positionsData.get(0).getName();
            res = "";
            res += closestPosition2 + "\n" + min_distance;
            res += "\n" + positionsData.get(0).toString();
            for (int i = 1; i < positionsData.size(); i++) {
               if(i!=j) {
                    int distance = positionData.uDistance(positionsData.get(i), wifis);
                    res += "\n" + positionsData.get(i).getName() + "\n" + distance;
                    res += "\n" + positionsData.get(i).toString();
                    closestPosition2 = positionsData.get(i).getName();//////////////////////////
                    if(closestPosition2.equals(closestPosition))
                        continue;
                    if (distance < min_distance) {
                        min_distance = distance;
                        closestPosition2 = positionsData.get(i).getName();

                    }
                }
            }

            if (min_distance == PositionData.MAX_DISTANCE){
                closestPosition2="OUT OF RANGE";
                Toast.makeText(this,"You are out of range of the selected building",Toast.LENGTH_LONG).show();

            }




            res += "\nCurrent:\n" + positionData.toString();
            double dist = (Math.pow(10,((min_distance)/10*2)))/100000;
		Log.v("Result",res);
		Log.v("dist", String.valueOf(dist));



		
		super.onActivityResult(requestCode, resultCode, intent);
        }
	}


	public class CustomOnItemSelectedListener implements
			AdapterView.OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			building = parent.getItemAtPosition(pos).toString();
			locate.setEnabled(true);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			locate.setEnabled(false);
		}

	}



}
