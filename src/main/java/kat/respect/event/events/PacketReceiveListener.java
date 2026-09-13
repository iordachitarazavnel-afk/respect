package kat.respect.event.events;

import java.util.ArrayList;
import kat.respect.event.CancellableEvent;
import kat.respect.event.Listener;
import net.minecraft.class_2596;

public interface PacketReceiveListener extends Listener {
   void onPacketReceive(PacketReceiveListener.PacketReceiveEvent var1);

   class PacketReceiveEvent extends CancellableEvent<PacketReceiveListener> {
      public class_2596 packet;

      public PacketReceiveEvent(class_2596 packet) {
         this.packet = packet;
      }

      @Override
      public void fire(ArrayList<PacketReceiveListener> listeners) {
         listeners.forEach(e -> e.onPacketReceive(this));
      }

      @Override
      public Class<PacketReceiveListener> getListenerType() {
         return PacketReceiveListener.class;
      }
   }
}
