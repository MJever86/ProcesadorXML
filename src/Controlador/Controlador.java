/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.DomUtil;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.WhitespaceStrippingPolicy;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Pedro
 */
public class Controlador {
    private File file;
    private File fileDTD;
    private File fileXSL; 
    private File fileHTML; 
    
    
    public Controlador() {
        this.fileHTML=null;
        this.file=null;
        this.fileDTD=null;
        this.fileXSL=null;
    }

    public Controlador(File file) {
        this.file = file;
        this.fileHTML=null;
        this.fileDTD=null;
        this.fileXSL=null;
    }

    public File getFileDTD() {
        return fileDTD;
    }

    public File getFileHTML() {
        return fileHTML;
    }

    public File getFileXSL() {
        return fileXSL;
    }

    public void setFileDTD(File fileDTD) {
        this.fileDTD = fileDTD;
    }

    public void setFileHTML(File FileHTML) {
        this.fileHTML = FileHTML;
    }

    public void setFileXSL(File fileXSL) {
        this.fileXSL = fileXSL;
    }
    

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String xPathEvaluate(String stringXpath) {
         String resultado = "";

        try {

            Processor proc = new Processor(false);
            DocumentBuilder builder = proc.newDocumentBuilder();
            builder.setLineNumbering(true);
            builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);

            XdmNode documentoXML = builder.build(file);
            XPathCompiler xpath = proc.newXPathCompiler();
            XPathSelector selector = xpath.compile(stringXpath).load();

            selector.setContextItem(documentoXML);
            XdmValue evaluate = selector.evaluate();
            for (XdmItem item : evaluate) {
                resultado += item.getStringValue() + "\n";
            }

        } catch (SaxonApiException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultado;
    }

    public String validar() {
        String resultado = "procesando fichero " + this.file.getPath().toString() + "\n";
        //String resultado="";
        try {
            DomUtil.parse(this.file, true);
            resultado += "\n fichero procesado";
            return resultado;
        } catch (ParserConfigurationException | IOException | SAXException ex) {

            resultado += ex.getLocalizedMessage() + "\n";
        }
        return resultado;
    }

    public String validateXSD() {
        String resultado = "Validacion xsd satisfactoria";
        try {
            Document doc = DomUtil.parseXSD(this.file/*, null*/);

        } catch (ParserConfigurationException | IOException | SAXException ex) {
            resultado = ex.getLocalizedMessage();
        }
        return resultado;
    }

    public  String xslTransform() {
        String resultado = "Transformacion correcta";
        File xmlFile=this.file; File xslFile=this.fileXSL; File htmlOut=this.fileHTML;
        if (xmlFile != null && xslFile != null && htmlOut != null) {
            try {

                Processor proc = new Processor(false);
                XsltCompiler comp = proc.newXsltCompiler();
                XsltExecutable exp = comp.compile(new StreamSource(xslFile));
                XdmNode source = proc.newDocumentBuilder().build(new StreamSource(xmlFile));
                Serializer out = proc.newSerializer(htmlOut);
                out.setOutputProperty(Serializer.Property.METHOD, "html");
                out.setOutputProperty(Serializer.Property.INDENT, "yes");
                XsltTransformer trans = exp.load();
                trans.setInitialContextNode(source);
                trans.setDestination(out);
                trans.transform();
                
            } catch (SaxonApiException ex) {
                //Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                resultado = ex.getLocalizedMessage();
            }
        } else {
            resultado = "Archivo no valido";
        }
        return resultado;
    }

}
