package dan200.billund.client.render;

import dan200.billund.client.helper.BrickRenderHelper;
import dan200.billund.shared.block.BillundBlocks;
import dan200.billund.shared.data.Stud;
import dan200.billund.shared.tile.TileEntityBillund;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class BrickBlockRenderer extends TileEntitySpecialRenderer {

    private void renderBrickAt(TileEntityBillund tile, double xc, double yc, double zc, float partial) {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
        GL11.glEnable(GL11.GL_BLEND);

        Tessellator tessellator = Tessellator.instance;

        BrickRenderHelper.translateToWorldCoords(Minecraft.getMinecraft().renderViewEntity, partial);

        for (int x = 0; x < TileEntityBillund.STUDS_PER_ROW; ++x) {
            for (int y = 0; y < TileEntityBillund.STUDS_PER_COLUMN; ++y) {
                for (int z = 0; z < TileEntityBillund.STUDS_PER_ROW; ++z) {
                    Stud stud = tile.getStudLocal(x, y, z);
                    if (stud != null && !stud.illuminated) {
                        int sx = tile.xCoord * TileEntityBillund.STUDS_PER_ROW + x;
                        int sy = tile.yCoord * TileEntityBillund.STUDS_PER_COLUMN + y;
                        int sz = tile.zCoord * TileEntityBillund.STUDS_PER_ROW + z;
                        if (stud.xOrigin == sx && stud.yOrigin == sy && stud.zOrigin == sz) {
                            // Draw the brick
                            int brightness = BillundBlocks.billund.getMixedBrightnessForBlock(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);

                            tessellator.startDrawingQuads();

                            BrickRenderHelper.renderBrick(
                                    tile.getWorldObj(), brightness, stud.illuminated, stud.smooth, stud.color, stud.transparent ? 0.8F : 1.0F,
                                    sx, sy, sz, stud.brickWidth, stud.brickHeight, stud.brickDepth
                            );

                            tessellator.draw();
                        }
                    }
                }
            }
        }

        char c0 = 61680;
        int j = c0 % 65536;
        int k = c0 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        for (int x = 0; x < TileEntityBillund.STUDS_PER_ROW; ++x) {
            for (int y = 0; y < TileEntityBillund.STUDS_PER_COLUMN; ++y) {
                for (int z = 0; z < TileEntityBillund.STUDS_PER_ROW; ++z) {
                    Stud stud = tile.getStudLocal(x, y, z);
                    if (stud != null && stud.illuminated) {
                        int sx = tile.xCoord * TileEntityBillund.STUDS_PER_ROW + x;
                        int sy = tile.yCoord * TileEntityBillund.STUDS_PER_COLUMN + y;
                        int sz = tile.zCoord * TileEntityBillund.STUDS_PER_ROW + z;
                        if (stud.xOrigin == sx && stud.yOrigin == sy && stud.zOrigin == sz) {
                            // Draw the brick
                            int brightness = BillundBlocks.billund.getMixedBrightnessForBlock(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);

                            tessellator.startDrawingQuads();

                            BrickRenderHelper.renderBrick(
                                    tile.getWorldObj(), brightness, stud.illuminated, stud.smooth, stud.color, stud.transparent ? 0.8F : 1.0F,
                                    sx, sy, sz, stud.brickWidth, stud.brickHeight, stud.brickDepth
                            );

                            tessellator.draw();
                        }
                    }
                }
            }
        }

        int i = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord, tile.zCoord, 0);
        int j1 = i % 65536;
        int k1 = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j1 / 1.0F, (float)k1 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // Teardown
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partial) {
        renderBrickAt((TileEntityBillund) tile, x, y, z, partial);
    }
}