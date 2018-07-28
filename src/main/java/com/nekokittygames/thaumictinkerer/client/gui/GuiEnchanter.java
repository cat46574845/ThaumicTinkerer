package com.nekokittygames.thaumictinkerer.client.gui;

import com.nekokittygames.thaumictinkerer.ThaumicTinkerer;
import com.nekokittygames.thaumictinkerer.client.gui.button.*;
import com.nekokittygames.thaumictinkerer.client.libs.LibClientResources;
import com.nekokittygames.thaumictinkerer.client.misc.ClientHelper;
import com.nekokittygames.thaumictinkerer.common.containers.EnchanterContainer;
import com.nekokittygames.thaumictinkerer.common.containers.MagnetContainer;
import com.nekokittygames.thaumictinkerer.common.items.ItemSoulMould;
import com.nekokittygames.thaumictinkerer.common.packets.PacketAddEnchant;
import com.nekokittygames.thaumictinkerer.common.packets.PacketHandler;
import com.nekokittygames.thaumictinkerer.common.packets.PacketIncrementEnchantLevel;
import com.nekokittygames.thaumictinkerer.common.packets.PacketMobMagnet;
import com.nekokittygames.thaumictinkerer.common.tileentity.TileEntityEnchanter;
import com.nekokittygames.thaumictinkerer.common.tileentity.TileEntityMobMagnet;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;
import thaumcraft.common.items.resources.ItemCrystalEssence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiEnchanter extends GuiContainer {


    public TileEntityEnchanter enchanter;

    public TileEntityEnchanter getEnchanter() {
        return enchanter;
    }

    public void setEnchanter(TileEntityEnchanter enchanter) {
        this.enchanter = enchanter;
    }

    public List<String> getTooltip() {
        return tooltip;
    }

    public int getVisRequireWidth() {
        return visRequireWidth;
    }

    public void setVisRequireWidth(int visRequireWidth) {
        this.visRequireWidth = visRequireWidth;
    }

    public int getVisRequireHeight() {
        return visRequireHeight;
    }

    public void setVisRequireHeight(int visRequireHeight) {
        this.visRequireHeight = visRequireHeight;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private List<String> tooltip = new ArrayList();
    private static final int WIDTH = 176;
    public static final int HEIGHT = 166;
    private  int visRequireWidth=0;
    private int visRequireHeight=0;
    private int x, y;
    private ItemStack lastTickItem;
    private ItemStack stack;
    GuiEnchantmentButton[] enchantButtons = new GuiEnchantmentButton[16];

    public GuiEnchanter(TileEntityEnchanter tileEntity, EnchanterContainer container) {
        super(container);
        xSize=WIDTH;
        ySize=HEIGHT;
        this.enchanter=tileEntity;
        stack=enchanter.getInventory().getStackInSlot(0);
        lastTickItem=stack;
    }

    @Override
    public void initGui() {
        super.initGui();
        x = (width - xSize) / 2;
        y = (height - ySize) / 2;
        this.visRequireWidth=this.fontRenderer.getStringWidth("Required Vis Crystals")+2;
        this.visRequireHeight=this.fontRenderer.FONT_HEIGHT+2;
        buildButtonList();

    }

    public void buildButtonList() {
        buttonList.clear();
        for (int i = 0; i < 16; i++) {
            int z = -11;
            if(i>7)
            {
                z=26;
            }
            if ((enchantButtons[8] == null || !enchantButtons[8].enabled)) {
                z = 0;
            }
            GuiEnchantmentButton button = new GuiEnchantmentButton(this, 1 + i, x + 38 + ((i) % 8) * 16, y + 32 + z);
            enchantButtons[i] = button;
            buttonList.add(button);
        }

        asignEnchantButtons();

        int i = 0;
        for (Integer enchant : enchanter.getEnchantments()) {
            GuiFramedEnchantmentButton button = new GuiFramedEnchantmentButton(this, 17 + i * 3, x + xSize + 4, y + (i * 26)+30+15);
            button.enchant = Enchantment.getEnchantmentByID(enchant);
            buttonList.add(button);
            buttonList.add(new GuiEnchantmentLevelButton(17 + i * 3 + 1, x + xSize + 24, y + (i * 26)+30+15 - 4, false));
            buttonList.add(new GuiEnchantmentLevelButton(17 + i * 3 + 2, x + xSize + 31, y + (i * 26)+30+15 - 4, true));
            ++i;
        }
    }
    @Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
}
    private void asignEnchantButtons() {
        for (int i = 0; i < 16; i++) {
            enchantButtons[i].enchant = null;
            enchantButtons[i].enabled = false;
        }
        ItemStack currentStack = enchanter.getInventory().getStackInSlot(0);
        if (currentStack == ItemStack.EMPTY || currentStack.isItemEnchanted())
            return;
        int it = 0;
        for (int enchant : enchanter.getCachedEnchantments()) {
            enchantButtons[it].enchant = Enchantment.getEnchantmentByID(enchant);
            enchantButtons[it].enabled = true;
            it++;
            if (it >= 16)
                break;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        stack= enchanter.getInventory().getStackInSlot(0);
        if(stack!=lastTickItem)
            buildButtonList();

        lastTickItem=stack;

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i1) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(LibClientResources.GUI_ENCHANTER);
        drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
        if(enchanter.getCachedEnchantments().size()>0 && enchanter.getCachedEnchantments().size()<9)
        {

            // 34,61
            drawTexturedModalRect(x+34,y+28,0,ySize,147,24);
        }
        else if(enchanter.getCachedEnchantments().size()>=8)
        {
            drawTexturedModalRect(x+34,y+17,0,ySize,147,24);
            // 34,61
            drawTexturedModalRect(x+34,y+54,0,ySize,147,24);
        }

        if(enchanter.getCachedEnchantments().size()>0)
        {
            this.fontRenderer.drawString(ArrayUtils.toString(enchanter.getCachedEnchantments().toArray()),x+30,y+5, 0x610B0B);
        }
        this.fontRenderer.drawString("Required Vis Crystals",x+177,y+7,0x999999);
        GlStateManager.color(1F, 1F, 1F);
        int y=0;
        int j=0;
        for(ItemStack itemStack:enchanter.getEnchantmentCost()) {
            if (!itemStack.isEmpty()) {
                GlStateManager.pushMatrix();
                //GlStateManager.translate(0.0F, 0.0F, 32.0F);
                GlStateManager.color(1F, 1F, 1F, 1F);
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableLighting();
                short short1 = 240;
                short short2 = 240;
                net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
                //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);
                itemRender.renderItemAndEffectIntoGUI(itemStack, x+177+(j*17), y+7+fontRenderer.FONT_HEIGHT);
                itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, x+177+(j*17), y+7+fontRenderer.FONT_HEIGHT, ""+((ItemCrystalEssence)itemStack.getItem()).getAspects(itemStack).getAmount(((ItemCrystalEssence)itemStack.getItem()).getAspects(itemStack).getAspectsSortedByAmount()[0]));
                GlStateManager.popMatrix();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableLighting();
                j++;
            }
        }



    }
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id==0)
        {

        }
        else if(button.id<=16)
        {
            GuiEnchantmentButton enchantButton=enchantButtons[button.id-1];
            if(enchantButton!=null && enchantButton.enchant!=null)
            {
                PacketHandler.INSTANCE.sendToServer(new PacketAddEnchant(enchanter,Enchantment.getEnchantmentID(enchantButton.enchant)));
            }
        }
        else
        {
            int type = (button.id - 17) % 3;
            int index = (button.id - 17) / 3;
            if (index >= enchanter.getEnchantments().size() || index >= enchanter.getLevels().size())
                return;

            int level = enchanter.getLevels().get(index);
            GuiEnchantmentLevelButton levelButton= (GuiEnchantmentLevelButton) button;
            PacketHandler.INSTANCE.sendToServer(new PacketIncrementEnchantLevel(enchanter,enchanter.getEnchantments().get(index),levelButton.plus));

        }
    }
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        if (!tooltip.isEmpty())
            ClientHelper.renderTooltip(par1 - x, par2 - y, tooltip);
        tooltip.clear();
    }

}