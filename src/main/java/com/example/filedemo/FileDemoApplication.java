package com.example.filedemo;

import com.example.filedemo.property.FileStorageProperties;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.docx4j.XmlUtils;
import org.docx4j.dml.diagram.*;
import org.docx4j.model.structure.MarginsWellKnown;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.contenttype.ContentTypes;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.OpcPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.DrawingML.DiagramLayoutHeaderPart;
import org.docx4j.openpackaging.parts.DrawingML.DiagramLayoutPart;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.glox4j.openpackaging.packages.GloxPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.w3c.dom.Document;

import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Map;


@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
@ServletComponentScan
public class FileDemoApplication {

    @Override
    public String toString() {
        return "FileDemoApplication{}";
    }

    private static org.docx4j.wml.P addInlineImageToParagraph(org.docx4j.dml.wordprocessingDrawing.Inline inline) {
        // Now add the in-line image to a paragraph
        org.docx4j.wml.ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
        org.docx4j.wml.P paragraph = factory.createP();
        org.docx4j.wml.R run = factory.createR();
        paragraph.getContent().add(run);
        org.docx4j.wml.Drawing drawing = factory.createDrawing();
        run.getContent().add(drawing);
        drawing.getAnchorOrInline().add(inline);
        return paragraph;
    }

    public static void main(String[] args) throws Exception {

		SpringApplication.run(FileDemoApplication.class, args);
		/*BoxAPIConnection api = new BoxAPIConnection("jZ0r9NG8A7qFd4ZRBGXhX6NOWdDuUtO1");
		BoxFolder rootFolder = BoxFolder.getRootFolder(api);
		for (BoxItem.Info itemInfo : rootFolder) {
			System.out.format("[%s] %s\n", itemInfo.getID(), itemInfo.getName());

		}*/
		/*BoxAPIConnection api1 = new BoxAPIConnection("jZ0r9NG8A7qFd4ZRBGXhX6NOWdDuUtO1");
		BoxFile file = new BoxFile(api1, "54966851681");
		URL embedLink = file.getPreviewLink();
		System.out.print(embedLink.toString());*/

		/*FileInputStream stream = new FileInputStream("/path/to/My File.txt");
		BoxFile.Info newFileInfo = rootFolder.uploadFile(stream, "My File.txt");
		stream.close();*/

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream("C:\\Users\\ponnl\\Desktop\\svgstyle.svg");
            fileOutputStream = new FileOutputStream("C:\\Users\\ponnl\\Desktop\\test\\Freesample.jpg");
            TranscoderInput input = new TranscoderInput(fileInputStream);
          //  TranscoderInput input1 = new TranscoderInput(new FileInputStream("C:\\Users\\ponnl\\Desktop\\svgstyle.css"));
            TranscoderOutput output = new TranscoderOutput(fileOutputStream);
            JPEGTranscoder t = new JPEGTranscoder();

            // Set the transcoding hints.
            t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(.8));
          //t.addTranscodingHint(JPEGTranscoder.KEY_USER_STYLESHEET_URI,input1);
           // t.transcode(input, output);
            WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage.createPackage();
            File file = new File("C:\\Users\\ponnl\\Desktop\\test\\Freesample.jpg");
            InputStream inputStream = new java.io.FileInputStream(file);
            long fileLength = file.length();

            byte[] bytes = new byte[(int) fileLength];

            int offset = 0;
            int numRead = 0;

            while (offset < bytes.length
                    && (numRead = inputStream.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            inputStream.close();

            String filenameHint = null;
            String altText = null;

            int id1 = 0;
            int id2 = 1;

            /*org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage imagePart = org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage
                    .createImagePart(wordprocessingMLPackage, bytes);
            int docPrId = 1;
            int cNvPrId = 2;
            org.docx4j.dml.wordprocessingDrawing.Inline inline = imagePart.createImageInline("Filename hint",
                    "Alternative text", docPrId, cNvPrId, false);

            org.docx4j.wml.P paragraph = addInlineImageToParagraph(inline);

            wordprocessingMLPackage.getMainDocumentPart().addObject(paragraph);
            wordprocessingMLPackage.save(new File("C:\\Users\\ponnl\\Desktop\\test\\Example.docx"));
*/

            // do something with the inputstream
        } catch (IOException e) {
            // handle an exception
        } finally { //  finally blocks are guaranteed to be executed
            // close() can throw an IOException too, so we got to wrap that too
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                // handle an exception, or often we just ignore it
            }
        }


        String inputfilepath = "C:\\Users\\ponnl\\Desktop\\test\\Example.docx";
        String outputdir = inputfilepath.substring(0, inputfilepath.lastIndexOf("\\") + 1);
        String outputfilename = inputfilepath.substring(inputfilepath.lastIndexOf("\\")) + ".glox";
        OpcPackage opcPackage = OpcPackage.load(new java.io.File(inputfilepath));
        GloxPackage gloxPackage = new GloxPackage();

        // DiagramLayoutPart - from the exemplar docx or pptx
        // .. just the first one we encounter
        DiagramLayoutPart source = null;
        for (Map.Entry<PartName, Part> entry : opcPackage.getParts().getParts().entrySet()) {

            if (entry.getValue().getContentType().equals(
                    ContentTypes.DRAWINGML_DIAGRAM_LAYOUT)) {
                source = (DiagramLayoutPart) entry.getValue();
                break;
            }
        }
        if (source == null) {
            System.out.println("No SmartArt found in " + inputfilepath);
            return;
        }
        DiagramLayoutPart target = new DiagramLayoutPart(new PartName("/diagrams/layout1.xml"));
        target.setJaxbElement(
                XmlUtils.deepCopy(source.getJaxbElement()));
        gloxPackage.addTargetPart(target);
        DiagramLayoutHeaderPart diagramLayoutHeaderPart = new DiagramLayoutHeaderPart();

        ObjectFactory factory = new ObjectFactory();
        CTDiagramDefinitionHeader header = factory.createCTDiagramDefinitionHeader();
        diagramLayoutHeaderPart.setJaxbElement(header);

        String uniqueId = target.getJaxbElement().getUniqueId();
        if (uniqueId != null) {
            header.setUniqueId(uniqueId);
            System.out.println("Creating glox for " + uniqueId);

            // Can we make a filename out of this?
            if (uniqueId.indexOf("/") > 0
                    && uniqueId.lastIndexOf("/") != uniqueId.length() - 1) {
                outputfilename = uniqueId.substring(uniqueId.lastIndexOf("/") + 1) + ".glox";
            }
        }
        if (target.getJaxbElement().getTitle() == null
                || target.getJaxbElement().getTitle().isEmpty()
                || (target.getJaxbElement().getTitle().size() == 1
                && target.getJaxbElement().getTitle().get(0).getVal().isEmpty())) {
            CTName title = factory.createCTName();
            title.setVal("some title");
            header.getTitle().add(title);
        } else {
            header.getTitle().addAll(target.getJaxbElement().getTitle());
        }

        if (target.getJaxbElement().getDesc() == null
                || target.getJaxbElement().getDesc().isEmpty()
                || (target.getJaxbElement().getDesc().size() == 1
                && target.getJaxbElement().getDesc().get(0).getVal().isEmpty())) {
            CTDescription desc = factory.createCTDescription();
            desc.setVal("some desc");
            header.getDesc().add(desc);
        } else {
            header.getDesc().addAll(target.getJaxbElement().getDesc());
        }
        gloxPackage.addTargetPart(diagramLayoutHeaderPart);


        // All done..
        String outfile = outputdir + outputfilename;
        System.out.println("Writing " + outfile);
        gloxPackage.save(new java.io.File(outfile));

        System.out.println("Done!");

        System.out.print(DiagramDataUnflatten1.mianer());


        Document doc = XmlUtils.getNewDocumentBuilder().parse(
                new File("C:\\Users\\ponnl\\Desktop\\test\\data-sample.xml"));


        //GloxPackage gloxPackage1 = GloxPackage.load( new File(outfile) );
        InputStream is = new FileInputStream(outfile);
        InputStream is1 = new FileInputStream(outfile);

        GloxPackage gloxPackage1 = (GloxPackage) OpcPackage.load(is);


        CTDiagramDefinition diagramLayoutObj = gloxPackage1.getDiagramLayoutPart().getJaxbElement();


        Templates layoutTreeCreatorXslt =
                DiagramLayoutPart.generateLayoutTreeXSLT(
                        diagramLayoutObj);


        Templates layoutTree2DiagramDataXslt = XmlUtils.getTransformerTemplate(
                new StreamSource(
                        org.docx4j.utils.ResourceUtils.getResource(
                                "org/docx4j/openpackaging/parts/DrawingML/DiagramLayoutTree4AlgHier.xslt")));

        CreateDocxWithSmartArt creatorDocx = new CreateDocxWithSmartArt(diagramLayoutObj, layoutTreeCreatorXslt, layoutTree2DiagramDataXslt);
        WordprocessingMLPackage pkg = creatorDocx.createSmartArtDocx(PageSizePaper.A4, false, MarginsWellKnown.NORMAL, doc, null);
        creatorDocx.createSmartArtDocx(PageSizePaper.A4, false, MarginsWellKnown.NORMAL, doc, pkg);

        GloxPackage gloxPackage2 = (GloxPackage) OpcPackage.load(is1);
        CTDiagramDefinition diagramLayoutObj1 = gloxPackage2.getDiagramLayoutPart().getJaxbElement();
        Templates layoutTreeCreatorXslt1 =
                DiagramLayoutPart.generateLayoutTreeXSLT(
                        diagramLayoutObj1);
        Templates layoutTree2DiagramDataXslt1 = XmlUtils.getTransformerTemplate(
                new StreamSource(
                        org.docx4j.utils.ResourceUtils.getResource(
                                "org/docx4j/openpackaging/parts/DrawingML/DiagramLayoutTree4AlgHier.xslt")));


        CreateDocxWithSmartArt creatorDocx1 = new CreateDocxWithSmartArt(diagramLayoutObj1, layoutTreeCreatorXslt1, layoutTree2DiagramDataXslt1);
        Document doc1 = XmlUtils.getNewDocumentBuilder().parse(
                new File("C:\\Users\\ponnl\\Desktop\\test\\data-sample.xml"));
        pkg = creatorDocx.createSmartArtDocx(PageSizePaper.A4, false, MarginsWellKnown.NORMAL, doc1, pkg);
        SaveToZipFile saver = new SaveToZipFile(pkg);
        saver.save(new File("C:\\Users\\ponnl\\Desktop\\test\\OUT1.docx"));
        System.out.println("Done!");


/*		org.docx4j.wml.Document wmlDocumentEl = (org.docx4j.wml.Document) documentPart.getJaxbElement();
		Body body = wmlDocumentEl.getBody();
		//System.out.print(XmlUtils.marshaltoString(body));

		String xpath = "//w:p";
		org.docx4j.openpackaging.parts.relationships.RelationshipsPart rp = wordMLPackage.getMainDocumentPart().getRelationshipsPart();
		org.docx4j.relationships.Relationship footerRelationship = rp.getRelationshipByType(org.docx4j.openpackaging.parts.relationships.Namespaces.FOOTER);
		org.docx4j.openpackaging.parts.WordprocessingML.FooterPart footerPart = (org.docx4j.openpackaging.parts.WordprocessingML.FooterPart) rp.getPart(footerRelationship);
		java.util.List<Object> jaxbNodes = footerPart.getJAXBNodesViaXPath(xpath, true);
		//System.out.print(jaxbNodes.toString());

//		JAXBContext context = JAXBContext.newInstance(org.docx4j.wml.P.class);
//
//		Marshaller m = context.createMarshaller();
//		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		for (Object jaxbNode : jaxbNodes) {

			if (jaxbNode.getClass().getName().equals("org.docx4j.wml.P")) {
				System.out.print("true");
				org.docx4j.wml.P p1 = (org.docx4j.wml.P) jaxbNode;
				System.out.print(p1.toString());
//				m.marshal(jaxbNode, System.out);
			}
		}


		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("filename.txt"), "utf-8"))) {
			writer.write(jaxbNodes.toString());
		}

		List<SectionWrapper> sectionWrappers = wordMLPackage.getDocumentModel().getSections();
		for (SectionWrapper sw : sectionWrappers) {
			HeaderFooterPolicy hfp = sw.getHeaderFooterPolicy();

			System.out.println("\n\nSECTION  \n");

			System.out.println("Headers:");
			if (hfp.getFirstHeader() != null) System.out.println("-first");
			if (hfp.getDefaultHeader() != null) System.out.println("-default");
			if (hfp.getEvenHeader() != null) System.out.println("-even");

			System.out.println("\nFooters:");
			if (hfp.getFirstFooter() != null) System.out.println("-first");
			if (hfp.getDefaultFooter() != null) System.out.println("-default");
			if (hfp.getEvenFooter() != null) System.out.println("-even");

			HeaderPart hp = hfp.getDefaultHeader();

			java.util.List<Object> jaxbNodes1 = hp.getContent();
			for (Object jaxbNode : jaxbNodes1) {

				if (jaxbNode.getClass().getName().equals("org.docx4j.wml.P")) {
					System.out.print("true");


				}
			}


		}

		OutputStream os = new FileOutputStream("C:\\Users\\ponnl\\Downloads\\app" + ".html");
		//OutputStream os1 = new FileOutputStream("C:\\Users\\ponnl\\Downloads\\app" + ".pdf");
		HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
		String inputfilepath = "C:\\Users\\ponnl\\Downloads";
		htmlSettings.setImageDirPath(inputfilepath);
		htmlSettings.setImageTargetUri(inputfilepath.substring(inputfilepath.lastIndexOf("/")+1)
				+ "_files");
		htmlSettings.setWmlPackage(wordMLPackage);
		Docx4J.toHTML(htmlSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);

		List<Relationship> footerRelationships = getRelationshipsOfType(wordMLPackage, Namespaces.FOOTER);

		for (Relationship footer : footerRelationships) {
			FooterPart footerParts = (FooterPart) rp.getPart(footer.getId());

		}


		//Docx4J.toPDF(WordprocessingMLPackage.load(new java.io.File("C:\\Users\\ponnl\\Downloads\\Application_profile_Testdocx_1545818541820.docx")),new java.io.FileOutputStream("textsample.pdf"));

		OutputStream oss = new java.io.FileOutputStream("C:\\Users\\ponnl\\Downloads\\Application_profile_Testdocx_1545818541820.pdf");

		FOSettings foSettings = Docx4J.createFOSettings();
		//foSettings.setFoDumpFile(new java.io.File(inputfilepath + ".fo"));
//    foSettings.setWmlPackage(template);
		foSettings.setWmlPackage(wordMLPackage);

		Docx4J.toFO(foSettings, oss, Docx4J.FLAG_EXPORT_PREFER_NONXSL);*/


    }


	/*public static List<Relationship> getRelationshipsOfType(WordprocessingMLPackage document, String type) {
		List<org.docx4j.relationships.Relationship> allRelationhips = document
				.getMainDocumentPart()
				.getRelationshipsPart()
				.getRelationships()
				.getRelationship();
		List<Relationship> headerRelationships = new ArrayList<>();
		for (org.docx4j.relationships.Relationship r : allRelationhips) {
			if (r.getType().equals(type)) {
				headerRelationships.add(r);
			}
		}
		return headerRelationships;
	}*/


}
