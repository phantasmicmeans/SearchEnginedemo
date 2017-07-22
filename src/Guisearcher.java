
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Guisearcher extends JFrame {

   Container contentpane;
   JTextField query;
   JButton searchbutton;
   JButton page;
   JLabel numretlb;
   JTextArea result;
   JTextArea result2;
   JTextArea result3;
   JScrollPane scrollpane;
   JScrollPane scrollpane2;
   JScrollPane scrollpane3;
   Font queryfont;
   Font buttonfont;
   Font numretfont;
   public String querystring=null;
   public String topdocstring;
   int count=0;

   Guisearcher() {
	   
      setTitle("Searcher");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      contentpane = getContentPane();
      // contentpane.setLayout(new FlowLayout(FlowLayout.LEFT));
      contentpane.setLayout(null);
      queryfont = new Font("돋움", Font.BOLD, 20);
      query = new JTextField();
      query.setFont(queryfont);
      query.setSize(420, 50);
      query.setLocation(30, 20);
      searchbutton = new JButton("검색");
      buttonfont = new Font("돋움", Font.BOLD, 20);
      searchbutton.setFont(buttonfont);
      searchbutton.setSize(80, 50);
      searchbutton.setLocation(470, 20);
      
      result = new JTextArea(50, 50);
      result.setSize(100,100);
      
      numretlb = new JLabel();
      numretfont = new Font("돋움", Font.BOLD, 20);
      numretlb.setFont(numretfont);
      numretlb.setSize(440, 30);
      numretlb.setLocation(30, 80);

      scrollpane = new JScrollPane(result);
      scrollpane.setBounds(30, 115, 600, 550);

      result2 = new JTextArea(50, 100);
   
      scrollpane2 = new JScrollPane(result2);
      scrollpane2.setBounds(30, 115, 600, 550);

      page = new JButton("페이지 이동");
      //page.addMouseListener(new mouse2Listener());
      page.setSize(130, 50);
      page.setLocation(250, 700);

      contentpane.add(query);
      contentpane.add(searchbutton);
      contentpane.add(numretlb);
      contentpane.add(scrollpane);
      contentpane.add(page);
   
      setSize(600, 800);
      setVisible(true);
      
      searchbutton.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            querystring = query.getText();
        	count=1;
           // result.setText(querystring);
         }
      
      });

      query.addActionListener(new ActionListener() { // Enter 키로 검색
         public void actionPerformed(ActionEvent e) {
            
            JTextField t = (JTextField) e.getSource();
            querystring = t.getText();
            
            
         }
      });
      

   }

   class mouseListener extends MouseAdapter { // 검색버튼으로 검색

      public void mouseClicked(MouseEvent e) {
         
   

   }

   class mouse2Listener extends MouseAdapter {
      public void mouseClicked(MouseEvent e) {

         result.setText(topdocstring);
      }
   }

   /*
    * public static void main(String[] args) { new Guisearcher();
    * 
    * }
    */

}
}