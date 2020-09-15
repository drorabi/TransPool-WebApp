package engine.data;

import engine.converted.classes.Transpool;
import engine.exceptions.*;
import engine.schema.generated.TransPool;
import engine.ui.Engine;
import javafx.concurrent.Task;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class DataLoader {
    private TransPool data;
    private final static String JAXB_XML_PACKAGE_NAME = "engine.schema.generated";
    private String massage="";
    private boolean fileUploadSuccessfully=true;

    public TransPool getData() {
        return data;
    }

    public void loadXML(InputStream in) throws JAXBException {
        data = deserializeFrom(in);
    }

    private static TransPool deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (TransPool) u.unmarshal(in);
    }

    public Transpool load(InputStream in) throws InvalidMap, InvalidPathDepatureDestination, InvalidRoute,
            InvalidStationsNames, InvalidStationsLocation,
            InvalidStationsCoordinates, InvalidPathNames, JAXBException,
            InvalidRideStartDay, InvalidRideStartHour, InvalidRouteThroughTheStationTwice, InvalidRideStartMinutes, NameExsitInSystem {
        loadXML(in);
        return convert();
    }


    private Transpool convert() throws InvalidPathNames, InvalidMap, InvalidPathDepatureDestination, InvalidStationsNames,
            InvalidStationsCoordinates, InvalidRoute, InvalidStationsLocation, InvalidRideStartDay, InvalidRideStartHour, InvalidRouteThroughTheStationTwice, InvalidRideStartMinutes, NameExsitInSystem {
        Transpool transpool = new Transpool(data);
        return transpool;
    }

    String getFileExtension(File file) {
        if (file == null) {
            return "";
        }
        String name = file.getName();
        int i = name.lastIndexOf('.');
        String ext = i > 0 ? name.substring(i + 1) : "";
        return ext;
    }

    public Transpool call(InputStream in)  {
       try {
           return load(in);

       }catch (InvalidMap | InvalidPathDepatureDestination | NameExsitInSystem | InvalidRoute | InvalidStationsNames | InvalidStationsLocation | InvalidStationsCoordinates | InvalidPathNames | JAXBException | InvalidRideStartDay | InvalidRideStartHour | InvalidRouteThroughTheStationTwice | InvalidRideStartMinutes e){
           setMassage(e.getMessage());
           fileUploadSuccessfully=false;
       }
       return null;
    }

    private void setMassage(String e){
        massage=e;
    }

    public String getMassage() {
        return massage;
    }

    public boolean IsFileUploadSuccessfully(){
        return fileUploadSuccessfully;
    }
}
