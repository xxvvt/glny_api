package org.example.utils;

import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;
import org.example.model.MyDestinationDataProvider;
import org.example.model.SapSystemDestinationDataProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@Component
public class SapUtils {
    private static String jcoAshost;
    private static String jcoSysnr;
    private static String jcoClient;
    private static String jcoUser;
    private static String jcoPasswd;
    private static String jcoLang;
    private static String jcoPoolCapacity;
    private static String jcoPeakLimit;
    private static String jcoABAP;
    private static String file_path;

    @Value("${SAP.sap-connection.jco-ashost}")
    public void setJcoAshost(String jcoAshost) {
        this.jcoAshost = jcoAshost;
    }

    @Value("${SAP.sap-connection.jco-sysnr}")
    public void setJcoSysnr(String jcoSysnr) {
        this.jcoSysnr = jcoSysnr;
    }

    @Value("${SAP.sap-connection.jco-client}")
    public void setJcoClient(String jcoClient) {
        this.jcoClient = jcoClient;
    }

    @Value("${SAP.sap-connection.jco-user}")
    public void setJcoUser(String jcoUser) {
        this.jcoUser = jcoUser;
    }

    @Value("${SAP.sap-connection.jco-passwd}")
    public void setJcoPasswd(String jcoPasswd) {
        this.jcoPasswd = jcoPasswd;
    }

    @Value("${SAP.sap-connection.jco-lang}")
    public void setJcoLang(String jcoLang) {
        this.jcoLang = jcoLang;
    }

    @Value("${SAP.sap-connection.jco-pool-capacity}")
    public void setJcoPoolCapacity(String jcoPoolCapacity) {
        this.jcoPoolCapacity = jcoPoolCapacity;
    }

    @Value("${SAP.sap-connection.jco-peak-limit}")
    public void setJcoPeakLimit(String jcoPeakLimit) {
        this.jcoPeakLimit = jcoPeakLimit;
    }
    @Value("${SAP.sap-connection.jco-ABAP}")
    public void setJcoABAP(String jcoABAP) {
        this.jcoABAP = jcoABAP;
    }
    @Value("${file.path}")
    public void setFilePath(String file_path) {
        this.file_path = file_path;
    }
    private  JCoDestination destination;


    /**
     * 创建SAP接口连接并返回数据
     * @param name   方法名称
     * @param table 表头
     * @param parameters    参数
     * @return 返回数据
     * */
    public  JCoTable createDataFile(String name, String table, Map<String,String> parameters) throws JCoException {
        JCoTable jtab = null;
        try{
            if(this.destination==null) this.getProperties();
            JCoFunction function = destination.getRepository().getFunction(name);
            if (function == null)
                throw new RuntimeException("BAPI not found in SAP.");
            //从parameters中获取参数
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                function.getImportParameterList().setValue(entry.getKey(), entry.getValue());
            }
            function.execute(destination);
            jtab = function.getTableParameterList().getTable(table);
        }catch (Exception e){
            e.printStackTrace();
        }
        return jtab;
    }
    public void  getProperties() throws JCoException {

            Properties connectProperties = new Properties();
            connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, jcoAshost);
            connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, jcoSysnr);
            connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, jcoClient);
            connectProperties.setProperty(DestinationDataProvider.JCO_USER, jcoUser);
            connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, jcoPasswd);
            connectProperties.setProperty(DestinationDataProvider.JCO_LANG, jcoLang);
            connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, jcoPoolCapacity);
            connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, jcoPeakLimit);
        // 创建 SAP JCo Destination
        try {
            // 设置路径（如果需要的话）
//             connectProperties.setProperty(DestinationDataProvider.JCO_PATH, "path/to/sapjco3.dll");

            MyDestinationDataProvider.changeProperties("ABAP_AS_WITH_POOL", connectProperties);
            this.destination = JCoDestinationManager.getDestination("ABAP_AS_WITH_POOL");
            // 在这里可以使用 destination 进行 SAP JCo 操作
            System.out.println("Connected to SAP system!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public void registerDataProvider() {
        MyDestinationDataProvider provider = new MyDestinationDataProvider();
        // 注册 DestinationDataProvider
        Environment.registerDestinationDataProvider(provider);
    }

}
