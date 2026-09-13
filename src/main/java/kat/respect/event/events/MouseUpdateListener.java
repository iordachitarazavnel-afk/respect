package kat.respect.event.events;

import java.util.ArrayList;
import kat.respect.event.Event;
import kat.respect.event.Listener;

public interface MouseUpdateListener extends Listener {
   void onMouseUpdate();

   class MouseUpdateEvent extends Event<MouseUpdateListener> {
      @Override
      public void fire(ArrayList<MouseUpdateListener> listeners) {
         listeners.forEach(MouseUpdateListener::onMouseUpdate);
      }

      @Override
      public Class<MouseUpdateListener> getListenerType() {
         return MouseUpdateListener.class;
      }
   }
}
