package oortcloud.hungryanimals.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.EnumProperty;
import net.minecraft.block.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import oortcloud.hungryanimals.core.lib.References;
import oortcloud.hungryanimals.core.lib.Strings;
import oortcloud.hungryanimals.items.ModItems;
import oortcloud.hungryanimals.tileentities.TileEntityTrough;

public class BlockTrough extends Block {

	public static final EnumProperty<EnumPartType> PART = EnumProperty.create("part", EnumPartType.class);
	public static final PropertyDirection FACING = PropertyDirection.create("facing", Direction.Plane.HORIZONTAL);
	public static final AxisAlignedBB BOUND_BOX = new AxisAlignedBB(0F, 0F, 0F, 1F, 0.5F, 1F);
	public static final AxisAlignedBB FLOOR = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
	public static final AxisAlignedBB EAST = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 0.125F, 0.5F, 1.0F);
	public static final AxisAlignedBB WEST = new AxisAlignedBB(0.875F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
	public static final AxisAlignedBB NORTH = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 0.125F);
	public static final AxisAlignedBB SOUTH = new AxisAlignedBB(0.0F, 0.0F, 0.875F, 1.0F, 0.5F, 1.0F);

	private final Random random = new Random();

	protected BlockTrough() {
		super(Material.WOOD);
		setHarvestLevel("axe", 0);
		setHardness(2.0F);

		setUnlocalizedName(References.MODID + "." + Strings.blockTroughName);
		setRegistryName(Strings.blockTroughName);
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, net.minecraft.util.math.BlockPos pos) {
		return BOUND_BOX;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, PART });
	}

	@OnlyIn(Dist.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return ModItems.trough;
	}

	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public EnumPushReaction getMobilityFlag(BlockState state) {
		return EnumPushReaction.IGNORE;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public void onNeighborChange(IBlockAccess worldIn, BlockPos pos, BlockPos neighborBlock) {
		BlockState state = worldIn.getBlockState(pos);
		Direction Direction = (Direction) state.getValue(FACING);

		if (state.getValue(PART) == BlockTrough.EnumPartType.HEAD) {
			if (worldIn.getBlockState(pos.offset(Direction.getOpposite())).getBlock() != this) {
				((World) worldIn).setBlockToAir(pos);
			}
		} else if (worldIn.getBlockState(pos.offset(Direction)).getBlock() != this) {
			((World) worldIn).setBlockToAir(pos);

			if (!((World) worldIn).isRemote) {
				this.dropBlockAsItem((World) worldIn, pos, state, 0);
				TileEntityTrough trough = (TileEntityTrough) worldIn.getTileEntity(pos);
				if (trough != null)
					dropStoredItems((World) worldIn, pos, trough);
			}
		}
	}

	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return state.getValue(PART) == EnumPartType.HEAD ? null : ModItems.trough;
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, BlockState state, float chance, int fortune) {
		if (state.getValue(PART) == BlockTrough.EnumPartType.FOOT) {
			super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
		}
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, EntityPlayer player) {
		// to prevent item drop in creative mode
		if (player.capabilities.isCreativeMode && state.getValue(PART) == BlockTrough.EnumPartType.HEAD) {
			BlockPos blockpos1 = pos.offset(((Direction) state.getValue(FACING)).getOpposite());

			if (worldIn.getBlockState(blockpos1).getBlock() == this) {
				worldIn.setBlockToAir(blockpos1);
			}
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
			@Nullable Entity entityIn, boolean p_185477_7_) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, FLOOR);

		Direction rot = ((Direction) state.getValue(FACING));
		if (state.getValue(PART) == EnumPartType.HEAD)
			rot = rot.getOpposite();

		if (rot != Direction.SOUTH) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH);
		}
		if (rot != Direction.WEST) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST);
		}
		if (rot != Direction.NORTH) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH);
		}
		if (rot != Direction.EAST) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST);
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return state.getValue(PART) == EnumPartType.FOOT;
	}

	@Override
	public TileEntity createTileEntity(World world, BlockState state) {
		return (state.getValue(PART) == EnumPartType.FOOT) ? new TileEntityTrough() : null;
	}

	public TileEntity getTileEntity(World world, BlockPos pos) {
		BlockState meta = world.getBlockState(pos);
		if (meta.getBlock() == this) {
			return (meta.getValue(PART) == EnumPartType.HEAD ? world.getTileEntity(pos.offset(((Direction) meta.getValue(FACING)).getOpposite()))
					: world.getTileEntity(pos));
		} else {
			return null;
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, EntityPlayer playerIn, EnumHand hand, Direction side, float hitX,
			float hitY, float hitZ) {
		TileEntity te = this.getTileEntity(worldIn, pos);

		if (!(te instanceof TileEntityTrough)) {
			return false;
		}

		TileEntityTrough foodbox = (TileEntityTrough) te;
		ItemStack stackinfoodbox = foodbox.stack;
		ItemStack stackinhand = playerIn.getHeldItem(hand);

		if (stackinhand.isEmpty()) {
			if (!stackinfoodbox.isEmpty()) {
				if (playerIn.inventory.addItemStackToInventory(stackinfoodbox)) {
					foodbox.stack = ItemStack.EMPTY;
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		if (stackinfoodbox.isEmpty()) {
			if (stackinhand.getCount() > 16) {
				foodbox.stack = stackinhand.splitStack(16);
			} else {
				foodbox.stack = stackinhand;
				playerIn.setHeldItem(hand, ItemStack.EMPTY);
			}
		} else if (stackinfoodbox.getItem() == stackinhand.getItem()) {
			if (stackinhand.getCount() + stackinfoodbox.getCount() > 16) {
				stackinfoodbox.setCount(16);
				stackinhand.grow(stackinfoodbox.getCount() - 16);
			} else {
				stackinfoodbox.grow(stackinhand.getCount());
				playerIn.setHeldItem(hand, ItemStack.EMPTY);
			}
		} else {
			return false;
		}

		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state) {

		TileEntityTrough trough = (TileEntityTrough) worldIn.getTileEntity(pos);
		if (trough != null)
			dropStoredItems(worldIn, pos, trough);

		super.breakBlock(worldIn, pos, state);
	}

	private void dropStoredItems(World worldIn, BlockPos pos, TileEntityTrough trough) {
		if (trough != null) {
			ItemStack itemstack = trough.stack;
			if (!itemstack.isEmpty()) {
				float f = this.random.nextFloat() * 0.8F + 0.1F;
				float f1 = this.random.nextFloat() * 0.8F + 0.1F;
				float f2 = this.random.nextFloat() * 0.8F + 0.1F;

				while (itemstack.getCount() > 0) {
					int j1 = this.random.nextInt(3) + 3;

					if (j1 > itemstack.getCount()) {
						j1 = itemstack.getCount();
					}

					itemstack.shrink(j1);
					EntityItem entityitem = new EntityItem(worldIn, (double) ((float) pos.getX() + f), (double) ((float) pos.getY() + f1),
							(double) ((float) pos.getZ() + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));

					CompoundNBT tag = itemstack.getTagCompound();
					if (tag != null) {
						entityitem.getItem().setTagCompound(tag.copy());
					}

					float f3 = 0.05F;
					entityitem.motionX = (double) ((float) this.random.nextGaussian() * f3);
					entityitem.motionY = (double) ((float) this.random.nextGaussian() * f3 + 0.2F);
					entityitem.motionZ = (double) ((float) this.random.nextGaussian() * f3);
					worldIn.spawnEntity(entityitem);
				}
			}

		}
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		Direction Direction = Direction.getHorizontal(meta & 3);
		return this.getDefaultState().with(FACING, Direction).with(PART, (meta >> 2 == 1) ? EnumPartType.HEAD : EnumPartType.FOOT);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return ((Direction) state.getValue(FACING)).getHorizontalIndex() + ((state.getValue(PART) == EnumPartType.HEAD ? 1 : 0) << 2);
	}

	public static enum EnumPartType implements IStringSerializable {
		HEAD("head"), FOOT("foot");
		private final String name;

		private EnumPartType(String name) {
			this.name = name;
		}

		public String toString() {
			return this.name;
		}

		public String getName() {
			return this.name;
		}
	}
}
