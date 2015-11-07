package mobileproject.incidentreport;

import android.content.Context;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Location;
import android.location.Criteria;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdate;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import mobileproject.incidentreport.Entities.IncidentReport;
public class IncidentMap extends FragmentActivity implements AdapterView.OnItemSelectedListener {

    private static final String database_url = "jdbc:mysql://frankencluster.com:3306/g04dbf15";
    private static final String database_user = "g04dbf15webuser";
    private static final String database_pass = "]a=S]90;{@BH";
    private Spinner categoryDropDown;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    ArrayList<IncidentReport> report = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_map);
        categoryDropDown = (Spinner) findViewById(R.id.categorySpin);
        categoryDropDown.setOnItemSelectedListener(this);

        new getDataFromDatabase().execute();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String cat = categoryDropDown.getSelectedItem().toString();
        filterCategory(cat);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void filterCategory(String category) {
        try {
            if (!category.equals("Select Category")) {
                mMap.clear();
                LocationManager mng = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = mng.getLastKnownLocation(mng.getBestProvider(new Criteria(), false));

                double lat = location.getLatitude();
                double lon = location.getLongitude();

                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("YOU").icon(BitmapDescriptorFactory.fromResource(R.drawable.youmarker)));


                for (int i = 0; i < report.size(); i++) {
                    if (report.get(i).getCategory().equals(category)) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(report.get(i).getLatitude(), report.get(i).getLongitude())).title(report.get(i).getCategory()).icon(BitmapDescriptorFactory.fromResource(R.drawable.criminal)));

                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    private class getDataFromDatabase extends AsyncTask<Void, Void, Void> {
        private String queryResult;

        protected Void doInBackground(Void... arg0) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(database_url, database_user, database_pass);
                String queryString = "select * from g04dbf15.tbl_incidents";

                Statement st = con.createStatement();
                final ResultSet rs = st.executeQuery(queryString);

                while (rs.next()) {
                    IncidentReport newReport = new IncidentReport();
                    newReport.setLongitude(rs.getFloat("longitude"));
                    newReport.setLatitude(rs.getFloat("latitude"));
                    newReport.setDescription(rs.getString("description"));
                    newReport.setIncidentId(rs.getInt("incident_id"));
                    report.add(newReport);
                }
                con.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(database_url, database_user, database_pass);
                for (int i = 0; i < report.size(); i++) {

                    String categoryQuery = "select * from g04dbf15.tbl_incident_cat where g04dbf15.tbl_incident_cat.incident_id = " + report.get(i).getIncidentId();
                    int catId = 0;
                    try {
                        Statement st3 = con.createStatement();
                        final ResultSet rs3 = st3.executeQuery(categoryQuery);

                        while (rs3.next()) {
                            catId = rs3.getInt("catogories_id");
                            try {
                                String categoryQuery2 = "select * from g04dbf15.tbl_catogories where g04dbf15.tbl_catogories.catogories_id = " + catId;
                                Statement st2 = con.createStatement();
                                final ResultSet rs2 = st2.executeQuery(categoryQuery2);


                                while (rs2.next()) {
                                    report.get(i).setCategory(rs2.getString("cat_type"));
                                }


                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println("Failed collect Data for the following reason:");
                        ex.printStackTrace();
                    }
                }
                con.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            setUpMapIfNeeded();
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.incidentMap))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        } else {
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
      try {
            LocationManager mng = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = mng.getLastKnownLocation(mng.getBestProvider(new Criteria(), false));


            double lat = location.getLatitude();
            double lon = location.getLongitude();

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 10);
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("YOU").icon(BitmapDescriptorFactory.fromResource(R.drawable.youmarker)));
            mMap.animateCamera(cameraUpdate);
            for (int i = 0; i < report.size(); i++) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(report.get(i).getLatitude(), report.get(i).getLongitude())).title(report.get(i).getCategory()).icon(BitmapDescriptorFactory.fromResource(R.drawable.criminal)));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}