package com.team8.webdataintegration.winter.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleFactory;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;




public class BookXMLReader extends XMLMatchableReader<Book, Attribute> implements
FusibleFactory<Book, Attribute> {

	@Override
	protected void initialiseDataset(DataSet<Book, Attribute> dataset) {
		// TODO Auto-generated method stub
		super.initialiseDataset(dataset);
		
		dataset.addAttribute(Book.TITLE);
		dataset.addAttribute(Book.ISBN);
		dataset.addAttribute(Book.RELEASE_DATE);
		dataset.addAttribute(Book.AUTHORS);
		dataset.addAttribute(Book.LANGUAGE);
		dataset.addAttribute(Book.PAGES);
		dataset.addAttribute(Book.PRICE);
		dataset.addAttribute(Book.GENRE);
		dataset.addAttribute(Book.FORMATS);
	}

	@Override
	public Book createInstanceForFusion(RecordGroup<Book, Attribute> cluster) {
		// TODO Auto-generated method stub
		List<String> ids = new LinkedList<>();

		for (Book b : cluster.getRecords()) {
			ids.add(b.getIdentifier());
		}

		Collections.sort(ids);

		String mergedId = StringUtils.join(ids, '+');

		return new Book(mergedId, "fused");
	}

	@Override
	public Book createModelFromElement(Node node, String provenanceInfo) {
		// TODO Auto-generated method stub
		
		String id = GetAttributeValueFromChildElement(node,"book", "id");

		// create the object with id and provenance information
		Book book = new Book(id, provenanceInfo);

		// fill the attributes
		book.setIsbn(getValueFromChildElement(node, "isbn"));
		book.setTitle(getValueFromChildElement(node, "title"));
		book.setLanguage(getValueFromChildElement(node, "language"));
	    book.setPages(getValueFromChildElement(node, "pages"));
	    book.setFormats(getValueFromChildElement(node, "formats"));
	    book.setGenre(getValueFromChildElement(node, "genre"));
	    book.setPublisher(getValueFromChildElement(node, "publisher"));
	    //convert string into date
	    
	    try {
	      String sPrice = getValueFromChildElement(node, "price");
	      if(sPrice != null) {
	    	  //double price = Double.parseDouble(sPrice);
	    	  book.setPrice(sPrice);
	      }
	    
	    }catch(Exception e) {
	    	System.out.println("Record ID"+ id);
	    	e.printStackTrace();
	    }

		// convert the date string into a DateTime object
	    String date = getValueFromChildElement(node, "release_date");
		try {
			
			if (date != null && !date.isEmpty()) {
			
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/u");
				LocalDateTime dt = LocalDate.parse(date, formatter).atStartOfDay();
				book.setRelease_date(dt);
			}
		} catch (java.time.format.DateTimeParseException e) {
			//catching for the case of d/M/yyyy pattern
			try {
				
				if (date != null && !date.isEmpty()) {
				    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/u");
					LocalDateTime dt = LocalDate.parse(date, formatter).atStartOfDay();
					book.setRelease_date(dt);
				}
				
			}catch(Exception ex) {
				ex.printStackTrace();
				
			}
			
			
		}

		// load the list of actors
		List<Author> authors = getObjectListFromChildElement(node, "authors",
				"author", new AuthorXMLReader(), provenanceInfo);
		book.setAuthors(authors);

		return book;
		
		
	}
	
	/***
	 * 
	 * @param node = parent node
	 * @param childName = child node name
	 * @param attributeName  = attribute of the node
	 * @return the attribute value if found on parent or the child node
	 */
	public String GetAttributeValueFromChildElement(Node node,String childName, String attributeName)
	{
		if (node.hasAttributes()) {
		   return	GetAttributeValueFromNode(node, attributeName);
		}
		
		NodeList children = node.getChildNodes();
		// iterate over the child nodes until the node with childName is found
		for (int j = 0; j < children.getLength(); j++) {
			Node child = children.item(j);

			// check the node type and the name
			if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE
					&& child.getNodeName().equals(childName)) {
				if(child.hasAttributes()) {
					return GetAttributeValueFromNode(child, attributeName);
				}
			}
		}

		return null;
	}
	
	/***
	 * 
	 * @param node = node which have attributes
	 * @param attributeName
	 * @return
	 */
	
	private String GetAttributeValueFromNode(Node node, String  attributeName)
	{
		if(node.hasAttributes()) {
			NamedNodeMap nnMap = node.getAttributes();
	        for (int j = 0; j < nnMap.getLength(); j++) {
	            Attr attr = (Attr) nnMap.item(j);
	            if(attr.getName() == attributeName) {
	            	return attr.getValue();
	            }
	        }
	        return null;
		}else {
			return null;
		}
	}
	
	

}
