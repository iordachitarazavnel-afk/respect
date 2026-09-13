package kat.respect.event.events;

import java.util.ArrayList;
import kat.respect.event.CancellableEvent;
import kat.respect.event.Listener;

public interface ItemUseListener extends Listener {
   void onItemUse(ItemUseListener.ItemUseEvent var1);

   class ItemUseEvent extends CancellableEvent<ItemUseListener> {
      @Override
      public void fire(ArrayList<ItemUseListener> listeners) {
         listeners.forEach(e -> e.onItemUse(this));
      }

      @Override
      public Class<ItemUseListener> getListenerType() {
         return ItemUseListener.class;
      }
   }
}
