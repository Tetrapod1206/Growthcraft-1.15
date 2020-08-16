package growthcraft.lib.common.block;

import growthcraft.lib.common.block.rope.IBlockRope;
import growthcraft.lib.utils.BlockStateUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Map;

public class RopeBlock extends Block implements IBlockRope, IWaterLoggable {

    /*
        Types of natural fiber rope:
        - Jute (plant)
        - Cotton (cotton)
        - Wool (16 colors, made from wool blocks)
        - Linen (strings) DEFAULT
        - Hemp (plant)
        - Straw (2 Hay bails -> Straw)
        - Sisal (plant)
        - Coir (coconut fibers)
     */

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    private static final VoxelShape KNOT_BOUNDING_BOX = makeCuboidShape(7.0D, 7.0D, 7.0D, 9.0D, 9.0D, 9.0D);
    private static final VoxelShape NORTH_BOUNDING_BOX = makeCuboidShape(7.0D, 7.0D, 0.0D, 9.0D, 9.0D, 7.0D);
    private static final VoxelShape EAST_BOUNDING_BOX = makeCuboidShape(9.0D, 7.0D, 7.0D, 16.0D, 9.0D, 9.0D);
    private static final VoxelShape SOUTH_BOUNDING_BOX = makeCuboidShape(7.0D, 7.0D, 9.0D, 9.0D, 9.0D, 16.0D);
    private static final VoxelShape WEST_BOUNDING_BOX = makeCuboidShape(0.0D, 7.0D, 7.0D, 7.0D, 9.0D, 9.0D);
    private static final VoxelShape UP_BOUNDING_BOX = makeCuboidShape(7.0D, 9.0D, 7.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape DOWN_BOUNDING_BOX = makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 7.0D, 9.0D);

    public RopeBlock() {
        this(getInitProperties(Material.WOOL));
    }

    public RopeBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState()
                .with(NORTH, Boolean.valueOf(false))
                .with(EAST, Boolean.valueOf(false))
                .with(SOUTH, Boolean.valueOf(false))
                .with(WEST, Boolean.valueOf(false))
                .with(UP, Boolean.valueOf(false))
                .with(DOWN, Boolean.valueOf(false))
                .with(WATERLOGGED, Boolean.valueOf(false))
        );
    }

    private static Properties getInitProperties(Material material) {
        Properties properties = Properties.create(material);
        properties.sound(SoundType.CLOTH);
        return properties;
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader worldReader, BlockPos pos, BlockPos neighbor) {
        if (!worldReader.isRemote()) {
            World world = (World) worldReader;
        }

    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        ArrayList<VoxelShape> voxelShapeArrayList = new ArrayList<VoxelShape>();
        Map<String, Block> blockMap = BlockStateUtils.getSurroundingBlocks(worldIn, pos);

        if (blockMap.get("north") instanceof RopeBlock) voxelShapeArrayList.add(NORTH_BOUNDING_BOX);
        if (blockMap.get("east") instanceof RopeBlock) voxelShapeArrayList.add(EAST_BOUNDING_BOX);
        if (blockMap.get("south") instanceof RopeBlock) voxelShapeArrayList.add(SOUTH_BOUNDING_BOX);
        if (blockMap.get("west") instanceof RopeBlock) voxelShapeArrayList.add(WEST_BOUNDING_BOX);
        if (blockMap.get("up") instanceof RopeBlock) voxelShapeArrayList.add(UP_BOUNDING_BOX);
        if (blockMap.get("down") instanceof RopeBlock) voxelShapeArrayList.add(DOWN_BOUNDING_BOX);

        VoxelShape[] voxelShapes = new VoxelShape[voxelShapeArrayList.size()];
        voxelShapes = voxelShapeArrayList.toArray(voxelShapes);

        return VoxelShapes.or(KNOT_BOUNDING_BOX, voxelShapes);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getPos();
        return getActualBlockState(context.getWorld(), context.getPos());
    }

    private BlockState getActualBlockState(World world, BlockPos blockPos) {
        Map<String, Block> blockMap = BlockStateUtils.getSurroundingBlocks(world, blockPos);
        IFluidState ifluidstate = world.getFluidState(blockPos);

        BlockState blockState = this.getDefaultState()
                .with(NORTH, blockMap.get("north") instanceof RopeBlock)
                .with(EAST, blockMap.get("east") instanceof RopeBlock)
                .with(SOUTH, blockMap.get("south") instanceof RopeBlock)
                .with(WEST, blockMap.get("west") instanceof RopeBlock)
                .with(UP, blockMap.get("up") instanceof RopeBlock)
                .with(DOWN, blockMap.get("down") instanceof RopeBlock)
                .with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
        return blockState;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        worldIn.setBlockState(pos, getActualBlockState(worldIn, pos), 3);
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    @Override
    public boolean canBeConnectedTo(BlockState state, IBlockReader world, BlockPos pos, Direction facing) {
        Block connectingBlock = state.getBlock();
        return connectingBlock instanceof RopeBlock;
    }

    @SuppressWarnings("deprecation")
    public IFluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
    }

}
