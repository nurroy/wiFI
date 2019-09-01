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

			double min_distance = positionData.uDistance(positionsData.get(0), wifis);
			int j=0;
			int k=0;
			closestPosition = positionsData.get(0).getName();
			String res1 = "";
			res1 += closestPosition + "\n" + min_distance;
			res1 += "\n" + positionsData.get(0).toString();



			for (int i = 1; i < positionsData.size(); i++) {
				double distance = positionData.uDistance(positionsData.get(i), wifis);
				res1 += "\n" + positionsData.get(i).getName() + "\n" + distance;
				res1 += "\n" + positionsData.get(i).toString();
				if (distance < min_distance) {
					min_distance = distance;
					j=i;
					k=i;
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
			String res2 = "";
			res2 += closestPosition2 + "\n" + min_distance;
			res2 += "\n" + positionsData.get(0).toString();

			for (int i = 1; i < positionsData.size(); i++) {
				if(i!=j) {
					double distance = positionData.uDistance(positionsData.get(i), wifis);
					res2 += "\n" + positionsData.get(i).getName() + "\n" + distance;
					res2 += "\n" + positionsData.get(i).toString();
					closestPosition2 = positionsData.get(i).getName();//////////////////////////
					if(closestPosition2.equals(closestPosition))
						continue;
					if (distance < min_distance) {
						min_distance = distance;
						k=j;
						closestPosition2 = positionsData.get(i).getName();


					}
				}
			}

			if (min_distance == PositionData.MAX_DISTANCE){
				closestPosition2="OUT OF RANGE";
				Toast.makeText(this,"You are out of range of the selected building",Toast.LENGTH_LONG).show();

			}

			result2.setText("Nearest point :  "+ closestPosition2);

            //////////////////////////////////////////////////
            min_distance = positionData.uDistance(positionsData.get(0), wifis);
            String closestPosition3 = null;

            closestPosition3 = positionsData.get(0).getName();
            String res3 = "";
            res3 += closestPosition3 + "\n" + min_distance;
            res3 += "\n" + positionsData.get(0).toString();
            for (int i = 1; i < positionsData.size(); i++) {
                if(i!=k) {
					if (j != k) {
						double distance = positionData.uDistance(positionsData.get(i), wifis);
						res3 += "\n" + positionsData.get(i).getName() + "\n" + distance;
						res3 += "\n" + positionsData.get(i).toString();
						closestPosition3 = positionsData.get(i).getName();//////////////////////////
						if (closestPosition3.equals(closestPosition2))
							continue;
						if (closestPosition3.equals(closestPosition))
							continue;
						if (distance < min_distance) {
							min_distance = distance;
							closestPosition3 = positionsData.get(i).getName();

						}
					}
				}
            }

            if (min_distance == PositionData.MAX_DISTANCE){
                closestPosition3="OUT OF RANGE";
                Toast.makeText(this,"You are out of range of the selected building",Toast.LENGTH_LONG).show();

            }

            result2.setText("Nearest point :  "+ closestPosition2);


			Log.v("closest1: ", closestPosition);
            Log.v("closest2: ", closestPosition2);
            Log.v("closest3: ", closestPosition3);





		
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
