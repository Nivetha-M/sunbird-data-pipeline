package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.DeNormalizationConfig;
import org.ekstep.ep.samza.util.DeviceDataCache;

import java.util.Map;

import static java.text.MessageFormat.format;

public class DeviceDataUpdater extends IEventUpdater {
    private static Logger LOGGER = new Logger(DeviceDataUpdater.class);
    private DeviceDataCache deviceCache;

    DeviceDataUpdater(DeviceDataCache deviceCache) {
        this.deviceCache = deviceCache;
    }

    public Event update(Event event) {
        Map<String, Object> deviceData;
        try {
            String did = event.did();
            if (did != null && !did.isEmpty()) {
                deviceData = deviceCache.getDataForDeviceId(event.did());
                if (deviceData != null && !deviceData.isEmpty()) {
                    event.addDeviceData(deviceData);
                } else {
                    event.setFlag(DeNormalizationConfig.getDeviceLocationJobFlag(), false);
                }
            }
            return event;
        } catch (Exception ex) {
            LOGGER.error(null, format("EXCEPTION WHEN DE-NORMALIZATION OF DEVICE DATA. EVENT: {0}, EXCEPTION:", event), ex);
            return event;
        }
    }
}