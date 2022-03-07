import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class E4 extends DefaultHandler {
    private static final String CLASS_NAME = E4.class.getName();
    private final static Logger LOG = Logger.getLogger(CLASS_NAME);

    private SAXParser parser = null;
    private SAXParserFactory spf;

    private double totalSales;
    private boolean inSales;

    private String currentElement;
    private String id;
    private String name;
    private String lastName;
    private String phone;
    private String car;
    private String model;
    private String insu;
    private String date;


    private String keyword;
    //ArrayList<seguromodelo>subtotales= new ArrayList<>();
    private HashMap<String,Double> subtotales;
    private HashMap<String, Double> contProm;


    public E4(){
        super();
        spf = SAXParserFactory.newInstance();
        // verificar espacios de nombre
        spf.setNamespaceAware(true);
        // validar que el documento este bien formado (well formed)
        spf.setValidating(true);

        subtotales = new HashMap<String, Double>();
        contProm = new HashMap<String, Double>();
        //subtotales=new TreeSet<>();

    }
    private void process(File file,String car) {
        try {
            // obtener un parser para verificar el documento
            parser = spf.newSAXParser();

        } catch (SAXException | ParserConfigurationException e) {
            LOG.severe(e.getMessage());
            System.exit(1);
        }
        System.out.println("\nStarting parsing of " + file + "\n");
        try {
            // iniciar analisis del documento
            keyword = car;
            parser.parse(file, this);
        } catch (IOException | SAXException e) {
            LOG.severe(e.getMessage());
        }
    }

    @Override
    public void startDocument() throws SAXException {
        // al inicio del documento inicializar
        // las ventas totales
        totalSales = 0.0;
    }

    @Override
    public void endDocument() throws SAXException {
        // Se proceso todo el documento, imprimir resultado
        Set<Map.Entry<String, Double>> entries = subtotales.entrySet();
        for (Map.Entry<String, Double> entry: entries) {
            System.out.printf("Marca: %-9.9s Fecha: %-9.9s Total de Ventas $%,9.2f\n",
                    keyword,
                    entry.getKey(),
                    entry.getValue());
        }


    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (localName.equals("insurance_record")) {
            inSales = true;
        }
        currentElement = localName;
    }

    @Override
    public void characters(char[] bytes, int start, int length) throws SAXException {

        switch ( currentElement ) {
            case "id":
                this.id = new String(bytes, start, length);
                break;
            case "first_name":
                this.name = new String(bytes, start, length);
                break;
            case "last_name":
                this.lastName = new String(bytes, start, length);
                break;
            case "phone":
                this.phone = new String(bytes, start, length);
                break;
            case "car":
                this.car = new String(bytes, start, length);
                break;
            case "model":
                this.model = new String(bytes, start, length);
                break;
            case "insurance":
                this.insu = new String(bytes, start, length);
                break;
            case "contract_date":
                this.date = new String(bytes, start, length);
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("insurance_record")) {

            double val = 0.0;
            double cont = 0;
            if (car.equals(keyword)) {
                try {
                    val = Double.parseDouble(this.insu);
                } catch (NumberFormatException e) {
                    LOG.severe(e.getMessage());
                }
                if (subtotales.containsKey(this.date.substring(0, 7))) {
                    Double sum = subtotales.get(this.date.substring(0, 7));
                    subtotales.put(this.date.substring(0, 7), sum + val);
                } else {
                    subtotales.put(this.date.substring(0, 7), val);
                }

            }
            inSales = false;
        }
    }

    public static void main(String args[]) {
        if (args.length == 0) {
            LOG.severe("No file to process. Usage is:" + "\njava DeptSalesReport <keyword>");
            return;
        }
        File xmlFile = new File(args[0] );
        E4 handler = new E4();
        handler.process( xmlFile ,"Ford");

    }

}