package org.example.model;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;

import java.util.Properties;

public class MyDestinationDataProvider implements DestinationDataProvider {

    private static Properties properties;
    // 是否已经注册的标志
    private static boolean isRegistered = false;
    @Override
    public Properties getDestinationProperties(String destinationName) {
//        if (destinationName.equals("ABAP_AS_WITH_POOL") && properties != null) {
            return properties;
//        }
//        return null;  // Return null if the destinationName is not recognized
    }

    @Override
    public boolean supportsEvents() {
        return false;  // We don't support events in this example
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener eventListener) {
        // We don't support events in this example, so no action needed here
    }

    // Custom method to change the properties dynamically
    public static void changeProperties(String destinationName, Properties newProperties) {
//        if (destinationName.equals("ABAP_AS_WITH_POOL")) {
            properties = newProperties;
//        }
    }

    // 注册 DestinationDataProvider
    public static void registerDataProvider() {
        if (!isRegistered) {
            Environment.registerDestinationDataProvider(new MyDestinationDataProvider());
            isRegistered = true;
        } else {
            // 如果已经注册过，则输出日志或采取其他措施
            System.out.println("DestinationDataProvider is already registered");
        }
    }
}
