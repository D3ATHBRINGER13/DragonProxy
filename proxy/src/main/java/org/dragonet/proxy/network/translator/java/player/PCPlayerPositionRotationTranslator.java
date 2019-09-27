/*
 * DragonProxy
 * Copyright (C) 2016-2019 Dragonet Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You can view the LICENSE file for more details.
 *
 * @author Dragonet Foundation
 * @link https://github.com/DragonetMC/DragonProxy
 */
package org.dragonet.proxy.network.translator.java.player;

import com.flowpowered.math.vector.Vector3f;
import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.nukkitx.protocol.bedrock.data.ItemData;
import com.nukkitx.protocol.bedrock.packet.AddPlayerPacket;
import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityDataPacket;
import lombok.extern.log4j.Log4j2;
import org.dragonet.proxy.data.entity.EntityType;
import org.dragonet.proxy.network.session.ProxySession;
import org.dragonet.proxy.network.session.cache.object.CachedEntity;
import org.dragonet.proxy.network.session.cache.object.CachedPlayer;
import org.dragonet.proxy.network.translator.PacketTranslator;

@Log4j2
public class PCPlayerPositionRotationTranslator implements PacketTranslator<ServerPlayerPositionRotationPacket> {
    public static final PCPlayerPositionRotationTranslator INSTANCE = new PCPlayerPositionRotationTranslator();

    @Override
    public void translate(ProxySession session, ServerPlayerPositionRotationPacket packet) {
        log.trace("GOT player position x=" + packet.getX() + ", y=" + packet.getY() + ", z=" + packet.getZ());

        CachedEntity cachedEntity = session.getCachedEntity();

        if (!cachedEntity.isSpawned()) {
            cachedEntity.moveAbsolute(new Vector3f(packet.getX(), packet.getY() + EntityType.PLAYER.getOffset() + 0.1f, packet.getZ()), packet.getPitch(), packet.getYaw());

            SetEntityDataPacket entityDataPacket = new SetEntityDataPacket();
            entityDataPacket.setRuntimeEntityId(1);
            entityDataPacket.getMetadata().putAll(cachedEntity.getMetadata());
            session.getBedrockSession().sendPacket(entityDataPacket);

            MovePlayerPacket movePlayerPacket = new MovePlayerPacket();
            movePlayerPacket.setRuntimeEntityId(1);
            movePlayerPacket.setPosition(new Vector3f(packet.getX(), packet.getY() + EntityType.PLAYER.getOffset() + 0.1f, packet.getZ()));
            movePlayerPacket.setRotation(new Vector3f(packet.getPitch(), packet.getYaw(), 0));
            movePlayerPacket.setMode(MovePlayerPacket.Mode.RESET);
            movePlayerPacket.setOnGround(true);
            cachedEntity.setShouldMove(false);

            session.getBedrockSession().sendPacket(movePlayerPacket);
            cachedEntity.setSpawned(true);

            log.info("Spawned player " + session.getUsername() + " at " + packet.getX() + " " + packet.getY() + " " + packet.getZ());
            return;
        }

        cachedEntity.moveAbsolute(new Vector3f(packet.getX(), packet.getY() + EntityType.PLAYER.getOffset() + 0.1f, packet.getZ()), packet.getPitch(), packet.getYaw());

        MovePlayerPacket movePlayerPacket = new MovePlayerPacket();
        movePlayerPacket.setRuntimeEntityId(1);
        movePlayerPacket.setPosition(new Vector3f(packet.getX(), packet.getY() + EntityType.PLAYER.getOffset() + 0.01f, packet.getZ()));
        movePlayerPacket.setRotation(new Vector3f(packet.getPitch(), packet.getYaw(), 0));
        movePlayerPacket.setMode(MovePlayerPacket.Mode.NORMAL);
        movePlayerPacket.setOnGround(true);

        cachedEntity.setShouldMove(false);

        session.getBedrockSession().sendPacket(movePlayerPacket);
        cachedEntity.setSpawned(true);

        ClientTeleportConfirmPacket teleportConfirmPacket = new ClientTeleportConfirmPacket(packet.getTeleportId());
        session.getDownstream().getSession().send(teleportConfirmPacket);
    }
}
