package gorden.widget.ploygonchart;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Gorden on 2016/4/15.
 */
public class PolyGonChartActivity extends Activity{
    private PolygonChart polay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polygonchart);
        polay= (PolygonChart) findViewById(R.id.polay);
        polay.setFillColor(Color.parseColor("#55ff0000"));
    }

    public void refreshChart(View view){
        polay.setChartData(new float[]{77,98,90,83,95});
    }
    public void addExplain(View view){
        polay.setPointExplain(new String[]{"人脉关系","成交率","信誉度","好评率","行为"});
    }
    public void addExplain1(View view){
        polay.clearPointExplain();
    }
    public void rotateChart(View view){
        polay.setChartDataAnimation(new float[]{77,98,90,83,95});
    }
    public void rotateChart1(View view){
        polay.setChartDataAnimation(new float[]{20,30,15,45,26});
    }
    public void addPoint(View view){
        polay.setSideNum(polay.getSideNum()+1);
    }
    public void removePoint(View view){
        polay.setSideNum(polay.getSideNum()-1);
    }
}
