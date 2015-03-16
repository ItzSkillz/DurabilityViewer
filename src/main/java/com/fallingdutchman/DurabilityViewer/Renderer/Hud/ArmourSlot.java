package com.fallingdutchman.DurabilityViewer.Renderer.Hud;

import com.fallingdutchman.DurabilityViewer.Handlers.RenderHandler;
import com.fallingdutchman.DurabilityViewer.LiteModDurabilityViewer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ArmourSlot
{
    private Minecraft mc;
    private FontRenderer fr;
    private ItemStack Item;

    public ArmourSlot(Minecraft mc, FontRenderer fr, ItemStack item)
    {
        this.mc = mc;
        this.fr = fr;
        this.Item = item;
    }

    public void Render(int xPos, RenderHandler armourRh, boolean RenderBar)
    {
        int yPos = 17;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(32826);
        RenderHelper.disableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();

        LiteModDurabilityViewer.itemRenderer.renderItemAndEffectIntoGUI(this.fr, this.mc.getTextureManager(), this.Item, xPos, yPos);
        armourRh.RenderDuraString(this.fr, this.Item, xPos, yPos);

        if (RenderBar)
        {
            armourRh.RenderDuraBar(this.Item, xPos, yPos);
        }

        GL11.glDisable(32826);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F,1.0F,1.0F,1.0F);
    }
}