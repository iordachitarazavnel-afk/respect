package kat.respect.event.events;

import java.util.ArrayList;
import kat.respect.event.Event;
import kat.respect.event.Listener;

public interface MovementPacketListener extends Listener {
   void onSendMovementPackets();

   class MovementPacketEvent extends Event<MovementPacketListener> {
      @Override
      public void fire(ArrayList<MovementPacketListener> listeners) {
         listeners.forEach(MovementPacketListener::onSendMovementPackets);
      }

      @Override
      public Class<MovementPacketListener> getListenerType() {
         return MovementPacketListener.class;
      }
   }
}
