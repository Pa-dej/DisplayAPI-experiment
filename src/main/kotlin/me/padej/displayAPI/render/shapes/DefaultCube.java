package me.padej.displayAPI.render.shapes;

import me.padej.displayAPI.utils.AlignmentType;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public abstract class DefaultCube extends DefaultDisplay {
    private float scale;
    private BlockData block;
    private BlockDisplay blockDisplay;

    private AlignmentType alignmentType;

    public DefaultCube(float scale, BlockData block, AlignmentType alignmentType) {
        this.scale = scale;
        this.block = block;
        this.alignmentType = alignmentType;
    }

    public AlignmentType getAlignmentType() {
        return alignmentType;
    }

    public void setAlignmentType(AlignmentType alignmentType) {
        this.alignmentType = alignmentType;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public BlockData getBlock() {
        return block;
    }

    public void setBlock(BlockData block) {
        this.block = block;
    }

    public BlockDisplay getBlockDisplay() {
        return blockDisplay;
    }

    public Transformation getTransformation() {
        return blockDisplay != null ? blockDisplay.getTransformation() : emptyTransformation;
    }

    public Location getLocation() {
        return blockDisplay != null ? blockDisplay.getLocation() : null;
    }

    public BlockDisplay spawn(Location spawnLocation) {
        if (blockDisplay != null && !blockDisplay.isDead()) {
            blockDisplay.remove();
            blockDisplay = null;
            return null;
        }
        blockDisplay = (BlockDisplay) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.BLOCK_DISPLAY);
        blockDisplay.setBlock(getBlock());
        blockDisplay.setRotation(0, 0);
        Vector3f offset = getOffset(alignmentType, getScale());
        blockDisplay.setTransformation(new Transformation(
                offset,
                new AxisAngle4f(),
                new Vector3f(getScale(), getScale(), getScale()),
                new AxisAngle4f()
        ));
        blockDisplay.setInterpolationDuration(1);
        blockDisplay.setTeleportDuration(1);

        return blockDisplay;
    }
}

