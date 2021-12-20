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
package org.openhab.binding.freeathome.internal;

import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.freeathome.internal.handler.FreeAtHomeBaseHandler;
import org.openhab.binding.freeathome.internal.handler.FreeAtHomeBridgeHandler;
import org.openhab.binding.freeathome.internal.model.StateConverter;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.types.State;

/**
 * The {@link FreeAtHomeHandlerUpdateHandler} is responsible for managing the registered update events
 *
 *
 * @author Stian Kjoglum - Initial contribution
 */
public class FreeAtHomeUpdateHandler {

    // private Logger logger = LoggerFactory.getLogger(FreeAtHomeUpdateHandler.class);

    public class FreeAtHomeThingChannel {
        public FreeAtHomeBaseHandler m_BaseHandler = null;;
        public ChannelUID m_Channel = null;
        public StateConverter m_StateConverter = null;

        public FreeAtHomeThingChannel(FreeAtHomeBaseHandler h, ChannelUID c, StateConverter s) {
            m_BaseHandler = h;
            m_Channel = c;
            m_StateConverter = s;
        }
    }

    Map<String, FreeAtHomeThingChannel> m_RegisteredThings;

    public FreeAtHomeUpdateHandler() {
        m_RegisteredThings = new HashMap<String, FreeAtHomeThingChannel>();
    }

    private String GenerateId(String serial, String channel, String dataPoint) {
        return serial + "_" + channel + "_" + dataPoint;
    }

    public void Register(FreeAtHomeUpdateChannel channel) {
        this.RegisterThing(channel.m_Thing, channel.m_OhThingChanneId, channel.m_OhThingStateConverter,
                channel.m_FhSerial, channel.m_FhChannel, channel.m_FhDataPoint);
    }

    private void RegisterThing(FreeAtHomeBaseHandler thing, String channelId, StateConverter state, String serial,
            String channel, String dataPoint) {

        ChannelUID cUid = thing.getThing().getChannel(channelId).getUID();
        FreeAtHomeThingChannel c = new FreeAtHomeThingChannel(thing, cUid, state);

        m_RegisteredThings.put(this.GenerateId(serial, channel, dataPoint), c);
    }

    public void Unregister(FreeAtHomeUpdateChannel channel) {
        UnregisterThing(channel.m_FhSerial, channel.m_FhChannel, channel.m_FhDataPoint);
    }

    private void UnregisterThing(String serial, String channel, String dataPoint) {
        m_RegisteredThings.remove(this.GenerateId(serial, channel, dataPoint));
    }

    public void NotifyThing(String serial, String channel, String dataPoint, String value) {
        // Find entry and call notify method at thing
        FreeAtHomeThingChannel thing = m_RegisteredThings.get(this.GenerateId(serial, channel, dataPoint));

        if (thing != null) {
            // Check if Thing is Virtual switch and automatically send back value to odp
            if (thing.m_Channel.getId().equals(FreeAtHomeBindingConstants.VIRTUALSWITCH_THING_CHANNEL)) {
                if (dataPoint.contains("idp")) {
                    FreeAtHomeBridgeHandler bridge = (FreeAtHomeBridgeHandler) thing.m_BaseHandler.callBridge();
                    String odpStatus = serial + "/" + channel + "/odp0000";
                    bridge.setDataPoint(odpStatus, value);
                }
            }
            /*
             * For each channel a specific converter can be registered that
             * generates a State from string value
             */
            StateConverter sConvert = thing.m_StateConverter;
            State sState = sConvert.convert(value);

            if (sState != null) {
                thing.m_BaseHandler.notifyUpdate(thing.m_Channel, sState);
            }
        }
    }
}
