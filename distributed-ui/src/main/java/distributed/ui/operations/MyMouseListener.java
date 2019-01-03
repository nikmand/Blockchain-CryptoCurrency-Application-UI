package distributed.ui.operations;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MyMouseListener implements MouseListener {
   @Override
   public void mouseClicked(MouseEvent event) {
      System.out.println("entered");
   }

   @Override
   public void mouseEntered(MouseEvent event) {
      System.out.println("entered");
   }

   @Override
   public void mouseExited(MouseEvent event) {
      System.out.println("exited");
   }

   @Override
   public void mousePressed(MouseEvent event) {
      System.out.println("pressed");
   }

   @Override
   public void mouseReleased(MouseEvent event) {
      System.out.println("released");
   }
}
