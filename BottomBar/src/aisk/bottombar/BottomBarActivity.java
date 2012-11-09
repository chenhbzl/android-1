package aisk.bottombar;

import aisk.timebar.TimePicker;
import android.app.Activity;
import android.os.Bundle;

public class BottomBarActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TimePicker picker = (TimePicker) findViewById(R.id.picker);
        int[] data = {1, 3, 5, 7, 13, 15, 19, 24, 29, 33, 37, 41, 48, 49, 50};
        picker.setTimeData(data);
    }
}