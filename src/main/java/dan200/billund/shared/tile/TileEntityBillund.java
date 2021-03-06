/**
 * This file is part of Billund - http://www.computercraft.info/billund
 * Copyright Daniel Ratcliffe, 2013. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.billund.shared.tile;

import dan200.billund.shared.block.BillundBlocks;
import dan200.billund.shared.data.Brick;
import dan200.billund.shared.data.Stud;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TileEntityBillund extends TileEntity {

    // Constants
    public static final int ROWS_PER_BLOCK = 6;
    public static final int LAYERS_PER_BLOCK = 5;

    public static final int STUDS_PER_ROW = ROWS_PER_BLOCK;
    public static final int STUDS_PER_COLUMN = LAYERS_PER_BLOCK;
    public static final int STUDS_PER_LAYER = ROWS_PER_BLOCK * ROWS_PER_BLOCK;
    public static final int STUDS_PER_BLOCK = LAYERS_PER_BLOCK * STUDS_PER_LAYER;

    // Static helpers
    public static Stud getStud(IBlockAccess world, int x, int y, int z) {
        int localX = (x % ROWS_PER_BLOCK + ROWS_PER_BLOCK) % ROWS_PER_BLOCK;
        int localY = (y % LAYERS_PER_BLOCK + LAYERS_PER_BLOCK) % LAYERS_PER_BLOCK;
        int localZ = (z % ROWS_PER_BLOCK + ROWS_PER_BLOCK) % ROWS_PER_BLOCK;
        int blockX = (x - localX) / ROWS_PER_BLOCK;
        int blockY = (y - localY) / LAYERS_PER_BLOCK;
        int blockZ = (z - localZ) / ROWS_PER_BLOCK;

        if (blockY >= 0) {
            Block block = world.getBlock(blockX, blockY, blockZ);
            if (block == BillundBlocks.billund) {
                TileEntity entity = world.getTileEntity(blockX, blockY, blockZ);
                if (entity != null && entity instanceof TileEntityBillund) {
                    TileEntityBillund billund = (TileEntityBillund) entity;
                    return billund.getStudLocal(localX, localY, localZ);
                }
            } else if (!(block.isAir(world, blockX, blockY, blockZ))) {
                Stud fake = new Stud(false, false, false, 0, x, y, z, 1, 1, 1);
                fake.actuallyExists = false;
                return fake;
            }
        }
        return null;
    }

    public static boolean canSetStud(IBlockAccess world, int x, int y, int z) {
        int localX = (x % ROWS_PER_BLOCK + ROWS_PER_BLOCK) % ROWS_PER_BLOCK;
        int localY = (y % LAYERS_PER_BLOCK + LAYERS_PER_BLOCK) % LAYERS_PER_BLOCK;
        int localZ = (z % ROWS_PER_BLOCK + ROWS_PER_BLOCK) % ROWS_PER_BLOCK;
        int blockX = (x - localX) / ROWS_PER_BLOCK;
        int blockY = (y - localY) / LAYERS_PER_BLOCK;
        int blockZ = (z - localZ) / ROWS_PER_BLOCK;

        if (blockY >= 0) {
            Block block = world.getBlock(blockX, blockY, blockZ);
            if (block == BillundBlocks.billund) {
                TileEntity entity = world.getTileEntity(blockX, blockY, blockZ);
                if (entity != null && entity instanceof TileEntityBillund) {
                    TileEntityBillund billund = (TileEntityBillund) entity;
                    return (billund.getStudLocal(localX, localY, localZ) == null);
                }
            } else if (block.isAir(world, blockX, blockY, blockZ)) {
                return true;
            }
        }
        return false;
    }

    public static void setStud(World world, int x, int y, int z, Stud stud) {
        int localX = (x % ROWS_PER_BLOCK + ROWS_PER_BLOCK) % ROWS_PER_BLOCK;
        int localY = (y % LAYERS_PER_BLOCK + LAYERS_PER_BLOCK) % LAYERS_PER_BLOCK;
        int localZ = (z % ROWS_PER_BLOCK + ROWS_PER_BLOCK) % ROWS_PER_BLOCK;
        int blockX = (x - localX) / ROWS_PER_BLOCK;
        int blockY = (y - localY) / LAYERS_PER_BLOCK;
        int blockZ = (z - localZ) / ROWS_PER_BLOCK;

        if (blockY >= 0) {
            Block block = world.getBlock(blockX, blockY, blockZ);
            if (block == BillundBlocks.billund) {
                // Add to existing billund block
                TileEntity entity = world.getTileEntity(blockX, blockY, blockZ);
                if (entity != null && entity instanceof TileEntityBillund) {
                    TileEntityBillund billund = (TileEntityBillund) entity;
                    billund.setStudLocal(localX, localY, localZ, stud);
                }
                world.markBlockForUpdate(blockX, blockY, blockZ);
            } else if (block.isAir(world, blockX, blockY, blockZ)) {
                // Add a new billund block
                if (world.setBlock(blockX, blockY, blockZ, BillundBlocks.billund, 0, 3)) {
                    TileEntity entity = world.getTileEntity(blockX, blockY, blockZ);
                    if (entity != null && entity instanceof TileEntityBillund) {
                        TileEntityBillund billund = (TileEntityBillund) entity;
                        billund.setStudLocal(localX, localY, localZ, stud);
                    }
                    world.markBlockForUpdate(blockX, blockY, blockZ);
                }
            }
        }
    }

    public static boolean canAddBrick(World world, Brick brick) {
        for (int x = brick.xOrigin; x < brick.xOrigin + brick.width; ++x) {
            for (int y = brick.yOrigin; y < brick.yOrigin + brick.height; ++y) {
                for (int z = brick.zOrigin; z < brick.zOrigin + brick.depth; ++z) {
                    if (!canSetStud(world, x, y, z)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void addBrick(World world, Brick brick) {
        for (int x = brick.xOrigin; x < brick.xOrigin + brick.width; ++x) {
            for (int y = brick.yOrigin; y < brick.yOrigin + brick.height; ++y) {
                for (int z = brick.zOrigin; z < brick.zOrigin + brick.depth; ++z) {
                    Stud stud = new Stud(brick, x - brick.xOrigin, y - brick.yOrigin, z - brick.zOrigin);
                    setStud(world, x, y, z, stud);
                }
            }
        }
    }

    public static void removeBrick(World world, Brick brick) {
        for (int x = brick.xOrigin; x < brick.xOrigin + brick.width; ++x) {
            for (int y = brick.yOrigin; y < brick.yOrigin + brick.height; ++y) {
                for (int z = brick.zOrigin; z < brick.zOrigin + brick.depth; ++z) {
                    setStud(world, x, y, z, null);
                }
            }
        }
    }

    public static class StudRaycastResult {
        public int hitX;
        public int hitY;
        public int hitZ;
        public int hitSide;
    }

    public static StudRaycastResult raycastStuds(World world, Vec3 origin, Vec3 direction, float distance) {
        float xScale = (float) ROWS_PER_BLOCK;
        float yScale = (float) LAYERS_PER_BLOCK;
        float zScale = (float) ROWS_PER_BLOCK;

        float x = (float) origin.xCoord * xScale;
        float y = (float) origin.yCoord * yScale;
        float z = (float) origin.zCoord * zScale;
        int sx = (int) Math.floor(x);
        int sy = (int) Math.floor(y);
        int sz = (int) Math.floor(z);
        x -= (float) sx;
        y -= (float) sy;
        z -= (float) sz;

        float dx = (float) direction.xCoord;
        float dy = (float) direction.yCoord * (yScale / xScale);
        float dz = (float) direction.zCoord;
        {
            float dLen = (float) Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
            dx /= dLen;
            dy /= dLen;
            dz /= dLen;
            distance *= xScale;
        }

        int stepX = 0;
        int stepY = 0;
        int stepZ = 0;
        float edgeX = 0.0f;
        float edgeY = 0.0f;
        float edgeZ = 0.0f;
        if (dx > 0.0f) {
            stepX = 1;
            edgeX = 1.0f;
        } else if (dx < 0.0f) {
            stepX = -1;
            edgeX = 0.0f;
        }
        if (dy > 0.0f) {
            stepY = 1;
            edgeY = 1.0f;
        } else if (dy < 0.0f) {
            stepY = -1;
            edgeY = 0.0f;
        }
        if (dz > 0.0f) {
            stepZ = 1;
            edgeZ = 1.0f;
        } else if (dz < 0.0f) {
            stepZ = -1;
            edgeZ = 0.0f;
        }

        float distanceLeft = distance;
        Stud hitStud = null;
        int hitSide = -1;
        while (distanceLeft > 0.0f && hitStud == null) {
            float distToEdgeX = 999.0f;
            float distToEdgeY = 999.0f;
            float distToEdgeZ = 999.0f;
            if (stepX != 0) {
                distToEdgeX = (edgeX - x) / dx;
            }
            if (stepY != 0) {
                distToEdgeY = (edgeY - y) / dy;
            }
            if (stepZ != 0) {
                distToEdgeZ = (edgeZ - z) / dz;
            }
            if (distToEdgeX <= distToEdgeY && distToEdgeX <= distToEdgeZ) {
                if (distToEdgeX < distanceLeft) {
                    sx += stepX;
                    x += (distToEdgeX * dx) - (float) stepX;
                    y += distToEdgeX * dy;
                    z += distToEdgeX * dz;
                    distanceLeft -= distToEdgeX;

                    hitStud = getStud(world, sx, sy, sz);
                    hitSide = (stepX > 0) ? 4 : 5;
                } else {
                    x += distToEdgeX * dx;
                    y += distToEdgeX * dy;
                    z += distToEdgeX * dz;
                    distanceLeft = 0.0f;
                }
            } else if (distToEdgeY <= distToEdgeX && distToEdgeY <= distToEdgeZ) {
                if (distToEdgeY < distanceLeft) {
                    sy += stepY;
                    x += distToEdgeY * dx;
                    y += (distToEdgeY * dy) - (float) stepY;
                    z += distToEdgeY * dz;
                    distanceLeft -= distToEdgeY;

                    hitStud = getStud(world, sx, sy, sz);
                    hitSide = (stepY > 0) ? 0 : 1;
                } else {
                    x += distToEdgeY * dx;
                    y += distToEdgeY * dy;
                    z += distToEdgeY * dz;
                    distanceLeft = 0.0f;
                }
            } else {
                if (distToEdgeZ < distanceLeft) {
                    sz += stepZ;
                    x += distToEdgeZ * dx;
                    y += distToEdgeZ * dy;
                    z += (distToEdgeZ * dz) - (float) stepZ;
                    distanceLeft -= distToEdgeZ;

                    hitStud = getStud(world, sx, sy, sz);
                    hitSide = (stepZ > 0) ? 2 : 3;
                } else {
                    x += distToEdgeZ * dx;
                    y += distToEdgeZ * dy;
                    z += distToEdgeZ * dz;
                    distanceLeft = 0.0f;
                }
            }
        }

        if (hitStud != null) {
            StudRaycastResult result = new StudRaycastResult();
            result.hitX = sx;
            result.hitY = sy;
            result.hitZ = sz;
            result.hitSide = hitSide;
            return result;
        }
        return null;
    }

    public boolean globalIllumination;

    // Privates
    private Stud[] m_studs;

    public TileEntityBillund() {
        super();
        m_studs = new Stud[STUDS_PER_BLOCK];
    }

    public Stud getStudLocal(int x, int y, int z) {
        if (x >= 0 && x < STUDS_PER_ROW &&
                y >= 0 && y < STUDS_PER_COLUMN &&
                z >= 0 && z < STUDS_PER_ROW) {
            return m_studs[x + (z * STUDS_PER_ROW) + (y * STUDS_PER_LAYER)];
        }
        return null;
    }

    public void setStudLocal(int x, int y, int z, Stud stud) {
        if (x >= 0 && x < STUDS_PER_ROW &&
                y >= 0 && y < STUDS_PER_COLUMN &&
                z >= 0 && z < STUDS_PER_ROW) {
            m_studs[x + (z * STUDS_PER_ROW) + (y * STUDS_PER_LAYER)] = stud;
            cullOrphans();
        }
    }

    public boolean isEmpty() {
        for (int i = 0; i < STUDS_PER_BLOCK; ++i) {
            if (m_studs[i] != null) {
                return false;
            }
        }
        return true;
    }

    public void cullOrphans() {
        globalIllumination = false;
        boolean changed = false;
        for (int i = 0; i < STUDS_PER_BLOCK; ++i) {
            Stud stud = m_studs[i];
            if (stud != null) {
                Stud origin = getStud(worldObj, stud.xOrigin, stud.yOrigin, stud.zOrigin);
                if (origin == null) {
                    m_studs[i] = null;
                    changed = true;
                } else {
                    if (!globalIllumination && stud.illuminated) {
                        globalIllumination = true;
                        changed = true;
                    }
                }
            }
        }
        if (changed) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbttagcompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);

        nbttagcompound.setBoolean("globalIllumination", globalIllumination);
        for (int i = 0; i < STUDS_PER_BLOCK; ++i) {
            Stud stud = m_studs[i];
            if (stud != null) {
                NBTTagCompound studTag = new NBTTagCompound();
                studTag.setBoolean("i", stud.illuminated);
                studTag.setBoolean("t", stud.transparent);
                studTag.setBoolean("s", stud.smooth);
                studTag.setInteger("c", stud.color);
                studTag.setInteger("x", stud.xOrigin);
                studTag.setInteger("y", stud.yOrigin);
                studTag.setInteger("z", stud.zOrigin);
                studTag.setInteger("w", stud.brickWidth);
                studTag.setInteger("h", stud.brickHeight);
                studTag.setInteger("d", stud.brickDepth);
                nbttagcompound.setTag("s" + i, studTag);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);

        globalIllumination = nbttagcompound.getBoolean("globalIllumination");
        for (int i = 0; i < STUDS_PER_BLOCK; ++i) {
            String key = "s" + i;
            if (nbttagcompound.hasKey(key)) {
                Stud stud = new Stud();
                NBTTagCompound studTag = nbttagcompound.getCompoundTag(key);
                stud.illuminated = studTag.getBoolean("i");
                stud.transparent = studTag.getBoolean("t");
                stud.smooth = studTag.getBoolean("s");
                stud.color = studTag.getInteger("c");
                stud.xOrigin = studTag.getInteger("x");
                stud.yOrigin = studTag.getInteger("y");
                stud.zOrigin = studTag.getInteger("z");
                stud.brickWidth = studTag.getInteger("w");
                stud.brickHeight = studTag.getInteger("h");
                stud.brickDepth = studTag.getInteger("d");
                m_studs[i] = stud;
            } else {
                m_studs[i] = null;
            }
        }
    }
}
