/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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

import org.openhab.binding.freeathome.internal.FreeAtHomeBindingConstants;
import org.openhab.binding.freeathome.internal.FreeAtHomeUpdateChannel;
import org.openhab.binding.freeathome.internal.config.FreeAtHomeVirtualConfig;
import org.openhab.binding.freeathome.internal.model.DefaultOnOffTypeConverter;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link FreeAtHomeVirtualSwitchHandler} represents virtual switch
 *
 * @author Stian Kjoglum - Initial contribution
 */

public class FreeAtHomeVirtualSwitchHandler extends FreeAtHomeBaseHandler {

    public FreeAtHomeVirtualSwitchHandler(Thing thing) {
        super(thing);
    }

    private Logger logger = LoggerFactory.getLogger(FreeAtHomeVirtualSwitchHandler.class);

    private FreeAtHomeVirtualConfig mConfiguration = new FreeAtHomeVirtualConfig();

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        FreeAtHomeBridgeHandler bridge = getFreeAtHomeBridge();

        if (bridge == null) {
            logger.error("No bridge connected");
            return;
        }

        /*
         * UpDownCommand
         */
        if (command instanceof OnOffType) {

            if (channelUID.getId().equals(FreeAtHomeBindingConstants.VIRTUALSWITCH_THING_CHANNEL)) {
                OnOffType udCommand = (OnOffType) command;
                String idpChannel = mConfiguration.deviceId + "/" + mConfiguration.channelId + "/"
                        + mConfiguration.dataPointId;
                String odpChannel = mConfiguration.deviceId + "/" + mConfiguration.channelId + "/"
                        + mConfiguration.dataPointIdUpdate;

                if (udCommand.equals(OnOffType.ON)) {

                    logger.debug("Virtual Switch ON {}", idpChannel);
                    bridge.setDataPoint(idpChannel, "1");
                    bridge.setDataPoint(odpChannel, "1");

                }
                if (udCommand.equals(OnOffType.OFF)) {

                    logger.debug("Virtual Switch OFF {}", idpChannel);
                    bridge.setDataPoint(idpChannel, "0");
                    bridge.setDataPoint(odpChannel, "0");

                }
            }
        }
    }

    @Override
    public void tearDown() {
    }

    @Override
    public void setUp() {
        mConfiguration = getConfigAs(FreeAtHomeVirtualConfig.class);

        // Fetch bridge on initialization to get proper state
        FreeAtHomeBridgeHandler bridge = getFreeAtHomeBridge();
        if (bridge != null) {
            mUpdateChannels.add(new FreeAtHomeUpdateChannel(this,
                    FreeAtHomeBindingConstants.VIRTUALSWITCH_THING_CHANNEL, new DefaultOnOffTypeConverter(),
                    mConfiguration.deviceId, mConfiguration.channelId, mConfiguration.dataPointIdUpdate));
        } // dummy call to avoid optimization
    }
}
