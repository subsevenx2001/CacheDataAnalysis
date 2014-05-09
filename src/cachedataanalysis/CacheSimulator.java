/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cachedataanalysis;

import java.io.IOException;
import java.util.ArrayList;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author jybowser
 */
public interface CacheSimulator {
    
    public void invalidate();
    public void lookup(PacketLog p);
    public ArrayList<XYSeries> exportHitRateData();
    public XYSeries exportReportChart() throws IOException;
    
}
