package growthcraft.lib.common.block;

import growthcraft.core.shared.Reference;
import growthcraft.lib.common.block.rope.IBlockRope;
import growthcraft.lib.utils.BlockStateUtils;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;
import java.util.Random;

/**
 * A CropsBlock that grows along a RopeBlock
 *
 * @author Alatyami
 * @since 5.0.0
 */
@SuppressWarnings("java:S1874")
public class GrowthcraftCropsRopeBlock extends BushBlock implements IBlockRope, IGrowable {

    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;

    protected static VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 5.0D, 10.0D),
            Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 5.0D, 10.0D),
            Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 5.0D, 10.0D),
            Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 5.0D, 10.0D),
            Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D),
            Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D),
            Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D),
            Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D)};

    private Item seedsItem;

    public GrowthcraftCropsRopeBlock() {
        this(getInitProperties());
    }

    public GrowthcraftCropsRopeBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState()
                .with(AGE, 0)
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false));
    }

    private static Properties getInitProperties() {
        Properties properties = Properties.create(Material.PLANTS);
        properties.tickRandomly();
        properties.hardnessAndResistance(0.2F, 0.2F);
        properties.sound(SoundType.PLANT);
        properties.notSolid();
        return properties;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        worldIn.setBlockState(pos, getActualBlockState(worldIn, pos), 2);
    }

    protected static float getGrowthChance(Block blockIn, IBlockReader worldIn, BlockPos pos) {
        float f = 1.0F;
        BlockPos blockpos = pos.down();

        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                float f1 = 0.0F;
                BlockState blockstate = worldIn.getBlockState(blockpos.add(i, 0, j));
                if (blockstate.canSustainPlant(worldIn, blockpos.add(i, 0, j), net.minecraft.util.Direction.UP, (net.minecraftforge.common.IPlantable) blockIn)) {
                    f1 = 1.0F;
                    if (blockstate.isFertile(worldIn, blockpos.add(i, 0, j))) {
                        f1 = 3.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        BlockPos blockpos1 = pos.north();
        BlockPos blockpos2 = pos.south();
        BlockPos blockpos3 = pos.west();
        BlockPos blockpos4 = pos.east();
        boolean flag = blockIn == worldIn.getBlockState(blockpos3).getBlock() || blockIn == worldIn.getBlockState(blockpos4).getBlock();
        boolean flag1 = blockIn == worldIn.getBlockState(blockpos1).getBlock() || blockIn == worldIn.getBlockState(blockpos2).getBlock();
        if (flag && flag1) {
            f /= 2.0F;
        } else {
            boolean flag2 = blockIn == worldIn.getBlockState(blockpos3.north()).getBlock() || blockIn == worldIn.getBlockState(blockpos4.north()).getBlock() || blockIn == worldIn.getBlockState(blockpos4.south()).getBlock() || blockIn == worldIn.getBlockState(blockpos3.south()).getBlock();
            if (flag2) {
                f /= 2.0F;
            }
        }

        return f;
    }

    @Override
    public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE, NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    public void withSeedsItem(Item seedsItem) {
        this.seedsItem = seedsItem;
    }

    public IItemProvider getSeedsItem() {
        assert seedsItem != null;
        return seedsItem;
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return new ItemStack(this.getSeedsItem());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE_BY_AGE[state.get(this.getAgeProperty())];
    }

    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    public boolean isMaxAge(BlockState state) {
        return state.get(this.getAgeProperty()) >= this.getMaxAge();
    }

    public int getMaxAge() {
        return 7;
    }

    protected int getAge(BlockState state) {
        return state.get(this.getAgeProperty());
    }

    public BlockState withAge(int age) {
        return this.getDefaultState().with(this.getAgeProperty(), age);
    }

    @Override
    public boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return state.getBlock() == Blocks.FARMLAND || state.getBlock() instanceof GrowthcraftCropsRopeBlock;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        return !this.isMaxAge(state);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        super.tick(state, worldIn, pos, rand);
        if (!worldIn.isAreaLoaded(pos, 1))
            return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (worldIn.getLightSubtracted(pos, 0) >= 9) {
            int i = this.getAge(state);
            if (i < this.getMaxAge()) {
                float f = getGrowthChance(this, worldIn, pos);
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((int) (25.0F / f) + 1) == 0)) {
                    grow(worldIn, rand, pos, state);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
                }
            }
        }
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        this.grow(worldIn, pos, state);
    }

    public void grow(World worldIn, BlockPos pos, BlockState state) {

        int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }

        worldIn.setBlockState(pos, getActualBlockStateWithAge(worldIn, pos, i), 2);

        if (i == this.getMaxAge()) {
            // then we need to try and spawn another crop above.
            Tag<Block> tagRope = BlockTags.getCollection().getOrCreate(Reference.TAG_ROPE);
            if (tagRope.contains(worldIn.getBlockState(pos.up()).getBlock())
                    && !(worldIn.getBlockState(pos.up()).getBlock() instanceof GrowthcraftCropsRopeBlock)) {
                worldIn.setBlockState(pos.up(), this.getActualBlockStateWithAge(worldIn, pos.up(), 0));
            }
        }
    }

    protected int getBonemealAgeIncrease(World worldIn) {
        return MathHelper.nextInt(worldIn.rand, 1, 3);
    }

    public BlockState getActualBlockStateWithAge(World world, BlockPos blockPos, int age) {
        Map<String, Block> blockMap = BlockStateUtils.getSurroundingBlocks(world, blockPos);

        return this.getDefaultState()
                .with(NORTH, canBeConnectedTo(blockMap.get("north")))
                .with(EAST, canBeConnectedTo(blockMap.get("east")))
                .with(SOUTH, canBeConnectedTo(blockMap.get("south")))
                .with(WEST, canBeConnectedTo(blockMap.get("west")))
                .with(UP, canBeConnectedTo(blockMap.get("up")))
                .with(DOWN, canBeConnectedTo(blockMap.get("down")))
                .with(AGE, age);
    }

    public BlockState getActualBlockState(World world, BlockPos blockPos) {
        return getActualBlockStateWithAge(world, blockPos, world.getBlockState(blockPos).get(this.getAgeProperty()));
    }

    @Override
    public boolean canBeConnectedTo(BlockState state, IBlockReader world, BlockPos pos, Direction facing) {
        Block connectingBlock = state.getBlock();
        return canBeConnectedTo(connectingBlock);
    }

    private boolean canBeConnectedTo(Block block) {
        Tag<Block> tagRope = BlockTags.getCollection().getOrCreate(Reference.TAG_ROPE);
        return tagRope.contains(block) || block instanceof IBlockRope;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        worldIn.setBlockState(pos, getActualBlockStateWithAge(worldIn, pos, worldIn.getBlockState(pos).get(this.getAgeProperty())), 3);
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

}
