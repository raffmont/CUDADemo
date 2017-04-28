package eu.project.rapid.cudademo;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import eu.project.rapid.ac.DFE;
import eu.project.rapid.common.Clone;
import eu.project.rapid.common.RapidConstants;
import eu.project.rapid.gvirtus.Providers;

public class MainActivity extends AppCompatActivity {
    private DFE dfe = null;
    private MatrixMul matrixMul;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Providers.getInstance().register("193.205.230.23", 9991);

        String vmIp = "192.168.0.182";

        Clone clone = null;
        clone = new Clone("", vmIp);
        dfe = DFE.getInstance(getPackageName(), getPackageManager(), this,
                clone, false, RapidConstants.COMM_TYPE.CLEAR);
        matrixMul = new MatrixMul(dfe);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(view, "Pre myRemotedMethod", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        Log.d("MainActivity", "Pre myRemotedMethod");
                        if (matrixMul != null) {
                            Log.d("MainActivity", "matrixMul != null");
                            int wa = 8;
                            int wb = 12;

                            Log.d("MainActivity", "Calling matrixMul without DFE");
                            matrixMul.localGpuMatrixMul(wa, wb, wa);
                            Log.d("MainActivity", "Matrix mul no DFE finished");

                            Log.d("MainActivity", "Calling matrixMul using the DFE");
//                            matrixMul.gpuMatrixMul(wa, wb, wa);
                            Log.d("MainActivity", "Matrix mul with DFE finished");
                        }
                        Log.d("MainActivity", "Post myRemotedMethod");
                        Snackbar.make(view, "Post myRemotedMethod", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dfe != null) {
            dfe.onDestroy();
        }
    }
}
