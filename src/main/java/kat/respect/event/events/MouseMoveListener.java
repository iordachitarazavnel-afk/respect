package kat.respect.event.events;

import java.util.ArrayList;
import kat.respect.event.CancellableEvent;
import kat.respect.event.Listener;

public interface MouseMoveListener extends Listener {
   void onMouseMove(MouseMoveListener.MouseMoveEvent var1);

   class MouseMoveEvent extends CancellableEvent<MouseMoveListener> {
      public long windowHandle;
      public double x;
      public double y;

      public MouseMoveEvent(long windowHandle, double x, double y) {
         this.windowHandle = windowHandle;
         this.x = x;
         this.y = y;
      }

      @Override
      public void fire(ArrayList<MouseMoveListener> listeners) {
         listeners.forEach(e -> e.onMouseMove(this));
      }

      @Override
      public Class<MouseMoveListener> getListenerType() {
         return MouseMoveListener.class;
      }
   }
}
