package ibm.mq;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MQConfig {

    private static Properties props = null;
    private static MQConfig instance;

    private InputStream mqConfigFile;

    private MQConfig(){}

    public String getProperty(String sKey){
        return props.getProperty(sKey);
    }

    public void setMqConfigFile(String filePath) throws  IOException {
        File f = new File(filePath);
        //props = new Properties(new FileInputStream(f));
        try {
            props.load(mqConfigFile);
        }catch (Exception e) {

        }finally{
            if(null != mqConfigFile){
                mqConfigFile.close();
            }
        }
    }

}
