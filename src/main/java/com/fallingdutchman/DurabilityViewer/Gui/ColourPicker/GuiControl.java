package com.fallingdutchman.DurabilityViewer.Gui.ColourPicker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;

import static org.lwjgl.opengl.GL11.*;

/**
 * GuiControlEx is the base class for additional controls. It includes some advanced drawing methods
 * which are used by several derived classes. full credit to Mumfrey for originally creating this.
 *
 * @author Adam Mummery-Smith
 *
 */
public class GuiControl extends GuiButton {

    /** Used by some controls to indicate the manner in which they have handled a keypress. */
    public enum KeyHandledState {
        /** The control did not handle the keypress. */
        None,

        /** The control handled the keypress and the container should do no further processing. */
        Handled,

        /** The control handled the keypress and the container should call actionPerformed. */
        ActionPerformed
    }

    public enum DialogResult {
        /** No result (maybe the dialog was not closed yet?). */
        None,

        /** Dialog result OK (user clicked OK or pressed RETURN). */
        OK,

        /** Dialog result Cancel (user clicked Cancel or pressed ESCAPE). */
        Cancel,

        Yes,

        No
    }

    /** Set by parent screen to enable cursor flash etc. */
    public int updateCounter;

    /** Reference to the minecraft game instance. */
    protected Minecraft mc;

    /** Flag indicating whether an action was performed, to support GuiScreenEx's callback mechanism. */
    protected boolean actionPerformed;

    /** Flag tracking whether an item was double-clicked. */
    protected boolean doubleClicked;

    /** Scale factor which translates texture pixel coordinates to relative coordinates. */
    protected static float texMapScale = 0.00390625F;

    protected static float guiScaleFactor;

    protected static int lastScreenWidth;
    protected static int lastScreenHeight;

    /**
     * Override from GuiButton, handle this call and forward it to DrawControl for neatness.
     *
     * @param minecraft Reference to the minecraft game instance
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     */
    @Override
    public final void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        this.drawControl(minecraft, mouseX, mouseY);
    }

    /**
     * Draw the control.
     *
     * @param minecraft Reference to the minecraft game instance
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     */
    protected void drawControl(Minecraft minecraft, int mouseX, int mouseY) {
        super.drawButton(minecraft, mouseX, mouseY);
    }

    /**
     * Constructor, passes through to GuiButton constructor.
     *
     * @param minecraft Minecraft game instance
     * @param controlId Control's ID (used for actionPerformed)
     * @param xPos Control X position (left)
     * @param yPos Control Y position (top)
     * @param controlWidth Control width
     * @param controlHeight Control height
     * @param displayText Control display text
     */
    public GuiControl(Minecraft minecraft, int controlId, int xPos, int yPos, int controlWidth,
                      int controlHeight, String displayText) {
        super(controlId, xPos, yPos, controlWidth, controlHeight, displayText);
        this.mc = minecraft;
    }

    public GuiControl(Minecraft minecraft, int controlId, int xPos, int yPos, String displayText) {
        super(controlId, xPos, yPos, displayText);
        this.mc = minecraft;
    }

    /**
     * GuiControlEx returns true from mousePressed if the mouse was captured, NOT if an action was
     * performed. Containers should call this function afterwards to determine whether an action was
     * performed.
     *
     * @return True if actionPerformed should be dispatched
     */
    public boolean isActionPerformed() {
        return this.actionPerformed;
    }

    /**
     * Get whether an actionPerformed was a double-click event.
     *
     * @return
     */
    public boolean isDoubleClicked(boolean resetDoubleClicked) {
        boolean result = this.doubleClicked;
        if (resetDoubleClicked)
            this.doubleClicked = false;
        return result;
    }

    /**
     * Draws a line between two points with the specified width and color.
     *
     * @param x1 Origin x coordinate
     * @param y1 Origin y coordinate
     * @param x2 End x coordinate
     * @param y2 End y coordinate
     * @param width Line width in pixels
     * @param color Line color
     */
    public static void drawLine(int x1, int y1, int x2, int y2, int width, int color) {
        drawArrow(x1, y1, x2, y2, 0, width, color, false, 0);
    }

    /**
     * Draws an OpenGL line.
     *
     * @param x1 Start x position
     * @param y1 Start y position
     * @param x2 End x position
     * @param y2 End y position
     * @param width Line width
     * @param color Line color
     */
    @SuppressWarnings("cast")
    public static void drawNativeLine(float x1, float y1, float x2, float y2, float width, int color) {
        float f = (float) (color >> 24 & 0xff) / 255F;
        float f1 = (float) (color >> 16 & 0xff) / 255F;
        float f2 = (float) (color >> 8 & 0xff) / 255F;
        float f3 = (float) (color & 0xff) / 255F;

        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(f1, f2, f3, f);
        glLineWidth(width);

        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawing(GL_LINES);
        tessellator.getWorldRenderer().addVertex(x1, y1, 0);
        tessellator.getWorldRenderer().addVertex(x2, y2, 0);
        tessellator.draw();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    /**
     * Draws an arrow between two points with the specified width and color.
     *
     * @param x1 Origin x coordinate
     * @param y1 Origin y coordinate
     * @param x2 End x coordinate
     * @param y2 End y coordinate
     * @param width Line width in pixels
     * @param arrowHeadSize Size of the arrow head
     * @param color Color
     */
    public static void drawArrow(int x1, int y1, int x2, int y2, int z, int width, int arrowHeadSize,
                                 int color) {
        drawArrow(x1, y1, x2, y2, z, width, color, true, arrowHeadSize);
    }

    /**
     * Internal function for drawing lines and arrows.
     *
     * @param x1 Origin x coordinate
     * @param y1 Origin y coordinate
     * @param x2 End x coordinate
     * @param y2 End y coordinate
     * @param width Line width in pixels
     * @param color Color
     * @param arrowHead True to draw an arrow, otherwise draws a line
     * @param arrowHeadSize Size of the arrow head
     */
    @SuppressWarnings("cast")
    public static void drawArrow(int x1, int y1, int x2, int y2, int z, int width, int color,
                                 boolean arrowHead, int arrowHeadSize) {
        // Calculate the line length and angle defined by the specified points
        int length = (int) Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        float angle = (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));

        // Local rotation
        glPushMatrix();
        glTranslatef(x1, y1, 0.0f);
        glRotatef(angle, 0.0f, 0.0f, 1.0f);

        // Calc coordinates for the line and arrow points
        x1 = 0;
        x2 = length - (arrowHead ? arrowHeadSize : 0);
        y1 = (int) (width * -0.5);
        y2 = y1 + width;

        // Calc color components
        float f = (float) (color >> 24 & 0xff) / 255F;
        float f1 = (float) (color >> 16 & 0xff) / 255F;
        float f2 = (float) (color >> 8 & 0xff) / 255F;
        float f3 = (float) (color & 0xff) / 255F;

        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(f1, f2, f3, f);

        // Draw the line
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().addVertex(x1, y2, z);
        tessellator.getWorldRenderer().addVertex(x2, y2, z);
        tessellator.getWorldRenderer().addVertex(x2, y1, z);
        tessellator.getWorldRenderer().addVertex(x1, y1, z);
        tessellator.draw();

        // If an arrow then draw the arrow head
        if (arrowHead && arrowHeadSize > 0) {
            tessellator.getWorldRenderer().startDrawing(4);
            tessellator.getWorldRenderer().addVertex(x2, 0 - arrowHeadSize / 2, z);
            tessellator.getWorldRenderer().addVertex(x2, arrowHeadSize / 2, z);
            tessellator.getWorldRenderer().addVertex(length, 0, z);
            tessellator.draw();
        }

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);

        glPopMatrix();
    }

    /**
     * Set the texmap scale factor.
     *
     * @param textureSize
     */
    @SuppressWarnings("cast")
    public void setTexMapSize(int textureSize) {
        texMapScale = 1F / (float) textureSize;
    }

    /**
     * Draws a textured rectangle at 90 degrees.
     *
     * @param x Left edge X coordinate
     * @param y Top edge Y coordinate
     * @param x2 Right edge X coordinate
     * @param y2 Bottom edge Y coordinate
     * @param u U coordinate
     * @param v V coordinate
     * @param u2 Right edge U coordinate
     * @param v2 Bottom edge V coordinate
     */
    @SuppressWarnings("cast")
    public void drawTexturedModalRectRot(int x, int y, int x2, int y2, int u, int v, int u2, int v2) {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().addVertexWithUV(x2, y2, this.zLevel, (float) (u) * texMapScale, (float) (v2)
                * texMapScale);
        tessellator.getWorldRenderer().addVertexWithUV(x2, y, this.zLevel, (float) (u2) * texMapScale, (float) (v2)
                * texMapScale);
        tessellator.getWorldRenderer().addVertexWithUV(x, y, this.zLevel, (float) (u2) * texMapScale, (float) (v)
                * texMapScale);
        tessellator.getWorldRenderer().addVertexWithUV(x, y2, this.zLevel, (float) (u) * texMapScale, (float) (v)
                * texMapScale);
        tessellator.draw();
    }

    /**
     * Draws a textured rectangle at 90 degrees.
     *
     * @param x Left edge X coordinate
     * @param y Top edge Y coordinate
     * @param u U coordinate
     * @param v V coordinate
     * @param width Width of texture to draw
     * @param height Height of texture to draw
     */
    @SuppressWarnings("cast")
    public void drawTexturedModalRectRot(int x, int y, int u, int v, int width, int height) {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().addVertexWithUV(x + height, y + width, this.zLevel, (float) (u) * texMapScale,
                (float) (v + height) * texMapScale);
        tessellator.getWorldRenderer().addVertexWithUV(x + height, y, this.zLevel, (float) (u + width) * texMapScale,
                (float) (v + height) * texMapScale);
        tessellator.getWorldRenderer().addVertexWithUV(x, y, this.zLevel, (float) (u + width) * texMapScale, (float) (v)
                * texMapScale);
        tessellator.getWorldRenderer().addVertexWithUV(x, y + width, this.zLevel, (float) (u) * texMapScale, (float) (v)
                * texMapScale);
        tessellator.draw();
    }

    /**
     * Draws a tesselated rectangle where the texture is stretched horizontally but vertical scaling
     * is achieved by splitting the texture in half and repeating the middle pixels.
     *
     * @param x Left edge X coordinate
     * @param y Top edge Y coordinate
     * @param x2 Right edge X coordinate
     * @param y2 Bottom edge Y coordinate
     * @param u U coordinate
     * @param v V coordinate
     * @param u2 Right edge U coordinate
     * @param v2 Bottom edge V coordinate
     */
    public void drawTessellatedModalRectV(int x, int y, int x2, int y2, int u, int v, int u2, int v2) {
        int tileSize = ((v2 - v) / 2);
        int vMidTop = v + tileSize;
        int vMidBtm = vMidTop + 1;

        this.drawTexturedModalRect(x, y, x2, y + tileSize, u, v, u2, vMidTop);
        this.drawTexturedModalRect(x, y + tileSize, x2, y2 - tileSize + 1, u, vMidTop, u2, vMidBtm);
        this.drawTexturedModalRect(x, y2 - tileSize + 1, x2, y2, u, vMidBtm, u2, v2);
    }

    /**
     * Draws a tesselated rectangle where the texture is stretched vertically but horizontal scaling
     * is achieved by splitting the texture in half and repeating the middle pixels.
     *
     * @param x Left edge X coordinate
     * @param y Top edge Y coordinate
     * @param x2 Right edge X coordinate
     * @param y2 Bottom edge Y coordinate
     * @param u U coordinate
     * @param v V coordinate
     * @param u2 Right edge U coordinate
     * @param v2 Bottom edge V coordinate
     */
    public void drawTessellatedModalRectH(int x, int y, int x2, int y2, int u, int v, int u2, int v2) {
        int tileSize = ((u2 - u) / 2);
        int uMidLeft = u + tileSize;
        int uMidRight = uMidLeft + 1;

        this.drawTexturedModalRect(x, y, x + tileSize, y2, u, v, uMidLeft, v2);
        this.drawTexturedModalRect(x + tileSize, y, x2 - tileSize + 1, y2, uMidLeft, v, uMidRight, v2);
        this.drawTexturedModalRect(x2 - tileSize + 1, y, x2, y2, uMidRight, v, u2, v2);
    }

    /**
     * Draws a tesselated rectangle where the texture is stretched vertically and horizontally but the
     * middle pixels are repeated whilst the joining pixels are stretched.
     *
     * @param x Left edge X coordinate
     * @param y Top edge Y coordinate
     * @param x2 Right edge X coordinate
     * @param y2 Bottom edge Y coordinate
     * @param u U coordinate
     * @param v V coordinate
     * @param u2 Right edge U coordinate
     * @param v2 Bottom edge V coordinate
     */
    public void drawTessellatedModalBorderRect(int x, int y, int x2, int y2, int u, int v, int u2,
                                               int v2) {
        this.drawTessellatedModalBorderRect(x, y, x2, y2, u, v, u2, v2,
                Math.min(((x2 - x) / 2) - 1, ((y2 - y) / 2) - 1));
    }

    /**
     * Draws a tesselated rectangle where the texture is stretched vertically and horizontally but the
     * middle pixels are repeated whilst the joining pixels are stretched. Bordersize specifies the
     * portion of the texture which will remain unstretched.
     *
     * @param x Left edge X coordinate
     * @param y Top edge Y coordinate
     * @param x2 Right edge X coordinate
     * @param y2 Bottom edge Y coordinate
     * @param u U coordinate
     * @param v V coordinate
     * @param u2 Right edge U coordinate
     * @param v2 Bottom edge V coordinate
     * @param borderSize Number of pixels to leave unstretched, must be less than half of the width or
     *        height (whichever is smallest)
     */
    public void drawTessellatedModalBorderRect(int x, int y, int x2, int y2, int u, int v, int u2,
                                               int v2, int borderSize) {
        int tileSize = Math.min(((u2 - u) / 2) - 1, ((v2 - v) / 2) - 1);

        int ul = u + tileSize, ur = u2 - tileSize, vt = v + tileSize, vb = v2 - tileSize;
        int xl = x + borderSize, xr = x2 - borderSize, yt = y + borderSize, yb = y2 - borderSize;

        this.drawTexturedModalRect(x, y, xl, yt, u, v, ul, vt);
        this.drawTexturedModalRect(xl, y, xr, yt, ul, v, ur, vt);
        this.drawTexturedModalRect(xr, y, x2, yt, ur, v, u2, vt);
        this.drawTexturedModalRect(x, yb, xl, y2, u, vb, ul, v2);
        this.drawTexturedModalRect(xl, yb, xr, y2, ul, vb, ur, v2);
        this.drawTexturedModalRect(xr, yb, x2, y2, ur, vb, u2, v2);
        this.drawTexturedModalRect(x, yt, xl, yb, u, vt, ul, vb);
        this.drawTexturedModalRect(xr, yt, x2, yb, ur, vt, u2, vb);
        this.drawTexturedModalRect(xl, yt, xr, yb, ul, vt, ur, vb);
    }

    /**
     * Draw a string but cut it off if it's too long to fit in the specified width.
     *
     * @param fontrenderer
     * @param s
     * @param x
     * @param y
     * @param width
     * @param color
     */
    public static void drawStringWithEllipsis(FontRenderer fontrenderer, String s, int x, int y,
                                              int width, int color) {
        if (fontrenderer.getStringWidth(s) <= width) {
            fontrenderer.drawStringWithShadow(s, x, y, color);
        } else if (width < 8) {
            fontrenderer.drawStringWithShadow("..", x, y, color);
        } else {
            String trimmedText = s;

            while (fontrenderer.getStringWidth(trimmedText) > width - 8 && trimmedText.length() > 0)
                trimmedText = trimmedText.substring(0, trimmedText.length() - 1);

            fontrenderer.drawStringWithShadow(trimmedText + "...", x, y, color);
        }
    }

    /**
     * @param boundingBox
     */
    @SuppressWarnings("cast")
    protected void drawCrossHair(int x, int y, int size, int width, int color) {
        float alpha = (float) (color >> 24 & 0xff) / 255F;
        float red = (float) (color >> 16 & 0xff) / 255F;
        float green = (float) (color >> 8 & 0xff) / 255F;
        float blue = (float) (color & 0xff) / 255F;

        glLineWidth(GuiControl.guiScaleFactor * width);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glColor4f(red, green, blue, alpha);
        glEnable(GL_COLOR_LOGIC_OP);
        glLogicOp(GL_OR_REVERSE);

        // Draw the frame
        Tessellator tessellator = Tessellator.getInstance();

        tessellator.getWorldRenderer().startDrawing(GL_LINES);
        tessellator.getWorldRenderer().addVertex(x - size, y, 0);
        tessellator.getWorldRenderer().addVertex(x + size, y, 0);
        tessellator.draw();

        tessellator.getWorldRenderer().startDrawing(GL_LINES);
        tessellator.getWorldRenderer().addVertex(x, y - size, 0);
        tessellator.getWorldRenderer().addVertex(x, y + size, 0);
        tessellator.draw();

        glDisable(GL_COLOR_LOGIC_OP);
        glEnable(GL_TEXTURE_2D);
    }

    protected void drawRotText(FontRenderer fontRenderer, String text, int xPosition, int yPosition,
                               int color, boolean colorOrOp) {
        if (colorOrOp) {
            glEnable(GL_COLOR_LOGIC_OP);
            glLogicOp(GL_OR_REVERSE);
        }

        int textWidth = fontRenderer.getStringWidth(text) / 2;

        glPushMatrix();
        glTranslatef(xPosition, yPosition, 0);
        glRotatef(-90, 0, 0, 1);
        glTranslatef(-textWidth, -4, 0);

        fontRenderer.drawString(text, 0, 0, color);

        glPopMatrix();

        if (colorOrOp) {
            glDisable(GL_COLOR_LOGIC_OP);
            glEnable(GL_TEXTURE_2D);
        }
    }

    /**
     * Draw a tooltip at the specified location and clip to screenWidth and screenHeight.
     *
     * @param fontRenderer
     * @param tooltipText
     * @param mouseX
     * @param mouseY
     * @param screenWidth
     * @param screenHeight
     * @param color
     * @param backgroundColor
     */
    protected void drawTooltip(FontRenderer fontRenderer, String tooltipText, int mouseX, int mouseY,
                               int screenWidth, int screenHeight, int color, int backgroundColor) {
        int textSize = fontRenderer.getStringWidth(tooltipText);
        mouseX = Math.max(0, Math.min(screenWidth - textSize - 6, mouseX - 6));
        mouseY = Math.max(0, Math.min(screenHeight - 16, mouseY - 18));

        drawRect(mouseX, mouseY, mouseX + textSize + 6, mouseY + 16, backgroundColor);
        this.drawString(fontRenderer, tooltipText, mouseX + 3, mouseY + 4, color);
    }

    /**
     * Draws a textured rectangle with custom UV coordinates.
     *
     * @param x Left edge X coordinate
     * @param y Top edge Y coordinate
     * @param x2 Right edge X coordinate
     * @param y2 Bottom edge Y coordinate
     * @param u U coordinate
     * @param v V coordinate
     * @param u2 Right edge U coordinate
     * @param v2 Bottom edge V coordinate
     */
    @SuppressWarnings("cast")
    public void drawTexturedModalRect(int x, int y, int x2, int y2, int u, int v, int u2, int v2) {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().addVertexWithUV(x, y2, this.zLevel, (float) (u) * texMapScale, (float) (v2)
                * texMapScale);
        tessellator.getWorldRenderer().addVertexWithUV(x2, y2, this.zLevel, (float) (u2) * texMapScale, (float) (v2)
                * texMapScale);
        tessellator.getWorldRenderer().addVertexWithUV(x2, y, this.zLevel, (float) (u2) * texMapScale, (float) (v)
                * texMapScale);
        tessellator.getWorldRenderer().addVertexWithUV(x, y, this.zLevel, (float) (u) * texMapScale, (float) (v)
                * texMapScale);
        tessellator.draw();
    }

    /**
     * Draws a textured rectangle with custom UV coordinates.
     *
     * @param x Left edge X coordinate
     * @param y Top edge Y coordinate
     * @param x2 Right edge X coordinate
     * @param y2 Bottom edge Y coordinate
     * @param u U coordinate
     * @param v V coordinate
     * @param u2 Right edge U coordinate
     * @param v2 Bottom edge V coordinate
     */
    public void drawTexturedModalRectF(int x, int y, int x2, int y2, float u, float v, float u2,
                                       float v2) {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().addVertexWithUV(x, y2, this.zLevel, u, v2);
        tessellator.getWorldRenderer().addVertexWithUV(x2, y2, this.zLevel, u2, v2);
        tessellator.getWorldRenderer().addVertexWithUV(x2, y, this.zLevel, u2, v);
        tessellator.getWorldRenderer().addVertexWithUV(x, y, this.zLevel, u, v);
        tessellator.draw();
    }

    /**
     * Draws a textured rectangle with the specified texture map size.
     *
     * @param x Left edge X coordinate
     * @param y Top edge Y coordinate
     * @param u Texture U coordinate
     * @param v Texture V coordinate
     * @param width Width
     * @param height Height
     * @param texMapScale Texture map scale for scaling UV coordinate
     */
    @SuppressWarnings("cast")
    public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height,
                                      float texMapScale) {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().addVertexWithUV(x + 0, y + height, this.zLevel, (float) (u + 0) * texMapScale,
                (float) (v + height) * texMapScale);
        tessellator.getWorldRenderer().addVertexWithUV(x + width, y + height, this.zLevel, (float) (u + width)
                * texMapScale, (float) (v + height) * texMapScale);
        tessellator.getWorldRenderer().addVertexWithUV(x + width, y + 0, this.zLevel, (float) (u + width) * texMapScale,
                (float) (v + 0) * texMapScale);
        tessellator.getWorldRenderer().addVertexWithUV(x + 0, y + 0, this.zLevel, (float) (u + 0) * texMapScale,
                (float) (v + 0) * texMapScale);
        tessellator.draw();
    }

    public static void setScreenSizeAndScale(int width, int height, int scaleFactor) {
        GuiControl.lastScreenWidth = width;
        GuiControl.lastScreenHeight = height;
        GuiControl.guiScaleFactor = scaleFactor;
    }
}