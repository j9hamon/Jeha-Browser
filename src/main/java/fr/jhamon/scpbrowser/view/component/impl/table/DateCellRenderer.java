package fr.jhamon.scpbrowser.view.component.impl.table;

import java.awt.Component;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DateCellRenderer extends DefaultTableCellRenderer{

  private static final long serialVersionUID = -2051271300287419276L;

  @Override
  public Component getTableCellRendererComponent(JTable jtab, Object v, boolean selected, boolean focus, int r, int c){
    JLabel component = (JLabel) super.getTableCellRendererComponent(jtab, v, selected, focus, r, c);

    SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    component.setText(formatter.format(v));

    return component;
  }
}
