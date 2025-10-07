package net.juniorwmg.paytheprice.mixin.gadgetsngoodies;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import themattyboy.gadgetsngoodies.init.GadgetItems;
import themattyboy.gadgetsngoodies.items.armor.ItemFlyArmor;
import themattyboy.gadgetsngoodies.network.GadgetPacketHandler;
import themattyboy.gadgetsngoodies.network.SetEntityJumpingMessages;
import themattyboy.gadgetsngoodies.network.StopFallDamageMessages;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentTranslation;

@Mixin(value = ItemFlyArmor.class, remap = false)
public abstract class ItemFlyArmorMixin {

    @Shadow private int coalTimer;
    @Shadow private boolean isJumping;
    @Shadow private int jumpMultiplier;
    @Shadow private int jumpTimer;
    @Shadow public static float maxCoalTimer;

    /**
     * @author JuniorWMG
     * @reason Fix ClassCastException when getting isJumping field from EntityLivingBase
     */
    @Overwrite
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        ItemStack equippedHelmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        ItemStack equippedBody = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        ItemStack equippedLeg = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        ItemStack equippedFeet = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);

        if (world.isRemote) {
            handleClientSide(player);
        }

        if ((!equippedHelmet.isEmpty() || player.capabilities.isCreativeMode) && equippedBody != null && equippedBody.getItem() == GadgetItems.jetpack && ((Object)this) == GadgetItems.jetpack) {
            boolean flag = player.inventory.hasItemStack(new ItemStack(Items.COAL)) || player.capabilities.isCreativeMode;

            boolean isPlayerJumping = false;
            try {
                Object jumpingValue = ReflectionHelper.getPrivateValue(net.minecraft.entity.EntityLivingBase.class, player, "isJumping", "field_70703_bu");
                if (jumpingValue instanceof Boolean) {
                    isPlayerJumping = (Boolean) jumpingValue;
                }
            } catch (Exception e) {
                if (world.isRemote) {
                    isPlayerJumping = isJumpKeyDown();
                } else {
                    isPlayerJumping = !player.onGround && player.motionY > 0;
                }
            }

            if (isPlayerJumping && !player.capabilities.isFlying && flag) {
                if (!world.isRemote) {
                    ReflectionHelper.setPrivateValue(NetHandlerPlayServer.class, ((EntityPlayerMP)player).connection, 0, 28);
                }

                player.motionY += 0.2;
                if (player.motionY >= 0.5) {
                    player.motionY = 0.5;
                }

                Vec3d vec3d = player.getLookVec();
                double d6 = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
                double d8 = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);

                if (d6 > 0.0) {
                    player.motionX += (vec3d.x * 1.7 / d6 * d8 - player.motionX) * 0.1;
                    player.motionZ += (vec3d.z * 1.7 / d6 * d8 - player.motionZ) * 0.1;
                }

                player.fallDistance = 0.0F;

                if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("FlyInfo")) {
                    if (!stack.getTagCompound().getBoolean("IsFlying")) {
                        stack.getTagCompound().setBoolean("IsFlying", true);
                    }
                } else {
                    NBTTagCompound nbt = new NBTTagCompound();
                    nbt.setBoolean("IsFlying", true);
                    stack.setTagInfo("FlyInfo", nbt);
                }

                world.playSound((EntityPlayer)null, player.getPosition(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.2F, 0.8F);

                if (!world.isRemote && (float)this.coalTimer < maxCoalTimer * 20.0F) {
                    ++this.coalTimer;
                }

                if ((float)this.coalTimer >= maxCoalTimer * 20.0F && !player.capabilities.isCreativeMode) {
                    player.inventory.clearMatchingItems(Items.COAL, -1, 1, (NBTTagCompound)null);
                    stack.damageItem(1, player);
                    this.coalTimer = 0;
                }
            } else {
                if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("FlyInfo")) {
                    stack.getTagCompound().setBoolean("IsFlying", false);
                }

                if (world.isRemote && isJumpKeyPressed() && !player.capabilities.isFlying && !flag) {
                    player.sendMessage(new TextComponentTranslation("out.of.fuel", new Object[0]));
                }
            }
        }

        if (equippedHelmet.isEmpty() && !player.capabilities.isCreativeMode && equippedBody.getItem() == GadgetItems.jetpack && world.isRemote && isJumpKeyPressed() && !player.capabilities.isFlying) {
            player.sendMessage(new TextComponentTranslation("safety.protocol.violation", new Object[0]));
        }

        if (equippedFeet.getItem() == GadgetItems.spring_boots) {
            if (player.onGround) {
                if (--this.jumpTimer > 0 && !world.isRemote) {
                    this.jumpMultiplier = Math.min(++this.jumpMultiplier, 2);
                }
                if (this.jumpTimer <= 0) {
                    this.jumpMultiplier = 0;
                }
                this.isJumping = false;
            } else {
                this.jumpTimer = 3;
            }

            if (world.isRemote && isJumpKeyDown()) {
                player.fallDistance = 0.0F;
                GadgetPacketHandler.INSTANCE.sendToServer(new StopFallDamageMessages.StopFallDamageMessage(player.getEntityId()));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void handleClientSide(EntityPlayer player) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        GadgetPacketHandler.INSTANCE.sendToServer(new SetEntityJumpingMessages.SetEntityJumpingMessage(player.getEntityId(), mc.gameSettings.keyBindJump.isKeyDown()));
    }

    @SideOnly(Side.CLIENT)
    private boolean isJumpKeyDown() {
        return net.minecraft.client.Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
    }

    @SideOnly(Side.CLIENT)
    private boolean isJumpKeyPressed() {
        return net.minecraft.client.Minecraft.getMinecraft().gameSettings.keyBindJump.isPressed();
    }
}