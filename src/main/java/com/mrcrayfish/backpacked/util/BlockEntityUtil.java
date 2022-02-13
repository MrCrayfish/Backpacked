package com.mrcrayfish.backpacked.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BlockEntityUtil
{
    /**
     * Sends an update packet to clients tracking a tile entity.
     *
     * @param blockEntity the tile entity to update
     */
    public static void sendUpdatePacket(BlockEntity blockEntity)
    {
        Packet<ClientGamePacketListener> packet = blockEntity.getUpdatePacket();
        if(packet != null)
        {
            sendUpdatePacket(blockEntity.getLevel(), blockEntity.getBlockPos(), packet);
        }
    }

    /**
     * Sends an update packet to clients tracking a tile entity with a specific CompoundNBT
     *
     * @param blockEntity the tile entity to update
     */
    public static void sendUpdatePacket(BlockEntity blockEntity, CompoundTag compound)
    {
        Packet<ClientGamePacketListener> packet = ClientboundBlockEntityDataPacket.create(blockEntity, blockEntity1 -> compound);
        sendUpdatePacket(blockEntity.getLevel(), blockEntity.getBlockPos(), packet);
    }

    /**
     * Sends an update packet but only to a specific player. This helps reduce overhead on the network
     * when you only want to update a tile entity for a single player rather than everyone who is
     * tracking the tile entity.
     *
     * @param blockEntity the tile entity to update
     * @param player the player to send the update to
     */
    public static void sendUpdatePacket(BlockEntity blockEntity, ServerPlayer player)
    {
        sendUpdatePacket(blockEntity, blockEntity.getUpdateTag(), player);
    }

    /**
     * Sends an update packet with a custom nbt compound but only to a specific player. This helps
     * reduce overhead on the network when you only want to update a tile entity for a single player
     * rather than everyone who is tracking the tile entity.
     *
     * @param blockEntity the tile entity to update
     * @param compound the update tag to send
     * @param player the player to send the update to
     */
    public static void sendUpdatePacket(BlockEntity blockEntity, CompoundTag compound, ServerPlayer player)
    {
        Packet<ClientGamePacketListener> packet = ClientboundBlockEntityDataPacket.create(blockEntity, blockEntity1 -> compound);
        player.connection.send(packet);
    }

    private static void sendUpdatePacket(Level level, BlockPos pos, Packet<ClientGamePacketListener> packet)
    {
        if(level instanceof ServerLevel serverLevel)
        {
            List<ServerPlayer> players = serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false);
            players.forEach(player -> player.connection.send(packet));
        }
    }
}
