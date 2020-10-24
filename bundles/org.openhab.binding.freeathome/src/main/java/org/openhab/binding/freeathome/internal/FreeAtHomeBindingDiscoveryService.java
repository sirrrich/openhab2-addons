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
package org.openhab.binding.freeathome.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.freeathome.internal.handler.FreeAtHomeBridgeHandler;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements manually triggered discovery
 *
 * @author Stian Kjoglum - Initial contribution
 *
 */
@Component(service = DiscoveryService.class)
public class FreeAtHomeBindingDiscoveryService extends AbstractDiscoveryService {

    private final Logger logger = LoggerFactory.getLogger(FreeAtHomeBindingDiscoveryService.class);

    public FreeAtHomeBindingDiscoveryService(int timeout) throws IllegalArgumentException {
        super(FreeAtHomeBindingConstants.SUPPORTED_THING_TYPES_UIDS, 90, false);
        // TODO Auto-generated constructor stub
    }

    public FreeAtHomeBindingDiscoveryService() {
        super(FreeAtHomeBindingConstants.SUPPORTED_THING_TYPES_UIDS, 90, false);
    }

    @Override
    public void startScan() {
        // TODO Auto-generated method stub
        FreeAtHomeBridgeHandler fh = FreeAtHomeBridgeHandler.g_freeAtHomeBridgeHandler;

        this.removeOlderResults(getTimestampOfLastScan());

        if (fh != null) {
            logger.debug("start scan");
            ThingUID bridgeUID = fh.getThing().getUID();

            String xmlTree = fh.getAll();
            FreeAtHomeBindingInventoryIterator iter = new FreeAtHomeBindingInventoryIterator(xmlTree);

            while (iter.iter_hasnext()) {
                FreeAtHomeBindingInventoryIterator.DeviceDescription device = iter.iter_get();
                logger.debug("Device {}", device);

                String deviceTypeId = device.DeviceTypeId;

                switch (deviceTypeId) {
                    // Jalousieaktor 4-fach, REG
                    case "B001": {
                        for (int i = 0; i < 4; i++) // B001 provides 4 different channels
                        {
                            String channelId = "ch000" + i;
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.RAFFSTORE_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("DeviceId", device.Serial);
                            properties.put("ChannelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + deviceTypeId + "_" + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        }
                        break;
                    }
                    // Jalousieaktor 1-fach, REG
                    // 1013 provides 1 channel, but it runs on channel ch0003
                    case "9013":
                    case "9014":
                    case "1013": {
                        String channelId = "ch0003";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.RAFFSTORE_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("DeviceId", device.Serial);
                        properties.put("ChannelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // Sensor/Schaltaktor 2/1-fach
                    // 9015 provides 1 channel, but it runs on channel ch0006
                    case "1002":
                    case "100E":
                    case "1015":
                    case "9015":
                    case "9003": {
                        String channelId = "ch0006";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.RAFFSTORE_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("DeviceId", device.Serial);
                        properties.put("ChannelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // Hue Schaltaktor
                    case "10C4": {
                        String channelId = "ch0000";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // Schaltaktor 4-fach, 16A, REG
                    case "B002": {
                        for (int i = 0; i < 4; i++) // B002 provides 4 different channels
                        {
                            String channelId = "ch000" + i;
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("deviceId", device.Serial);
                            properties.put("channelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + deviceTypeId + "_" + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        }
                        break;
                    }

                    // Schaltaktor 8 fach
                    case "B008": {
                        for (int i = 0; i < 8; i++) // B008 provides 8 binary channels
                        {
                            String channelId = "ch000" + i;
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.BINARYSWITCH_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("DeviceId", device.Serial);
                            properties.put("ChannelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + deviceTypeId + "_" + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        }

                        for (int i = 12; i < 12 + 8; i++) // B008 provides 8 actuator channels
                        {
                            String n = Integer.toHexString(i).toUpperCase();

                            String result = ("0000" + n).substring(n.length());
                            String channelId = "ch" + result;
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("deviceId", device.Serial);
                            properties.put("channelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + deviceTypeId + "_" + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        }
                        break;
                    }

                    // Schaltaktor 1/1
                    case "100C":
                    case "900C": {
                        String channelId = "ch0003";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // Schaltaktor 2/2
                    case "1010":
                    case "9010":
                    case "9011": {
                        for (int i = 6; i < 8; i++) {
                            String channelId = "ch000" + i;
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("deviceId", device.Serial);
                            properties.put("channelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + deviceTypeId + "_" + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        }
                        break;
                    }

                    // Switch sensor 1/1
                    case "1000":
                    case "9000":
                    case "9001": {
                        String channelId = "ch0000";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // Schaltactor 1/1
                    case "900B":
                    case "900D": {
                        String channelId = "ch0001";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // Switch group
                    case "4000": {
                        String channelId = "ch0000";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID, device.Serial);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);
                        properties.put("dataPointId", "odp0002");

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // Motion Detector (with actuator)
                    case "900A": {
                        String channelId1 = "ch0000";
                        ThingUID uid1 = new ThingUID(FreeAtHomeBindingConstants.MOTIONSWITCH_THING_TYPEUID,
                                device.Serial + "_" + channelId1);
                        Map<String, Object> properties1 = new HashMap<>(1);
                        properties1.put("deviceId", device.Serial);
                        properties1.put("channelId", channelId1);

                        DiscoveryResult discoveryResult1 = DiscoveryResultBuilder.create(uid1)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId1)
                                .withBridge(bridgeUID).withProperties(properties1).build();
                        thingDiscovered(discoveryResult1);

                        String channelId2 = "ch0001";
                        ThingUID uid2 = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                device.Serial + "_" + channelId2);
                        Map<String, Object> properties2 = new HashMap<>(1);
                        properties2.put("deviceId", device.Serial);
                        properties2.put("channelId", channelId2);

                        DiscoveryResult discoveryResult2 = DiscoveryResultBuilder.create(uid2)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId2)
                                .withBridge(bridgeUID).withProperties(properties2).build();
                        thingDiscovered(discoveryResult2);
                        break;
                    }

                    // Motion Detector (without actuator)
                    case "9008":
                    case "1008":
                    case "9009": {
                        String channelId = "ch0000";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.MOTIONSWITCH_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // Virtual Switch
                    case "0001": {
                        String channelId = "ch0000";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.VIRTUALSWITCH_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // Jalousiegruppe
                    case "4001": {
                        String channelId = "ch0000";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.RAFFSTORE_THING_TYPEUID, device.Serial);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("DeviceId", device.Serial);
                        properties.put("ChannelId", channelId);
                        properties.put("InputIdComplete", "odp0003");
                        properties.put("InputIdStepwise", "odp0004");

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder
                                .create(uid).withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                        + deviceTypeId + "_" + device.Serial)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // thermostat
                    case "1004":
                    case "9004":
                    case "9005":
                    case "2041": {
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.THERMOSTAT_THING_TYPEUID, device.Serial);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder
                                .create(uid).withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                        + deviceTypeId + "_" + device.Serial)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // touch
                    case "1020": {
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.TOUCH_THING_TYPEUID, device.Serial);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder
                                .create(uid).withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                        + deviceTypeId + "_" + device.Serial)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // 7" Panel with door opener functionality
                    case "1038": {
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.DOORPANEL_THING_TYPEUID, device.Serial);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder
                                .create(uid).withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                        + deviceTypeId + "_" + device.Serial)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // neue Szene
                    case "4800": {
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SCENE_THING_TYPEUID, device.Serial);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("SceneId", device.Serial);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder
                                .create(uid).withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                        + deviceTypeId + "_" + device.Serial)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // Dimmaktor 4-fach
                    case "101C":
                    case "1021":
                    case "901C": {
                        for (int i = 0; i < 4; i++) {
                            String channelId = "ch000" + i;
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.DIMMER_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("deviceId", device.Serial);
                            properties.put("channelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + deviceTypeId + "_" + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        }
                        break;
                    }

                    // Dimmaktor 1/1-fach
                    case "9017": {
                        String channelId = "ch0003";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.DIMMER_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // Dimmaktor 2/1-fach
                    case "9019": {
                        String channelId = "ch0006";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.DIMMER_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // Hue dimmer
                    case "10C0": {
                        String channelId = "ch0000";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.DIMMER_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // Window sensor
                    case "2042": {
                        String channelId = "ch0001";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.WINDOWSENSOR_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // Binary sensor
                    case "B007":
                    case "B005": {
                        String channelId = "ch0000";
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.BINARYSWITCH_THING_TYPEUID,
                                device.Serial + "_" + channelId);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);
                        properties.put("channelId", channelId);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_" + deviceTypeId
                                        + "_" + device.Serial + "_" + channelId)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // weather station
                    case "101D": {
                        ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.WEATHER_THING_TYPEUID, device.Serial);
                        Map<String, Object> properties = new HashMap<>(1);
                        properties.put("deviceId", device.Serial);

                        DiscoveryResult discoveryResult = DiscoveryResultBuilder
                                .create(uid).withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                        + deviceTypeId + "_" + device.Serial)
                                .withBridge(bridgeUID).withProperties(properties).build();
                        thingDiscovered(discoveryResult);
                        break;
                    }

                    // Wireless devices
                    case "A039": {
                        if (device.DeviceTypeName.contains("Schaltaktor 1/1-fach")) {
                            String channelId = "ch0006";
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("deviceId", device.Serial);
                            properties.put("channelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + deviceTypeId + "_" + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        } else if (device.DeviceTypeName.contains("Schaltaktor 2/2-fach")) {
                            for (int i = 6; i < 8; i++) {
                                String channelId = "ch000" + i;
                                ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.SWITCH_THING_TYPEUID,
                                        device.Serial + "_" + channelId);
                                Map<String, Object> properties = new HashMap<>(1);
                                properties.put("deviceId", device.Serial);
                                properties.put("channelId", channelId);

                                DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                        .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                                + deviceTypeId + "_" + device.Serial + "_" + channelId)
                                        .withBridge(bridgeUID).withProperties(properties).build();
                                thingDiscovered(discoveryResult);
                            }
                        } else if (device.DeviceTypeName.contains("Dimmaktor 1/1-fach")) {
                            String channelId = "ch0008";
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.DIMMER_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("deviceId", device.Serial);
                            properties.put("channelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + deviceTypeId + "_" + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        } else if (device.DeviceTypeName.contains("Jalousieaktor 2/1-fach")) {
                            String channelId = "ch0009";
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.RAFFSTORE_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("DeviceId", device.Serial);
                            properties.put("ChannelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + deviceTypeId + "_" + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        } else if (device.DeviceTypeName.contains("Jalousieaktor 1/1-fach")) {
                            String channelId = "ch0009";
                            ThingUID uid = new ThingUID(FreeAtHomeBindingConstants.RAFFSTORE_THING_TYPEUID,
                                    device.Serial + "_" + channelId);
                            Map<String, Object> properties = new HashMap<>(1);
                            properties.put("DeviceId", device.Serial);
                            properties.put("ChannelId", channelId);

                            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(uid)
                                    .withLabel(device.DeviceDisplayName + "_" + device.DeviceTypeName + "_"
                                            + deviceTypeId + "_" + device.Serial + "_" + channelId)
                                    .withBridge(bridgeUID).withProperties(properties).build();
                            thingDiscovered(discoveryResult);
                        }
                        break;
                    }

                    default: {
                        if (fh.dummyThingsEnabled()) {

                            ThingUID uid1 = new ThingUID(FreeAtHomeBindingConstants.DUMMY_THING_TYPEUID, device.Serial);

                            DiscoveryResult discoveryResult1 = DiscoveryResultBuilder
                                    .create(uid1).withLabel("Dummy_" + device.DeviceDisplayName + "_"
                                            + device.DeviceTypeName + "_" + deviceTypeId + "_" + device.Serial)
                                    .withBridge(bridgeUID).build();
                            thingDiscovered(discoveryResult1);
                        }
                    }
                }

                iter.iter_next();
            }

        }
    }

    @Override
    protected void startBackgroundDiscovery() {
        logger.debug("Start freeathome background discovery");
        startScan();
    }

    @Override
    protected void stopBackgroundDiscovery() {
        logger.debug("Stop freeathome background discovery");
    }

    @Override
    public boolean isBackgroundDiscoveryEnabled() {
        return false;
    }
}
