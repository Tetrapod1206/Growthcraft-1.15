package growthcraft.apples.init;

import growthcraft.apples.common.block.BlockAppleStairs;
import growthcraft.apples.shared.Reference;
import growthcraft.apples.shared.UnlocalizedName;
import growthcraft.core.Growthcraft;
import growthcraft.lib.common.block.GrowthcraftButtonBlock;
import growthcraft.lib.common.block.GrowthcraftFenceBlock;
import growthcraft.lib.common.block.GrowthcraftLogBlock;
import growthcraft.lib.common.block.GrowthcraftPlankBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class GrowthcraftApplesBlocks {

    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, Reference.MODID);

    // public static Block definitions.
    public static final RegistryObject<GrowthcraftButtonBlock> applePlankButton;
    public static final RegistryObject<GrowthcraftPlankBlock> applePlank;
    // TODO[43]: Implement applePlankBoat;
    public static final RegistryObject<GrowthcraftFenceBlock> applePlankFence;
    // TODO[36]: Implement applePlankFenceGate;
    // TODO[38]: Implement applePlankFenceRope;
    // TODO[44]: Implement applePlankPressurePlate;
    // TODO[41]: Implement applePlankSign;
    // TODO[32]: Implement applePlankSlab;
    public static final RegistryObject<BlockAppleStairs> applePlankStairs;
    // TODO[42]: Implement applePlankTrapdoor;
    // TODO[39]: **Implement appleTreeLeaves;
    // TODO[34]: **Implement appleTreeSapling;
    // TODO[33]: Implement appleWoodDoor;
    // TODO[45]: **Implement appleWood;
    public static final RegistryObject<GrowthcraftLogBlock> appleWood;
    // TODO[46]: Implement appleWoodStripped;
    // TODO[37]: **Implement appleWoodLog;
    public static final RegistryObject<GrowthcraftLogBlock> appleWoodLog;
    // TODO[47]: Implement appleWoodLogStripped;

    // Static initializer for Growthcraft Apples blocks.
    static {
        applePlankButton = BLOCKS.register(
                UnlocalizedName.APPLE_PLANK_BUTTON,
                () -> new GrowthcraftButtonBlock(UnlocalizedName.APPLE_PLANK_BUTTON));
        applePlank = BLOCKS.register(
                UnlocalizedName.APPLE_PLANK,
                () -> new GrowthcraftPlankBlock(UnlocalizedName.APPLE_PLANK));
        applePlankFence = BLOCKS.register(
                UnlocalizedName.APPLE_PLANK_FENCE,
                () -> new GrowthcraftFenceBlock(UnlocalizedName.APPLE_PLANK_FENCE));
        applePlankStairs = BLOCKS.register(
                UnlocalizedName.APPLE_PLANK_STAIRS,
                () -> new BlockAppleStairs(Blocks.OAK_PLANKS, UnlocalizedName.APPLE_PLANK_STAIRS));
        appleWood = BLOCKS.register(
                UnlocalizedName.APPLE_WOOD,
                () -> new GrowthcraftLogBlock(UnlocalizedName.APPLE_WOOD));
        appleWoodLog = BLOCKS.register(
                UnlocalizedName.APPLE_WOOD_LOG,
                () -> new GrowthcraftLogBlock(UnlocalizedName.APPLE_WOOD_LOG));
    }

    private GrowthcraftApplesBlocks() { /* Prevent Default Public Constructor */ }

    /**
     * Legacy block registration. Use DeferredRegister<Block> and RegistryObject<T> instead.
     *
     * @param registry Forge Block Registry
     * @deprecated use GrowthcraftBambooBlocks.BLOCKS deferred registry instead.
     */
    @Deprecated
    public static void registerBlocks(IForgeRegistry<Block> registry) {
        Growthcraft.LOGGER.debug("Growthcraft Apples Registering blocks ...");
    }

    /**
     * Dynamically register Growthcraft Apples BlockItems.
     *
     * @param itemRegistry IForgeRegistry<Item> reference for registering items.
     * @param properties   Item properties with item group for creative tab.
     */
    public static void registerBlockItems(IForgeRegistry<Item> itemRegistry, Item.Properties properties) {
        Growthcraft.LOGGER.debug("Growthcraft Apples Registering itemBlocks ...");

        BLOCKS.getEntries().stream()
                .map(RegistryObject::get).forEach(block -> {
            final BlockItem blockItem = new BlockItem(block, properties);
            if (block.getRegistryName() != null) {
                blockItem.setRegistryName(block.getRegistryName());
                itemRegistry.register(blockItem);
            }
        });

    }
}
