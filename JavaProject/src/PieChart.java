import java.awt.Color;
import java.awt.color.ColorSpace;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
 
public class PieChart extends JFrame {
	
	private static String[][] result;
   
	public PieChart( String title, String[][] result) {
		super( title ); 
      
		PieChart.result = result;
		System.out.println("size : " + result.length);
   }
   
   private static PieDataset createDataset() {
	   DefaultPieDataset dataset = new DefaultPieDataset( );
      
	   for(String[] str: result) {
		   System.out.println(str[0] + ", " + str[1]);
		  dataset.setValue(str[0],  -1 * Integer.parseInt(str[1]));
	   }
      
	   return dataset;         
   	}
   
   private static JFreeChart createChart( PieDataset dataset ) {
      JFreeChart chart = ChartFactory.createPieChart(      
         "Spending",   // chart title 
         dataset,          // data    
         true,             // include legend   
         true, 
         false);

      return chart;
   }
   
   public static JPanel createDemoPanel( ) {
      JFreeChart chart = createChart( createDataset() );  
      return new ChartPanel( chart ); 
   }
   
}