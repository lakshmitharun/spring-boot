package com.example.filedemo;
import java.io.File;
import java.io.IOException;

import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.SystemUtils;
import org.docx4j.UnitsOfMeasurement;
import org.docx4j.XmlUtils;
import org.docx4j.dml.diagram.CTDataModel;
import org.docx4j.dml.diagram.CTDiagramDefinition;
import org.docx4j.model.structure.MarginsWellKnown;
import org.docx4j.model.structure.PageDimensions;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.OpcPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.DrawingML.*;
import org.docx4j.wml.P;
import org.docx4j.wml.SectPr.PgSz;
import org.glox4j.openpackaging.packages.GloxPackage;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class CreateDocxWithSmartArt  extends CreateWithSmartArtAbstract {

    public static int i1=1;
    public CreateDocxWithSmartArt(CTDiagramDefinition diagramLayoutObj,
                                  Templates layoutTreeCreatorXslt,
                                  Templates layoutTree2DiagramDataXslt) {

        super( diagramLayoutObj,
                layoutTreeCreatorXslt,
                layoutTree2DiagramDataXslt);
    }
    public WordprocessingMLPackage createSmartArtDocx(
            PageSizePaper sz, boolean landscape,
            MarginsWellKnown margins,
            Document xml, WordprocessingMLPackage mla) throws Exception {

        // Make a basic docx
        WordprocessingMLPackage wordMLPackage = null;
        if(mla==null){
             wordMLPackage = WordprocessingMLPackage.createPackage(sz, landscape);
        }else{
            wordMLPackage = mla;
        }


        // Layout part
        DiagramLayoutPart layout = new DiagramLayoutPart();
        layout.setJaxbElement(diagramLayoutObj);

        DiagramColorsPart colors = new DiagramColorsPart();
        colors.unmarshal("colorsDef-accent1_2.xml");
        //colors.CreateMinimalContent("mycolors");

        DiagramStylePart style = new DiagramStylePart();
        style.unmarshal("quickStyle-simple1.xml");
        //style.CreateMinimalContent("mystyle");

        // DiagramDataPart
        DiagramDataPart data = new DiagramDataPart();
        data.setPackage(wordMLPackage); // otherwise we need to pass pkg around
        data.setJaxbElement( createDiagramData(data, xml) );
        CTDataModel jaxbElement = data.getJaxbElement();


        String layoutRelId = String.valueOf(System.currentTimeMillis());
                wordMLPackage.getMainDocumentPart().addTargetPart(layout).setId(layoutRelId);
        String dataRelId = String.valueOf(System.currentTimeMillis());
                wordMLPackage.getMainDocumentPart().addTargetPart(data).setId(dataRelId);
        String colorsRelId = String.valueOf(System.currentTimeMillis());
                wordMLPackage.getMainDocumentPart().addTargetPart(colors).setId(colorsRelId);
        String styleRelId = String.valueOf(System.currentTimeMillis());
                wordMLPackage.getMainDocumentPart().addTargetPart(style).setId(styleRelId);

        System.out.println("ids tharun");
        System.out.println(layoutRelId+" "+dataRelId+" "+colorsRelId+" "+styleRelId);

        // Occupy entire page, less margins
        PageDimensions pd = new PageDimensions();
        pd.setPgSize(sz, landscape );
        PgSz pgSz = pd.getPgSz();
        pd.setMargins(margins);
        String cx =  ""+UnitsOfMeasurement.twipToEMU(pgSz.getW().intValue()
                - (pd.getPgMar().getLeft().intValue()+pd.getPgMar().getRight().intValue() ) );  //"5486400";
        String cy = ""+UnitsOfMeasurement.twipToEMU(pgSz.getH().intValue()
                - (pd.getPgMar().getTop().intValue()+pd.getPgMar().getBottom().intValue() )-10000);   //"3200400";
        // Now use it in the docx
        System.out.println("pgSz.getH().intValue() "+pgSz.getH().intValue());
        System.out.println("pd.getPgMar().getTop().intValue() "+pd.getPgMar().getTop().intValue());
        System.out.println("pd.getPgMar().getBottom().intValue()  "+pd.getPgMar().getBottom().intValue());

        System.out.print("smart value");
        System.out.print(createSmartArt( layoutRelId,  dataRelId, colorsRelId,  styleRelId, cx, cy));
        System.out.print(XmlUtils.marshaltoString(createSmartArt( layoutRelId,  dataRelId, colorsRelId,  styleRelId, cx, cy)));
        wordMLPackage.getMainDocumentPart().addObject(
                createSmartArt( layoutRelId,  dataRelId, colorsRelId,  styleRelId, cx, cy));


        return wordMLPackage;
    }

    public static P createSmartArt(String layoutRelId, String dataRelId,
                                   String colorsRelId, String styleRelId, String cx, String cy) throws Exception {

        String ml = "<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
                + "<w:r>"
                + "<w:rPr>"
                + "<w:noProof/>"
                + "<w:lang w:eastAsia=\"en-AU\"/>"
                + "</w:rPr>"
                + "<w:drawing>"
                + "<wp:inline distT=\"0\" distB=\"0\" distL=\"0\" distR=\"0\" xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\" >"
                + "<wp:extent cx=\"${cx}\" cy=\"${cy}\"/>"
                + "<wp:effectExtent l=\"0\" t=\"0\" r=\"0\" b=\"0\"/>"
                + "<wp:docPr id=\""+i1+"\" name=\"Diagram 1\"/>"
                + "<wp:cNvGraphicFramePr/>"
                + "<a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
                + "<a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/diagram\">"
                + "<dgm:relIds r:dm=\"${dataRelId}\" r:lo=\"${layoutRelId}\" r:qs=\"${styleRelId}\" r:cs=\"${colorsRelId}\" xmlns:dgm=\"http://schemas.openxmlformats.org/drawingml/2006/diagram\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"/>"
                + "</a:graphicData>"
                + "</a:graphic>"
                + "</wp:inline>"
                + "</w:drawing>"
                + "</w:r>"
                + "</w:p>";
        i1++;
        java.util.HashMap<String, String>mappings = new java.util.HashMap<String, String>();

        mappings.put("layoutRelId", layoutRelId);
        mappings.put("dataRelId", dataRelId);
        mappings.put("colorsRelId", colorsRelId);
        mappings.put("styleRelId", styleRelId);
        mappings.put("cx", cx);
        mappings.put("cy", cy);

        return (P)org.docx4j.XmlUtils.unmarshallFromTemplate(ml, mappings ) ;
    }


}
