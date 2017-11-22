package com.vmware.Utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;

public class JAXBUtils {
	
	private JAXBContext jaxbcontext;
	
	public JAXBUtils(Class<?>... types){
		try {
			jaxbcontext = JAXBContext.newInstance(types);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the Marshaller
	 * @param encoding
	 * @return
	 */
	public Marshaller getMarshaller(String encoding){
		Marshaller marshaller = null;;
		try {
			marshaller = jaxbcontext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			if(StringUtils.isNotBlank(encoding)){
				marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
			}
		
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return marshaller;
	}
	
	/**
	 * Get the unmarshaller
	 * @return
	 */
	public Unmarshaller getUnmarshaller(){
		Unmarshaller unmarshaller = null;
		
		try {
			unmarshaller = jaxbcontext.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return unmarshaller;
	}
	

	/**
	 * Convert the Java Object to XML String
	 * @param obj
	 * @param encoding
	 * @return
	 */
	public String objToXml(Object obj, String encoding){
		
		StringWriter sw = new StringWriter();
		try {
			this.getMarshaller(encoding).marshal(obj, sw);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}
	
	/**
	 * Convert the java Object to XML String (Specific support root element is collection)
	 * @param root
	 * @param rootName
	 * @param encoding
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String objToXml(Collection root, String rootName, String encoding){
	       try {  
	            CollectionWrapper wrapper = new CollectionWrapper();  
	            wrapper.collection = root;  
	  
	            JAXBElement<CollectionWrapper> wrapperElement = new JAXBElement<CollectionWrapper>(  
	                    new QName(rootName), CollectionWrapper.class, wrapper);  
	  
	            StringWriter writer = new StringWriter();  
	            this.getMarshaller(encoding).marshal(wrapperElement, writer);  
	  
	            return writer.toString();  
	        } catch (JAXBException e) {  
	            throw new RuntimeException(e);  
	        }  
	}
	
	/**
	 * Convert XML String to Java Object
	 * @param xmlString
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T>T xmlStringToObj(String xmlString){
		T obj = null;
		StringReader sr = new StringReader(xmlString);
		try {
			obj = (T) this.getUnmarshaller().unmarshal(sr);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	
	public static class CollectionWrapper{
		@SuppressWarnings("rawtypes")
		@XmlAnyElement
		protected Collection collection;
	}
	
}
