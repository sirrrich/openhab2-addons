/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.freeathome.internal.handler;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.freeathome.internal.FreeAtHomeBindingConstants;
import org.openhab.binding.freeathome.internal.FreeAtHomeUpdateChannel;
import org.openhab.binding.freeathome.internal.config.FreeAtHomeBinaryConfig;
import org.openhab.binding.freeathome.internal.model.DefaultOnOffTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FreeAtHomeBinarySwitchHandler} represents virtual switch
 *
 * @author Stian Kjoglum - Initial contribution
 */

public class FreeAtHomeBinarySwitchHandler extends FreeAtHomeBaseHandler {

    public FreeAtHomeBinarySwitchHandler(Thing thing) {
        super(thing);
    }

    private Logger logger = LoggerFactory.getLogger(FreeAtHomeBinarySwitchHandler.class);

    private FreeAtHomeBinaryConfig m_Configuration = new FreeAtHomeBinaryConfig();

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        FreeAtHomeBridgeHandler bridge = getFreeAtHomeBridge();

        if (bridge == null) {
            logger.error("No bridge connected");
            return;
        }
    }

    @Override
    public void tearDown() {
    }

    @Override
    public void setUp() {
        m_Configuration = getConfigAs(FreeAtHomeBinaryConfig.class);

        // Fetch bridge on initialization to get proper state
        FreeAtHomeBridgeHandler bridge = getFreeAtHomeBridge();
        if (bridge != null) {
            m_UpdateChannels.add(new FreeAtHomeUpdateChannel(this,
                    FreeAtHomeBindingConstants.BINARYSWITCH_THING_CHANNEL, new DefaultOnOffTypeConverter(),
                    m_Configuration.deviceId, m_Configuration.channelId, m_Configuration.dataPointIdSwitchUpdate));
        }
    }
}
