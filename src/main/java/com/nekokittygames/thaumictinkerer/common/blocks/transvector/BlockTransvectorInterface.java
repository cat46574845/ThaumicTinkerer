package com.nekokittygames.thaumictinkerer.common.blocks.transvector;

import com.nekokittygames.thaumictinkerer.common.blocks.TTCamoBlock;
import com.nekokittygames.thaumictinkerer.common.libs.LibBlockNames;
import com.nekokittygames.thaumictinkerer.common.tileentity.TileEntityCamoflage;
import com.nekokittygames.thaumictinkerer.common.tileentity.transvector.TileEntityTransvectorInterface;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTransvectorInterface extends TTCamoBlock<TileEntityTransvectorInterface> {
    public BlockTransvectorInterface() {
        super(LibBlockNames.TRANSVECTOR_INTERFACE,Material.WOOD, true);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityTransvectorInterface();
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {

        TileEntity te=worldIn.getTileEntity(pos);
        if(te instanceof TileEntityTransvectorInterface)
        {
            TileEntityTransvectorInterface tinterface= (TileEntityTransvectorInterface) te;
            TileEntity remote=tinterface.getTile();
            if(remote!=null)
                return remote.getBlockType().getComparatorInputOverride(worldIn.getBlockState(remote.getPos()),worldIn,remote.getPos());
        }
        return super.getComparatorInputOverride(blockState, worldIn, pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (camoflageFromHand(playerIn, hand, te)) return true;
        return false;

    }


}