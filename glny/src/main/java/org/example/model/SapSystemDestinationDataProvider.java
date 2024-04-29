package org.example.model;

import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

import java.util.HashMap;
import java.util.Properties;

public class SapSystemDestinationDataProvider implements DestinationDataProvider {

    private DestinationDataEventListener el;
    private final HashMap<String, Properties> connectionProperties = new HashMap<>();


    @Override
    public Properties getDestinationProperties(String destinationName) {
        if (connectionProperties.size() > 0) {
            Properties con = connectionProperties.get(destinationName.toLowerCase().trim());
            if (con != null) {
                return con;
            }
        }
        return null;
    }


    @Override
    public boolean supportsEvents() {
        return true;
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener dl) {
        this.el = dl;
    }

    public void addConnectionProperties(String destName, Properties properties) {
        connectionProperties.put(destName.toLowerCase().trim(), properties);
    }
}