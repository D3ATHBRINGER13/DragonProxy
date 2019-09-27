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
package org.dragonet.proxy.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EntityType {
    PLAYER(63,1.8, 1.6, 1.6200000047683716f),
    SHEEP(13, 1.3f, 0.9f),
    BAT(19, 0.9f, 0.5f),
    ZOMBIE(32, 1.8f, 0.6f, 1.6200000047683716f),
    ENDERMITE(55, 0.3f, 0.4f),
    CREEPER(33, 1.7f, 0.6f, 1.6200000047683716f),
    VILLAGER(15, 1.8f, 0.6f, 1.6200000047683716f),

    ARMOR_STAND(61, 0f);

    private int type;
    private double height;
    private double width;
    private float offset;

    EntityType(int type, double height) {
        this(type, height, 0);
    }

    EntityType(int type, double height, double width) {
        this.type = type;
        this.height = height;
        this.width = width;
    }
}
