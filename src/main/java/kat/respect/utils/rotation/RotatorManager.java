package kat.respect.utils.rotation;

import kat.respect.Respect;
import kat.respect.event.EventManager;
import kat.respect.event.events.AttackListener;
import kat.respect.event.events.BlockBreakingListener;
import kat.respect.event.events.ItemUseListener;
import kat.respect.event.events.MovementPacketListener;
import kat.respect.event.events.PacketReceiveListener;
import kat.respect.event.events.PacketSendListener;
import kat.respect.utils.RotationUtils;
import net.minecraft.class_2708;
import net.minecraft.class_2828;

public final class RotatorManager
   implements PacketSendListener,
   BlockBreakingListener,
   ItemUseListener,
   AttackListener,
   MovementPacketListener,
   PacketReceiveListener {
   private boolean enabled;
   private boolean rotateBack;
   private boolean resetRotation;
   private final EventManager eventManager;
   private Rotation currentRotation;
   private float clientYaw;
   private float clientPitch;
   private float serverYaw;
   private float serverPitch;
   private boolean wasDisabled;

   public RotatorManager() {
      this.eventManager = Respect.INSTANCE.eventManager;
      this.eventManager.remove(PacketSendListener.class, this);
      this.eventManager.remove(AttackListener.class, this);
      this.eventManager.remove(ItemUseListener.class, this);
      this.eventManager.remove(MovementPacketListener.class, this);
      this.eventManager.remove(PacketReceiveListener.class, this);
      this.eventManager.remove(BlockBreakingListener.class, this);
      this.enabled = true;
      this.rotateBack = false;
      this.resetRotation = false;
      this.serverYaw = 0.0F;
      this.serverPitch = 0.0F;
      this.clientYaw = 0.0F;
      this.clientPitch = 0.0F;
   }

   public void shutDown() {
      this.eventManager.remove(PacketSendListener.class, this);
      this.eventManager.remove(AttackListener.class, this);
      this.eventManager.remove(ItemUseListener.class, this);
      this.eventManager.remove(MovementPacketListener.class, this);
      this.eventManager.remove(PacketReceiveListener.class, this);
      this.eventManager.remove(BlockBreakingListener.class, this);
   }

   public Rotation getServerRotation() {
      return new Rotation(this.serverYaw, this.serverPitch);
   }

   public void enable() {
      this.enabled = true;
      this.rotateBack = false;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void disable() {
      if (this.isEnabled()) {
         this.enabled = false;
         if (!this.rotateBack) {
            this.rotateBack = true;
         }
      }
   }

   public void setRotation(Rotation rotation) {
      this.currentRotation = rotation;
   }

   public void setRotation(double yaw, double pitch) {
      this.setRotation(new Rotation(yaw, pitch));
   }

   private void resetClientRotation() {
      Respect.mc.field_1724.method_36456(this.clientYaw);
      Respect.mc.field_1724.method_36457(this.clientPitch);
      this.resetRotation = false;
   }

   public void setClientRotation(Rotation rotation) {
      this.clientYaw = Respect.mc.field_1724.method_36454();
      this.clientPitch = Respect.mc.field_1724.method_36455();
      Respect.mc.field_1724.method_36456((float)rotation.yaw());
      Respect.mc.field_1724.method_36457((float)rotation.pitch());
      this.resetRotation = true;
   }

   public void setServerRotation(Rotation rotation) {
      this.serverYaw = (float)rotation.yaw();
      this.serverPitch = (float)rotation.pitch();
   }

   @Override
   public void onAttack(AttackListener.AttackEvent event) {
      if (!this.isEnabled() && this.wasDisabled) {
         this.enabled = true;
         this.wasDisabled = false;
      }
   }

   @Override
   public void onItemUse(ItemUseListener.ItemUseEvent event) {
      if (!event.isCancelled() && this.isEnabled()) {
         this.enabled = false;
         this.wasDisabled = true;
      }
   }

   @Override
   public void onPacketSend(PacketSendListener.PacketSendEvent event) {
      if (event.packet instanceof class_2828 packet) {
         this.serverYaw = packet.method_12271(this.serverYaw);
         this.serverPitch = packet.method_12270(this.serverPitch);
      }
   }

   @Override
   public void onBlockBreaking(BlockBreakingListener.BlockBreakingEvent event) {
      if (!event.isCancelled() && this.isEnabled()) {
         this.enabled = false;
         this.wasDisabled = true;
      }
   }

   @Override
   public void onSendMovementPackets() {
      if (this.isEnabled() && this.currentRotation != null) {
         this.setClientRotation(this.currentRotation);
         this.setServerRotation(this.currentRotation);
      } else {
         if (this.rotateBack) {
            Rotation serverRot = new Rotation(this.serverYaw, this.serverPitch);
            Rotation clientRot = new Rotation(Respect.mc.field_1724.method_36454(), Respect.mc.field_1724.method_36455());
            if (RotationUtils.getTotalDiff(serverRot, clientRot) > 1.0) {
               Rotation smoothRotation = RotationUtils.getSmoothRotation(serverRot, clientRot, 0.2);
               this.setClientRotation(smoothRotation);
               this.setServerRotation(smoothRotation);
            } else {
               this.rotateBack = false;
            }
         }
      }
   }

   @Override
   public void onPacketReceive(PacketReceiveListener.PacketReceiveEvent event) {
      if (event.packet instanceof class_2708 packet) {
         this.serverYaw = packet.comp_3228().comp_3150();
         this.serverPitch = packet.comp_3228().comp_3151();
      }
   }
}
