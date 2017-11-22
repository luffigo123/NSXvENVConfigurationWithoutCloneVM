package com.vmware.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class XmlFileOp {
	private static String PATH_RestCallXML =  "src\\main\\resources";

	/**
	 * Read xml template
	 * @param xmlFilePath
	 * @return XML file contents
	 * @author Fei
	 */
	@SuppressWarnings("resource")
	public static String readXMLContens(String xmlFilePath)
	{	
		String xmlContents = new String();
		String xmlPath = xmlFilePath;
		File f = new File(xmlPath);
		FileReader fr = null;
		BufferedReader br = null;
		
		try 
		{
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			String tempString;
			while((tempString = br.readLine()) != null)
			{
				xmlContents = xmlContents + tempString;
			}
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return xmlContents;				
	}
	
	
	/**
	 * Generate XML with ini properties' Value
	 * @param xmlContens
	 * @param xmlKeyValue
	 * @param xmlKeys
	 * @return string of XML with properties values
	 * @author Fei
	 */
	public static String setXMLInfo(String xmlContents, Hashtable<String, String> xmlKeyValue, String [] xmlKeys)
	{	
		Hashtable<String, String> ht = xmlKeyValue;
		String [] keys = xmlKeys;
		StringBuffer sb = new StringBuffer(xmlContents);
		String firstTemp = null;
		String lastTemp = null;
		
		for(int i=0; i< keys.length; i++)
		{
			firstTemp = "<" + keys[i] + ">";
			lastTemp = "</" + keys[i] + ">";
			if(sb.toString().contains(firstTemp) && sb.toString().contains(lastTemp))
			{
				String value = (ht.get(keys[i])).toString();
				int indexFirst = sb.toString().indexOf(firstTemp) + firstTemp.length();			
				int indexLast = sb.toString().indexOf(lastTemp);
				sb.delete(indexFirst, indexLast);
				sb.insert(indexFirst, value);			
			}		
		}	
		return sb.toString();
		
	}
	
	/**
	 * Generate xmlContents string with values. Delete the property when value is "NoSet".
	 * @param xmlContents
	 * @param xmlKeyValue
	 * @param xmlKeys
	 * @return
	 */
	public static String generateXMLContentsWithValues(String[] xmlKeys,String[] xmlValues, String xmlFilePath)
	{
 		Hashtable<String, String> ht = new Hashtable<String, String>();
 		for(int i=0; i< xmlKeys.length; i++)
 		{
 			if(xmlValues[i] == null)
 			{
 				xmlValues[i] = "";
 			}
 			ht.put(xmlKeys[i], xmlValues[i]);
 		}
 		
 		//Get the final contens of xml
 		String xmlContents = readXMLContens(xmlFilePath);
 		//String tempContents = xmlContents.replaceAll("\\s*", "");
 		
		String [] keys = xmlKeys;
	
		for(int i=0; i< keys.length; i++)
		{	
			String value = (ht.get(keys[i])).toString();
			if(!keys[i].contains("_"))
			{
				Pattern p = Pattern.compile("(?<=<" + keys[i] + ">).*?(?=</" + keys[i] + ">)");
				Matcher m = p.matcher(xmlContents);
				if(m.find()){
					xmlContents = xmlContents.replaceAll("(?<=<" + keys[i] + ">).*?(?=</" + keys[i] + ">)", value);
				}
				if(xmlContents.contains("<" + keys[i] + ">NoSet</" + keys[i] + ">"))
				{
					xmlContents = xmlContents.replace("<" + keys[i] + ">NoSet</" + keys[i] + ">", "");
				}
			}
			else 
			{
				String[] targetTemp = keys[i].split("_");
				String firstSection = targetTemp[0];
				String secondSection = targetTemp[1];
				String temp = null;
				//int fisrtSectionIndex = xmlContents.indexOf("<" + firstSection + ">");
				//int firstSectionEndIndex = 0;
				Pattern p = Pattern.compile("(?<=<" + firstSection + ">).*?(?=</" + firstSection + ">)");
				Matcher m = p.matcher(xmlContents);
				if(m.find())
				{
					//xmlContents = xmlContents.replaceAll(, value);
					temp = m.group();
					Pattern p2 = Pattern.compile("(?<=<" + secondSection + ">).*?(?=</" + secondSection + ">)");
					Matcher m2 = p2.matcher(temp);
					String temp2 = null;
					if(m2.find())
					{
						temp2 =temp.replaceAll("(?<=<" + secondSection + ">).*?(?=</" + secondSection + ">)", value);
						if(temp2.contains("<" + secondSection + ">NoSet</" + secondSection + ">"))
						{
							temp2 = temp2.replace("<" + secondSection + ">NoSet</" + secondSection + ">", "");
						}
						xmlContents = xmlContents.replace(temp, temp2);
						
					}		
					//xmlContents = xmlContents.replace(temp, temp2);
				}
			}
				
		}	
		return xmlContents;
	}
	
	/**
	 * Get specified property value
	 * @param xmlContens
	 * @param targetKey
	 * @return String
	 * @author Fei
	 */
	public static String getXMLItemValue(String xmlContents, String targetKey)
	{
		String targetValue = null;
		int indexBegin = 0;
		int indexEnd = 0;
		try {
			if(xmlContents != null && xmlContents.contains(targetKey))
			{
				String tagetBegin = "<" + targetKey + ">";
				String targetEnd = "</" + targetKey + ">";
				if(xmlContents.contains(tagetBegin) && xmlContents.contains(targetEnd))
				{
					indexBegin = xmlContents.indexOf(targetKey) + tagetBegin.length()-1;
					indexEnd = xmlContents.indexOf(targetEnd);
				}
				targetValue = xmlContents.substring(indexBegin, indexEnd);
			}
			else 
			{
				targetValue = null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return targetValue;
	}
	
	/**
	 * Delete the line of xmlContents which contains NoSet String
	 * @param xmlContents
	 * @return
	 * @throws IOException
	 */
	public static String deleteNoSetLinesFromXML(String xmlContents)
	{
		String result = null;

		return result;
	}
	
	
	/**
	 * Get the values of specified xml property
	 * @param xmlContens
	 * @param targetKey
	 * @return ArrayList<String>
	 * @throws IOException
	 * @author Fei
	 */
	public static ArrayList<String> getPropertyValues(String xmlContens, String targetKey) throws IOException
	{
		ArrayList<String> aList = new ArrayList<String>();
		StringReader sr;
		BufferedReader br;
		if(xmlContens != null && xmlContens.contains(targetKey))
		{
			sr = new StringReader(xmlContens);
			br = new BufferedReader(sr);
			String readLine = null;
			int indexBegin = 0;
			int indexEnd = 0;
			String targetValue =  null;
			while((readLine = br.readLine()) != null)
			{
				String tagetBegin = "<" + targetKey + ">";
				String targetEnd = "</" + targetKey + ">";
				if(readLine.contains(tagetBegin) && readLine.contains(targetEnd))
				{
					indexBegin = readLine.indexOf(targetKey) + tagetBegin.length()-1;
					indexEnd = readLine.indexOf(targetEnd);
					targetValue = readLine.substring(indexBegin, indexEnd);
					aList.add(targetValue);
				}
			}
		}
		else 
		{
			System.out.println("The target is not exist in the xml.");
			return aList;
		}
		
		return aList;
	}
	
	/**
	 * Check the return result
	 * @param responseString
	 * @return
	 * @author Fei
	 */
 	public static boolean CheckResult(String responseString)
	{
		boolean resultFlag = false;
		String flag = "<status>";
		String flagEnd = "</status>";
		int indexFirst = responseString.indexOf(flag) + flag.length();
		int indexEnd = responseString.indexOf(flagEnd);
		String statusWord = responseString.substring(indexFirst, indexEnd);
		//System.out.println(statusWord);
		if(statusWord.equals("false"))
		{
			resultFlag = false;
		}
		else if(statusWord.equals("true"))
		{
			resultFlag = true;
		}
		
		return resultFlag;
	}
 	
	/**
	 * Generate xmlContents with xml template and iniValues
	 * @param xmlKeys
	 * @param xmlValues
	 * @param xmlFilePath - 
	 * @return
	 */
 	public static String getXMLContensWithValues(String[] xmlKeys, String[] xmlValues, String xmlFilePath)
 	{
 		String contents = null;
 		Hashtable<String, String> ht = new Hashtable<String, String>();
 		for(int i=0; i< xmlKeys.length; i++)
 		{
 			ht.put(xmlKeys[i], xmlValues[i]);
 		}
 		
 		//Get the final contens of xml
 		String xmlSSO = readXMLContens(xmlFilePath);
 		contents = setXMLInfo(xmlSSO, ht, xmlKeys);
 		return contents;
 	}
 	
 	/**
 	 * Modify the auery content info
 	 * @param queryContents
 	 * @param xmlKeys
 	 * @param xmlValues
 	 * @return
 	 */
 	public static String modifyXMLContents(String queryContents, String[] xmlKeys, String[] xmlValues)
 	{
 		String contents = null;
 		Hashtable<String, String> ht = new Hashtable<String, String>();
 		for(int i=0; i< xmlKeys.length; i++)
 		{
 			ht.put(xmlKeys[i], xmlValues[i]);
 		}
 		
 		//Get the final contens of xml
 		contents = setXMLInfo(queryContents, ht, xmlKeys);
 		return contents;
 	}
 	
	/**Read and clear the value in the XML file from the local drive, automatically add the full path from Program.FileDirString
	 * 
	 * @param fileName
	 * @return the value cleared 
	 */
	public static String readAndClearXmlFile(String fileName) {
		String xmlString = "";
		Pattern p = Pattern.compile("(?<=>).+(?=</)");
		String xmlFileName = PATH_RestCallXML + "\\" + fileName;
		
		try
		{
			FileReader fr = new FileReader(xmlFileName);
			BufferedReader br = new BufferedReader(fr);
			String s = null;
			while((s=br.readLine())!=null)
			{
				Matcher m = p.matcher(s);
				if (m.find())
				{
					String mStr = m.group();
					s = s.replace(mStr, "");
				}
				xmlString = xmlString + s;
			}
			
			br.close();
			fr.close();
			return xmlString;
		} catch (IOException e){
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * Read the xml string and find the value for the specified tag then return it.
	 * @param xmlContents
	 * @param xmlTag
	 * @return
	 */
	public static String readValueFromXmlString(String xmlContents, String xmlTag) {
		String value = "";
		Pattern p = Pattern.compile("(?<=" + xmlTag + ">).+?(?=</" + xmlTag + ">)");
		
		try {
			
			Matcher m = p.matcher(xmlContents);
			if(m.find()) {
				value = m.group().trim();
				return value;
			
			}
			else 
				return value;
			
		} catch (Exception e) {
			e.printStackTrace();
			return value;
		}
		
		
	}

	

	
	
	/**
	 * Generate the xml String from the sample xml file, using the value in parameters instead of reading from config.txt, applicable for some values not from config file! 
	 * return the full xml string with key and value filled in config.ini, according to the specified xml file, Format: String xmlFileContents = GenerateXMLString (String XMLFileName, String value1, String XMLFileKey1, String value2, String, XMLFileKey2 ...);
	 * @param args: fileName
	 * 				value1, tag1InXMLFile,
	 * 				value2, tag2InXMLFile, ...
	 * 
	 * @return the full XML request body as a single string
	 */
	public static String generateXmlStringWithoutConfig (String...args) {
		
		String xmlFileStrOrig = readAndClearXmlFile(args[0]);
		StringBuffer xmlFileStrBuf = new StringBuffer(xmlFileStrOrig);
		
		for (int i=1; i<args.length; i++)
		{
			//JOptionPane.showMessageDialog(null, configValue);
			String value = args[i];
			i++;
			if(xmlFileStrBuf.toString().contains(args[i]))
			{
				int j = xmlFileStrBuf.toString().indexOf("</"+args[i]);
				xmlFileStrBuf.insert(j, value);
			}
			
			
		}
				
		return xmlFileStrBuf.toString();
	}
	

	/** Write the xml contents into the specified filename
	 * 
	 * @param fileName
	 * @param xmlContents
	 * @return
	 */
	public static Boolean writeAsXmlFile(String fileName, String xmlContents) {
		String fullFilePath = PATH_RestCallXML + "\\" + fileName;
		
		try {
					
				File newXMLFile = new File(fullFilePath);
				
				if(!newXMLFile.exists()){
					newXMLFile.createNewFile();
				}
				
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fullFilePath)), "UTF-8"));
				
				bw.write(xmlContents);
				bw.close();
				System.out.println("Wrote the contents into the file " + fileName);
				return true;
					
			} catch (Exception e) {
				return false;
				// TODO: handle exception
			}
	}
	
	
	
	
	/**
	 * Find the value including subStringInValue for the specified xml tag in the xml contents.
	 * @param xmlContents
	 * @param xmlTag
	 * @return the value string
	 */
	public static String readSpecialValueFromXmlString(String xmlContents, String xmlTag, String subStringInValue) {
		String value = "";
		Pattern p = Pattern.compile("(?<=" + xmlTag + ">)" + subStringInValue + "\\d(?=</" + xmlTag + ">)");
		
		try {
			
			Matcher m = p.matcher(xmlContents);
			if(m.find()) {
				value = m.group().trim();
				return value;
			
			}
			else 
				return value;
			
		} catch (Exception e) {
			e.printStackTrace();
			return value;
		}
		
		
	}
	public static void print4Spaces (int spacesNumber) {
		for (int i = 0; i < spacesNumber; i++) {
			System.out.print("    ");
		}
	}
	/**
	 * Generate the xml string from the sample xml file, with the tag and value in pairs as parameters, in sequence, return the full xml, The tags not in the list or specify value null would keep the value in the xml template
	 * @param args: filename(or treat it as XML string if file not exist)
	 * 				tag1, value1,
	 * 				tag2, value2,... (The tags not in the list or value in param as null would keep the value in the xml template, so must specify a value (even blank "") for those invalid values (e.g. format samples).
	 * @return String full XML request body as a single string
	*/
	
	public static String generateXMLStringCommon (String...args) {
		int i=0;
		String xmlString = "";
		String xmlFilePath = PATH_RestCallXML + "\\" + args[i++];
		String s = "";
		
		//If args[0] as an existed file
		if(chekFileOrString(xmlFilePath)) {
			try {
				
				FileReader fr = new FileReader (xmlFilePath);
				
				BufferedReader br = new BufferedReader(fr);
				
					while((s = br.readLine())!=null) {
						if(i<args.length) {
							String tag = args[i];
							String value = Matcher.quoteReplacement(args[i+1]);
							Pattern p = Pattern.compile("(?<=<" + tag + ">).*(?=</" + tag + ">)");
							Matcher m = p.matcher(s);
							if(m.find()) {
								//String mStr = m.group().trim();  //s.replace(mStr, value) will wrongly fill the value many times if the value is empty in the xml tag.
								if(value!=null) {					//If value as null, no change it at all
									s = s.replaceAll("(?<=<" + tag + ">).*(?=</" + tag + ">)", value); //replaceAll(regex String can work well now.
								}
								i+=2;
							}
						}
					xmlString += s;	
					}
					
					br.close();
					fr.close();
			} catch (Exception e) {
				e.printStackTrace();
				//LogFileOp.writeLogFile("Generate the XML String from Xml template", "Failed to generate the xml string because of " + e.toString());
			}
		
		}
		//If args[1] as a single line xml string e.g. query results
		else {
			String strTmp;
			//Remove the <?xml> tag e.g. <?xml version="1.0" encoding="UTF-8"?>
			String xmlHeaderPStr = "<\\?xml.+?>";
			Pattern xmlHeaderP = Pattern.compile(xmlHeaderPStr);
			Matcher xmlHeaderM = xmlHeaderP.matcher(args[0]);
			if(xmlHeaderM.find()) {
				String xmlHeaderStr = xmlHeaderM.group();
				strTmp = args[0].replace(xmlHeaderStr, "");
			}
			else {
				strTmp = args[0];
			}
			
			for (int j = 1; j < args.length; j++) {
				String tagPStr = ".*?<" + args[j] + ">.*?</" + args[j] + ">";
				Pattern tagP = Pattern.compile(tagPStr);
				Matcher tagM = tagP.matcher(strTmp);
				if(tagM.find()) {
					String tagStr = tagM.group();
					strTmp = strTmp.replaceFirst(tagPStr, "");
					if(args[j+1]!=null) {
						tagStr = tagStr.replaceAll("(?<=<" + args[j] + ">).*(?=</" + args[j] + ">)", args[++j]);
					}
					else {
						j++;
					}
					
					xmlString += tagStr;
					
				}
			}
			if(!strTmp.isEmpty()) {
				xmlString += strTmp;
			}
		}
		
		
		
		return xmlString;
			
	}
	
	
	/**
	 * Break the whole line XML string output of GET request into readable lines
	 * @param xmlString
	 * @return and display the lined contents.
	 */
	public static String breakAndPrintXMLStringInLines(String xmlString) {
		
		//String breakChar = System.getProperty("line.separator");
		String breakChar = "\n";
		/**
		 * <virtualWireCreateSpec><name>LogicalSwitch001</name><description>Test the Logical Switch</description><tenantId>Tenant name 001</tenantId></virtualWireCreateSpec>
		 */
		String startTagStr = "<[^<>]+>";
		String endTagStr = "</[^<>]+>";
		String tagStr = "(?<=<)[^<>]+(?=>)|(?<=</)[^<>]+(?=>)";
		
		Pattern startTagPtn = Pattern.compile(startTagStr);
		Pattern endTagPtn = Pattern.compile(endTagStr);
		Pattern tagPtn = Pattern.compile(tagStr);
		
		String linedStr = "";
		String tmpStr = xmlString.trim();
		
		while (!tmpStr.isEmpty()) {
			
			Matcher tagM = tagPtn.matcher(tmpStr);
			String lineStr = "";
			
			if(tagM.find()) {
				String tag = tagM.group().trim();
				String tagPStr = "<" + tag + ">[^<>]+</" + tag + ">";
				Pattern pairTagsPtn = Pattern.compile(tagPStr);
				
				Matcher pairTagsM = pairTagsPtn.matcher(tmpStr);
				
				if(pairTagsM.find()) {
					lineStr = pairTagsM.group().trim(); 
					int i = lineStr.length();
					tmpStr = tmpStr.substring(i);
					System.out.println(lineStr);
				}
				
				
				else {
					Matcher startTagM = startTagPtn.matcher(tmpStr);
					if(startTagM.find()) {
						lineStr = startTagM.group().trim();
						int i = lineStr.length();
						tmpStr = tmpStr.substring(i);
						System.out.println(lineStr);
					}
					
					else {
						Matcher endTagM = endTagPtn.matcher(tmpStr);
						if(endTagM.find()) {
							lineStr = endTagPtn.matcher(tmpStr).group().trim();
							int i = lineStr.length();
							tmpStr = tmpStr.substring(i);
							System.out.println(lineStr);
						}
					}
					
				}
				
				linedStr = linedStr + breakChar + lineStr;
			}
				
		}
		
		return linedStr.trim();
	}
	
	
	/**
	 * Find the Nearest specified tag's value behind the <tag1>value1</tag1> in the String of param1, e.g. used to find the ServiceID which behind the Service name in the queried XML string. 
	 * @param xMLStringContent
	 * @param tag1
	 * @param value1
	 * @param tag2
	 * @return and println the found value2 string
	 */
	public static String findNearestAfterTagValue (String xMLStringContent, String tag1, String value1, String tag2) {
		String value2 = "";
		//(?<=<serviceName>Guest Introspection</serviceName>.*?<serviceId>).+?(?=</serviceId>)
		String value2PtnStr = "(?<=<" + tag1 + ">" + value1 + "</" + tag1 + ">.{1,2048}?<" + tag2 + ">).+?(?=</" + tag2 + ">)"; //Change the .* to .{1,2048} as the JDK 1.7 doesn't support look-behind group with variable maximum length
		Pattern value2Ptn = Pattern.compile(value2PtnStr);
		Matcher value2M = value2Ptn.matcher(xMLStringContent);
		if(value2M.find()) {
			value2 = value2M.group().trim();
		}
		System.out.println("The Matched value is " + value2);
		return value2;
	}
	
	
	/**
	 * Find the Nearest specified tag's value ahead the <tag2>value2</tag2> in the String of param1, e.g. used to find the objectId according to the Security Group name in the queried XML String 
	 * @param xMLStringContent
	 * @param tag1
	 * @param tag2
	 * @param value2
	 * @return and println the found value1 string
	 */
	public static String findNearestBeforeTagValue (String xMLStringContent, String tag1, String tag2, String value2) {
		String value1 = "";
		
		//.+(?=</objectId>.*?<name>Security Policy002</name>)
		String tmpStr = "";
		String tmpPtnStr = ".+(?=</" + tag1 + ">.*?<" + tag2 + ">" + value2 + "</" + tag2 + ">)";
		Pattern tmpPtn = Pattern.compile(tmpPtnStr);
		Matcher tmpM = tmpPtn.matcher(xMLStringContent);
		if(tmpM.find()) {
			tmpStr = tmpM.group().trim();
		}

		//(?<=<objectId>)[^<>]+$
		String valuePtnStr = "(?<=<" + tag1 + ">)[^<>/]+$";
		Pattern valuePtn = Pattern.compile(valuePtnStr);
		Matcher valueM = valuePtn.matcher(tmpStr);
		if(valueM.find()) {
			value1 = valueM.group().trim();
		}
		
		System.out.println("The Matched value is " + value1);
		
		return value1;
	}
	
	
	/**
	 * Set the tag's value to hashtable. E.G <tag1>Figo</tag1> <tag2>1001</tag2>
	 * "Figo" for key, "1001" for value.
	 * @param xmlContents
	 * @param tag1
	 * @param tag2
	 * @return
	 */
	public static Hashtable<String,String> getRulesTagValues(String xmlContents,String tag1,String tag2)
	{
		Hashtable<String,String> ht = new Hashtable<String,String>();
		ArrayList<String> alOne = new ArrayList<String>();
		ArrayList<String> alTwo = new ArrayList<String>();
		int count1 = 0;
		int count2 = 0;
		Pattern p1 = Pattern.compile("(?<=<" + tag1 + ">)[.\\w\\s]*(?=</" + tag1 + ">)");
		Matcher m1 = p1.matcher(xmlContents);
		while(m1.find())
		{
			String temp = m1.group();
			temp=temp.replaceAll("\\s*", "");
			alOne.add(temp);
			count1++;
		}
		
		Pattern p2 = Pattern.compile("(?<=<" + tag2 + ">)[.\\w\\s]*(?=</" + tag2 + ">)");
		Matcher m2 = p2.matcher(xmlContents);
		while(m2.find())
		{
			String temp2 = m2.group();
			temp2 = temp2.replaceAll("\\s*", "");
			alTwo.add(temp2);
			count2++;
		}
		
		
		if (count1 == count2)
		{
			for(int j=0; j<count1;j++)
			{
				ht.put(alOne.get(j), alTwo.get(j));
			}
		}
		else {
			System.out.println("The" + tag1 + "and" + tag2 + "number are not matched in teh xmlContents.S");
		}
		return ht;
	}
	
	
	/**
	 *Print the indented XML string in the console for easy reading 
	 *As Eclipse JAVA can't handle escape character /b in outputs, so use the 4 space chars instead 
	 *@param XMLString
	 */
	public static void printIndentedXMLString (String XMLString) {
		//System.out.println(XMLString);
		String tmpStr = XMLString.trim();
		int tabN = 0;
		
		while (tmpStr.trim().length()>0) {
			String tag;
			String key;
			String tagPtnStr = "<.+?>";
			Pattern tagPtn = Pattern.compile(tagPtnStr);
			Matcher tagMc = tagPtn.matcher(tmpStr);
			if (tagMc.find()) {
				//find out the tag
				tag = tagMc.group();
				if(tag.startsWith("<?xml"))  {
					System.out.println(tag);
					tmpStr = tmpStr.substring(tag.length()).trim();
				}
				
				else if(tag.contains("/>")) {
					print4Spaces(tabN);
					System.out.print(tag + "\r\n");
					tmpStr = tmpStr.substring(tag.length());
				}
				
				else {
					//get the key
					String keyPtnStr = "(?<=</)|(?<=<)[^/<>]+?((?=/>)|(?=>))";
					Pattern keyPtn = Pattern.compile(keyPtnStr);
					Matcher keyMc = keyPtn.matcher(tmpStr);
					
					if (keyMc.find()) {
						key = keyMc.group();
						//left tag not in pair
						String tagNPairLStr = "<" + key + ">(?=<[^/])";
						Pattern tagNPairLPtn = Pattern.compile(tagNPairLStr);
						Matcher tagNPairLM = tagNPairLPtn.matcher(tmpStr);
						if(tagNPairLM.find()) {
							String tagNPairL = tagNPairLM.group().trim();
							print4Spaces(tabN++);
							System.out.print(tagNPairL + "\r\n");
							tmpStr = tmpStr.substring(tagNPairL.length());
						}
						else {
							//right tag not in pair
							String tagNPairRStr = "^</[^<]+?>";
							Pattern tagNPairRPtn = Pattern.compile(tagNPairRStr);
							Matcher tagNPairRM = tagNPairRPtn.matcher(tmpStr);
							if(tagNPairRM.find()) {
								String tagNPairR = tagNPairRM.group().trim();
								print4Spaces((tabN<=0)?tabN:--tabN);  //check if indents set to none
								System.out.print(tagNPairR + "\r\n");
								tmpStr = tmpStr.substring(tagNPairR.length()).trim();

							
							}
							else {
								//tags in pair and other occasions.
								String tagPairStr = "(^<" + key + ">[^<>]*?</" + key + ">)|(^<[^/]*?" + key + "[^<>]*?>)";
								Pattern tagPairPtn = Pattern.compile(tagPairStr);
								Matcher tagPairM = tagPairPtn.matcher(tmpStr);
								if(tagPairM.find()) {
									String tagPair = tagPairM.group();
									print4Spaces(tabN);
									System.out.print(tagPair + "\r\n");
									tmpStr = tmpStr.substring(tagPair.length());
																	
								}
								
							}
						}
						
						

					}
					
					
					
					
				}
			}
			
			
			
 
		}
		
		
	}
	
	
	/**
	 * Get property value by specific tag
	 * @param xmlContents
	 * @param tag
	 * @return
	 * @author Fei
	 */
	public static String getValueBySpecificTag(String xmlContents, String tag)
	{
		String result = null;
		
		if(!tag.contains("_") && xmlContents.contains("<" + tag + ">"))
		{
			Pattern p1 = Pattern.compile("(?<=<" + tag + ">).*?(?=</" + tag + ">)");
			Matcher m1 = p1.matcher(xmlContents);
			if(m1.find())
			{
				result = m1.group();
			}
		}
		if(tag.contains("_")) 
		{
			String[] targetTemp = tag.split("_");
			String firstSection = targetTemp[0];
			String secondSection = targetTemp[1];
			String temp = null;
			if(xmlContents.contains("<" + firstSection + ">"))
			{
				Pattern p = Pattern.compile("(?<=<" + firstSection + ">).*?(?=</" + firstSection + ">)");
				Matcher m = p.matcher(xmlContents);
				if(m.find())
				{
					temp = m.group();
					if(temp.contains("<" + secondSection + ">"))
					{
						Pattern p2 = Pattern.compile("(?<=<" + secondSection + ">).*?(?=</" + secondSection + ">)");
						Matcher m2 = p2.matcher(temp);
						if(m2.find())
						{
							result = m2.group();
						}		
					}	
				}
			}	
		}
		return result;
	}
	
	/**
	 * Check the tag if exist in the xmlContents
	 * @return
	 * @author Fei
	 */
	public static boolean checkTagExist(String xmlContents, String tag)
	{
		boolean result = false;
		
		if(!tag.contains("_") && xmlContents.contains("<" + tag + ">"))
		{
				result = true;	
		}
		if(tag.contains("_"))
		{
			String[] targetTemp = tag.split("_");
			String firstSection = targetTemp[0];
			String secondSection = targetTemp[1];
			if(xmlContents.contains("<" + firstSection + ">"))
			{
				String temp = null;
				Pattern p = Pattern.compile("(?<=<" + firstSection + ">).*?(?=</" + firstSection + ">)");
				Matcher m = p.matcher(xmlContents);
				if(m.find())
				{
					temp = m.group();
					if(temp.contains("<" + secondSection + ">"))
					{
						result = true;
					}
				}
			}
			
		}
		return result;
	}
	
	public static String generateXMLContentsWithValues2(String[] xmlKeys,String[] xmlValues, String xmlFilePath)
	{
 		Hashtable<String, String> ht = new Hashtable<String, String>();
 		for(int i=0; i< xmlKeys.length; i++)
 		{
 			if(xmlValues[i] == null)
 			{
 				xmlValues[i] = "";
 			}
 			ht.put(xmlKeys[i], xmlValues[i]);
 		}
 		
 		//Get the final contens of xml
 		String xmlContents = readXMLContens(xmlFilePath);
	
		String [] keys = xmlKeys;
	
		for(int i=0; i< keys.length; i++)
		{	
			String value = (ht.get(keys[i])).toString();
			if(!keys[i].contains("_"))
			{
				Pattern p = Pattern.compile("(?<=<" + keys[i] + ">).*?(?=</" + keys[i] + ">)");
				Matcher m = p.matcher(xmlContents);
				if(m.find()){
					xmlContents = xmlContents.replaceAll("(?<=<" + keys[i] + ">).*?(?=</" + keys[i] + ">)", value);
				}
			}
			else 
			{
				String[] targetTemp = keys[i].split("_");
				String firstSection = targetTemp[0];
				String secondSection = targetTemp[1];
				String temp = null;
				Pattern p = Pattern.compile("(?<=<" + firstSection + ">).*?(?=</" + firstSection + ">)");
				Matcher m = p.matcher(xmlContents);
				if(m.find())
				{
					//xmlContents = xmlContents.replaceAll(, value);
					temp = m.group(0);
					Pattern p2 = Pattern.compile("(?<=<" + secondSection + ">).*?(?=</" + secondSection + ">)");
					Matcher m2 = p2.matcher(temp);
					String temp2 = null;
					if(m2.find())
					{
						temp2 =temp.replaceAll("(?<=<" + secondSection + ">).*?(?=</" + secondSection + ">)", value);
						if(temp2.contains("<" + secondSection + ">NoSet</" + secondSection + ">"))
						{
							temp2 = temp2.replace("<" + secondSection + ">NoSet</" + secondSection + ">", "");
						}
						xmlContents = xmlContents.replace(temp, temp2);
						
					}		
					//xmlContents = xmlContents.replace(temp, temp2);
				}
			}
				
		}	
		return xmlContents;
	}
	
	
	/**
	 * Get specific tag's values
	 * @param tag
	 * @param xmlContents
	 * @return
	 */
	public static ArrayList<String> getSpecificTagValues(String tag, String xmlContents)
	{
		ArrayList<String> list = new ArrayList<String>();
		Pattern p = Pattern.compile("(?<=<" + tag + ">)[^/]*(?=</" + tag + ">)");
		Matcher m = p.matcher(xmlContents);
		
		while(m.find())
		{
			list.add(m.group());
		}
		return list;
	}
	

	/**
	 * @Author: Bo Cao Jan 13, 2015
	 * @param tagsPathWithValues format: ParentTag^ChildTag#No@Value (The number starts from 1 and less than 10) assume ParentTag unique (the symbols ^ # % )
	 * @param xmlContents
	 * @return the new Xml string filled the values into the specified tags
	 */
	public static String fillValuesIntoXmlContents (String[] tagsPathWithValue, String xmlContents) {
		
		//StringBuilder xmlContentsSb = new StringBuilder(xmlContents);
		StringBuilder newXmlContents = new StringBuilder(xmlContents);
		String parentGroupBody;
		String newParenGroupBody;
				
		String[] parentTags = new String[tagsPathWithValue.length]; //The Parent tags, leave blank if the child tag unique
		String[] childTags = new String[tagsPathWithValue.length]; //The Child tags
		int[] index = new int[tagsPathWithValue.length]; //The index of the child tag, if it's unique, not set it at all
		String[] values = new String[tagsPathWithValue.length]; //the values for the child tag
		
		//Patterns for ParentTag, index and value , tag /w ParentTag^ChildTag#No@Value
		String parentTagPStr = "\\w+?(?=\\^)";
		String childTagPStr = "(?>=\\^)?\\w+?(?=\\#)|(?=\\@)";
		String indexPStr = "(?>=\\#)\\d+?(?=\\@)";
		String valuePStr = "(?>=@).+$";
		
		Pattern parentTagP = Pattern.compile(parentTagPStr);
		Pattern childTagP = Pattern.compile(childTagPStr);
		Pattern indexP = Pattern.compile(indexPStr);
		Pattern valueP = Pattern.compile(valuePStr);
		
		int startIndex;
		int endIndex;
		
		//Locate the tags and filled the values
		for(int i = 0; i < tagsPathWithValue.length; i++) {
			
			//Read the Parent tag
			if(tagsPathWithValue[i].contains("^")) {
				Matcher parentTagM = parentTagP.matcher(tagsPathWithValue[i]);
				if(parentTagM.find()) {
					parentTags[i] = parentTagM.group();
				}
			
			}
			else { 
				parentTags[i] = "";
			}
			
			//Read the Child tag
			Matcher childTagM = childTagP.matcher(tagsPathWithValue[i]);
			if (childTagM.find()) {
				childTags[i] = childTagM.group();
			}
			else {
				childTags[i] = "";
			}
			
			//Read the index number (only 1 numeric char! max: 9)
			
			if (tagsPathWithValue[i].contains("#")) {
				int iIndexStart = tagsPathWithValue[i].indexOf("#") + 1;
				index[i] = Integer.parseInt(tagsPathWithValue[i].substring(iIndexStart, iIndexStart+1));
			
			}
			else {
				index[i] = 1;
			}
			
			//Read the Value for the tag
			System.out.println(tagsPathWithValue[i]);
			int valueIndex = tagsPathWithValue[i].indexOf("@");
			values[i] = tagsPathWithValue[i].substring(valueIndex+1);
						
			String parentBodyPStr = "<" + parentTags[i] + ">.*?<" + childTags[i] + ">.*?</" + parentTags[i] + ">";
			Pattern parentBodyP = Pattern.compile(parentBodyPStr);
			Matcher parentBodyM = parentBodyP.matcher(newXmlContents.toString());
			if(parentBodyM.find()) {
				parentGroupBody = parentBodyM.group();
				startIndex = parentBodyM.start();
				endIndex = parentBodyM.end();
				
				/*
				System.out.println(parentTags[i]);
				System.out.println(childTags[i]);
				System.out.println(index[i]);
				System.out.println(values[i]);*/
				
				newParenGroupBody = fillInTagNOfParentValue(parentTags[i],childTags[i], index[i], values[i], parentGroupBody);
				newXmlContents = newXmlContents.replace(startIndex, endIndex, newParenGroupBody);
				System.out.println(startIndex + ", " + endIndex + ", " + newParenGroupBody + ", " + newXmlContents);
				
			}
			
		}
		return newXmlContents.toString();
			
		
	}
	
	/**
	 * Fill the specified index of tag with the value in the xml string block, format: fillInTagNsValue(String tag, int order, String value, String xmlbody)
	 * @param tag
	 * @param order
	 * @param value
	 * @param xmlBlock
	 * @return
	 */
	public static String fillInTagNsValue (String tag, int order, String value, String xmlBlock) {
		StringBuilder xmlBlockSb = new StringBuilder(xmlBlock);
		StringBuilder newXmlBlock = new StringBuilder();
		String TagPairStr = "";
		String newTagPairStr = "";
		String tagPairPStr = "<" + tag + ">" + ".*?</" + tag + ">";
		Pattern tagPairP = Pattern.compile(tagPairPStr);
		Matcher tagPairM = tagPairP.matcher(xmlBlock);
		int i = 0;
		int startIndex = 0;
		int endIndex = 0;
		
		while(i < order) {
			if(tagPairM.find()) {
				startIndex = tagPairM.start();
				endIndex = tagPairM.end();
				TagPairStr = tagPairM.group();
				//System.out.println("startindex:" + startIndex + " " + endIndex);
				//CHECK IF THE CORRECT PARENT!!!
			}
			i++;
		}
		
		//System.out.println("tagpairstr= " + TagPairStr);
		newTagPairStr = TagPairStr.replaceFirst("(?<=>).*?(?=</)", value);
		//System.out.println("newtagpair: " + newTagPairStr);
		
		newXmlBlock = xmlBlockSb.replace(startIndex, endIndex, newTagPairStr);
		System.out.println("newxml: " + newXmlBlock);
		return newXmlBlock.toString();
	}
	
	
	/**
	 * Find the nearest parent tag name for the specified tag with value 
	 * @param tag
	 * @param value
	 * @param xmlBlock
	 * @return
	 */
	public static String findParentTagValue (String tag, String value, String xmlBlock) {
		String parentTag = "";
		String tagPStr = "<" + tag + ">" + value + "</" + tag + ">";
		Pattern tagP = Pattern.compile(tagPStr);
		Matcher tagM = tagP.matcher(xmlBlock);
		int tagIndex;
		String firstHalf;
		if(tagM.find()) {
			tagIndex = tagM.start();
			firstHalf = xmlBlock.substring(0, tagIndex);
			//
			
			String parentTagPStr = "(?<=<)[^</]+?(?=>$)";
			Pattern parentTagP = Pattern.compile(parentTagPStr);
			String lastTagPStr = "(?<=</)\\w+?(?=>$)|(?=/>$)";
			Pattern lastTagP = Pattern.compile(lastTagPStr);
			
			do {
				Matcher parentTagM = parentTagP.matcher(firstHalf);
				if(parentTagM.find()) {
					parentTag = parentTagM.group();
					break;
				}
				else {
					Matcher lastTagM = lastTagP.matcher(firstHalf);
					if(lastTagM.find()) {
						String lastTag = lastTagM.group();
						String lastPairPStr = "<" + lastTag + ">.*?</" + lastTag + ">$";
						Pattern lastPairP = Pattern.compile(lastPairPStr);
						Matcher lastPairM = lastPairP.matcher(firstHalf);
						if(lastPairM.find()) {
							int lastPairIndex = lastPairM.start();
							firstHalf = firstHalf.substring(0, lastPairIndex);
							System.out.println(firstHalf);
						}
						
					}
				}
			}
			while(!firstHalf.isEmpty());
			
		}
				
		return parentTag;
	}
	
	
	/**
	 * 
	 * @param tag
	 * @param order
	 * @param value
	 * @param xmlBlock
	 * @return
	 */
	public static String fillInTagNOfParentValue (String parentTag, String tag, int order, String value, String xmlBlock) {
		StringBuilder xmlBlockSb = new StringBuilder(xmlBlock);
		StringBuilder newXmlBlock = new StringBuilder();
		String TagPairStr = "";
		String newTagPairStr = "";
		String tagPairWithParentPStr = "(?<=<" + parentTag + ">.{0,2048}?)<" + tag + ">?" + ".*?</" + tag + ">(?=.*?</" + parentTag + ">?)";
		Pattern tagPairWithParentP = Pattern.compile(tagPairWithParentPStr);
		Matcher tagPairWithParentM = tagPairWithParentP.matcher(xmlBlock);
		int i = 0;
		int startIndex = 0;
		int endIndex = 0;
		
		while(i < order) {
			if(tagPairWithParentM.find()) {
				startIndex = tagPairWithParentM.start();
				endIndex = tagPairWithParentM.end();
				TagPairStr = tagPairWithParentM.group();
				//System.out.println("startindex:" + startIndex + " " + endIndex);
			}
			i++;
		}
		
		//System.out.println("tagpairstr= " + TagPairStr);
		newTagPairStr = TagPairStr.replaceFirst("(?<=>).*?(?=</)", value);
		//System.out.println("newtagpair: " + newTagPairStr);
		
		newXmlBlock = xmlBlockSb.replace(startIndex, endIndex, newTagPairStr);
		//System.out.println("newxml: " + newXmlBlock);
		return newXmlBlock.toString();
	}
	
	
	/**
	 * 
	 * @param tag
	 * @param xmlBlock
	 * @return
	 */
	public static String findParentTag (String tag, String xmlBlock) {
		String parentTag = "";
		String tagPStr = "<" + tag + ">[^<>].*?</" + tag + ">";
		Pattern tagP = Pattern.compile(tagPStr);
		Matcher tagM = tagP.matcher(xmlBlock);
		int tagIndex;
		String firstHalf;
		if(tagM.find()) {
			tagIndex = tagM.start();
			firstHalf = xmlBlock.substring(0, tagIndex);
			//
			
			String parentTagPStr = "(?<=<)[^</]+?(?=>$)";
			Pattern parentTagP = Pattern.compile(parentTagPStr);
			String lastTagPStr = "(?<=</)\\w+?(?=>$)|(?=/>$)";
			Pattern lastTagP = Pattern.compile(lastTagPStr);
			
			do {
				Matcher parentTagM = parentTagP.matcher(firstHalf);
				if(parentTagM.find()) {
					parentTag = parentTagM.group();
					break;
				}
				else {
					Matcher lastTagM = lastTagP.matcher(firstHalf);
					if(lastTagM.find()) {
						String lastTag = lastTagM.group();
						String lastPairPStr = "<" + lastTag + ">.*?</" + lastTag + ">$";
						Pattern lastPairP = Pattern.compile(lastPairPStr);
						Matcher lastPairM = lastPairP.matcher(firstHalf);
						if(lastPairM.find()) {
							int lastPairIndex = lastPairM.start();
							firstHalf = firstHalf.substring(0, lastPairIndex);
							System.out.println(firstHalf);
						}
						
					}
				}
			}
			while(!firstHalf.isEmpty());
			
		}
				
		return parentTag;
	}
	
	
	/**
	 * Combine the formated (multi-lines, indented) into the same line, remove the <?xml...>tags
	 * @param fileName
	 * @return the unformated XML string
	 */
	public static String unFormatXmlInFile (String fileName) {
		String contents = "";
		String fullFilePath = PATH_RestCallXML + "\\" + fileName;
		//<?xml version="1.0" encoding="UTF-8"?>
		String xmlPartPStr = "<\\?xml.+?>";
		Pattern xmlPartP = Pattern.compile(xmlPartPStr);
		
		//BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(MainEntry.ConfigFilePath), "UTF-8"));
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fullFilePath),"UTF-8"));
			String s = null;
			while ((s = br.readLine())!=null) {
				Matcher xmlPartM = xmlPartP.matcher(s);
				if(!xmlPartM.find()) {
					contents += s.trim();
				}
				 
			}
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		
		
		System.out.println(contents);
		return contents;
		
	}

	
	/**
	 * append string to the end tag of original content. eg. end tag is </value>, append string "test".
	 * The final result should be "test</value>"
	 * @param appendString
	 * @param origianlContents
	 * @param endTag
	 * @return
	 * @author Fei
	 */
	public static String appendToEnd(String appendString, String origianlContents, String endTag)
	{
		String finalContents = origianlContents;
		String endTagString = "</" + endTag + ">";
		StringBuffer sb = new StringBuffer(finalContents);
		if(finalContents.contains(endTagString))
		{
			int index = finalContents.indexOf(endTagString);
			sb.insert(index, appendString);
			finalContents = sb.toString();
		}
		return finalContents;
	}
		
	/**
	 * Read the whole Xml file as a single String
	 * @param xmlFileName file name only (which under the src/xml/)
	 * @return the string with the xml file contents
	 * @throws IOException
	 * Author: Bo Cao Jan 20, 2015
	 */
	public static String readXmlFileIntoString (String xmlFileName) throws IOException {
		String contents = "";
		String fullFilePath = PATH_RestCallXML + "\\" + xmlFileName; 
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fullFilePath),"UTF-8"));
		String s = null;
		while ((s = br.readLine())!=null) {
				contents += s.trim();
		}
		
		br.close();
		
		return contents;
	}
	
	
	/**
	 * Remove the block of the specified tag in the xml contents (for more than one blocks with same tagname, remove only the first block).
	 * @param tagName
	 * @return the remaining xml contents without the block.
	 * Author: Bo Cao jan 20, 2015
	 */
	public static String removeBlockInXMLString (String tagName, String xmlContents) {
		String results = "";
		String blockPStr = "<" + tagName + ">.*?</" + tagName + ">";
		Pattern blockP = Pattern.compile(blockPStr);
		
		Matcher blockM = blockP.matcher(xmlContents);
		
		if(blockM.find()) {
			String block = blockM.group();
			results = xmlContents.replace(block, "");
		}
		
		
		return results.trim();
	}
	
	
	/**
	 * Remove the specified block (one in the several block of same tagName), with the nearest subtag and value.
	 * @param tagName
	 * @param subTagName
	 * @param subTagValue
	 * @param xmlContents
	 * @return the remaining xml contents without the block
	 * author: Bo Cao on Jan 21, 2015
	 */
	public static String removeSpecialBlockInXMLString (String tagName, String subTagName, String subTagValue, String xmlContents) {
		String results = "";
		String blockPStr = "<" + tagName + "><" + subTagName + ">" + subTagValue + "</" + subTagName + ">.*?</" + tagName + ">";
		Pattern blockP = Pattern.compile(blockPStr);
		Matcher blockM = blockP.matcher(xmlContents);
		
		if(blockM.find()) {
			String block = blockM.group();
			results = xmlContents.replace(block, "");
		}
		
		return results.trim();
	}
	
	
	/**
	 * Insert the String contents before the Start Tag
	 * @param appendString
	 * @param origianlContents
	 * @param startTag
	 * @return
	 */
	public static String insertStringBeforeStartTag(String appendString, String origianlContents, String startTag)
	{
		String finalContents = origianlContents;
		String endTagString = "<" + startTag + ">";
		StringBuffer sb = new StringBuffer(finalContents);
		if(finalContents.contains(endTagString))
		{
			int index = finalContents.indexOf(endTagString);
			sb.insert(index, appendString);
			finalContents = sb.toString();
		}
		return finalContents;
	}
	
	
	/**
	 * Modify the tag property's value. e.g <section name="TestSection">. tag is section, property is name, modify the value "TestSection"
	 * @param xmlContents
	 * @param tag
	 * @param property
	 * @param value
	 * @return
	 * @author Fei
	 */
	public static String modifyTagPropertyValue(String xmlContents, String tag, String property,String value)
	{
		if(xmlContents != null && xmlContents.contains(tag))
		{
			String temp = null;
			Pattern p = Pattern.compile("<" + tag + ".*?" + ">");
			Matcher m = p.matcher(xmlContents);
			if(m.find())
			{
				temp = m.group();
			}
			
			if(temp.contains(property))
			{
				Pattern p2 = Pattern.compile("(?<=" + property + "=\").*?(?=\")");
				Matcher m2 = p2.matcher(temp);
				String temp2 = null;
				if(m2.find())
				{
					temp2 = temp.replaceAll("(?<=" + property + "=\").*?(?=\")", value);
				}
				xmlContents = xmlContents.replace(temp, temp2);
			}
		}
		return xmlContents;		
	}
	
	/**
	 * Find the specific Tag's property value. e.g <section id="1007" name="figo" generationNumber="1422259158876" timestamp="1422259158876"/>.
	 * The tag is <section ....>, relateProperty is name, relatePropertyValue is figo , targetProperty is id. This will return id's value 1007
	 * @param xmlContents
	 * @param tag
	 * @param relateProperty
	 * @param relatePropertyValue
	 * @param targetProperty
	 * @return
	 */
	public static String findSpecificTagPropertyValue(String xmlContents, String tag, String relateProperty, String relatePropertyValue,String targetProperty)
	{
		String result = null;
		if(xmlContents != null && xmlContents.contains(tag))
		{
			String temp = null;
			String targetString = null;
			Pattern p = Pattern.compile("<" + tag + ".*?" + ">");
			Matcher m = p.matcher(xmlContents);
			while(m.find())
			{
				temp = m.group();
				String temp2 = relateProperty + "=\"" + relatePropertyValue + "\"";
				if(temp.contains(temp2))
				{
					targetString = temp;
					break;
				}
			}
			if(targetString.contains(targetProperty))
			{
				Pattern p2 = Pattern.compile("(?<=" + targetProperty + "=\").*?(?=\")");
				Matcher m2 = p2.matcher(temp);
				if(m2.find())
				{
					result = m2.group();
				}
			}
			else {
				System.out.println("The specific property not found!");
			}
		}
		return result;
	}
	
	/**
	 * Get the strings before specific tag. e.g sample string <rule id="1012" disabled="true" logged="true"><action>allow</action><name>Matru-3</name>
	 * tag1 is rule, tag2 is name, tag2Value is Matru-3, this method will return string 
	 * @param xmlContents
	 * @param tag1
	 * @param tag2
	 * @param tag2Value
	 * @return
	 */
	public static String getStringsBeforeSpecificTag(String xmlContents,String tag1,String tag2,String tag2Value)
	{
		String result = null;
		if( xmlContents!= null && xmlContents.contains(tag1) && xmlContents.contains(tag2))
		{
			//Pattern p = Pattern.compile("<" + tag1 + "[^/]*><" + tag2 + ">" + tag2Value + "</" + tag2 + ">");
			Pattern p = Pattern.compile("<" + tag1 + "[^/]*>.{0,64}<" + tag2 + ">" + tag2Value + "</" + tag2 + ">");
			Matcher m = p.matcher(xmlContents);
			if(m.find())
			{
				result = m.group();
			}
		}
		//System.out.println(result);
		return result;	
	}
	
	/**
	 * Get the property value in tag string. e.g <rule id="1012" disabled="true" logged="true"><name>Matru-3</name>.
	 * tag is rule, property is id, return the property id value "1012". 
	 * @param xmlContents
	 * @param tag
	 * @param property
	 * @return
	 */
	public static String getSpecificPropertyValueInTagString(String xmlContents, String tag, String property)
	{
		String result = null;
		if(xmlContents!= null && xmlContents.contains(tag) && xmlContents.contains(property))
		{
			Pattern p = Pattern.compile("<" + tag + ".*?" + ">");
			Matcher m = p.matcher(xmlContents);
			String temp = null;
			if(m.find())
			{
				temp = m.group();
				Pattern p2 = Pattern.compile("(?<=" + property + "=\").*?(?=\")");
				Matcher m2 = p2.matcher(temp);
				if(m2.find())
				{
					result = m2.group();
				}
			}
		}
		return result;
	}
	/**       
	 * xml String to Document
	 *  @return Document       
	 *  @param s xml string  
	 */      
	public static Document string2Document(String s){         
		Document doc = null;        
		try{              
			doc = DocumentHelper.parseText(s);        
		}catch(Exception ex){                          
			ex.printStackTrace();        
		}         
		return doc;     
	} 
	/** 
	 * document to xml file and update original xml file
	 * @return true: successful  flase:fail 
	 * @param filename 
	 * @param document 
	 */ 
	public static boolean doc2XmlFile(Document document,String fullFilePath) { 
		boolean flag = true; 
	    try { 
	    	/**/ 	        
	        OutputFormat format = OutputFormat.createPrettyPrint(); 
	        format.setEncoding("UTF-8"); 
	        XMLWriter writer = new XMLWriter(new FileWriter(new File(fullFilePath)),format); 
	        writer.write(document); 
	        writer.close();             
	        }catch(Exception ex) { 
	            flag = false; 
	            ex.printStackTrace(); 
	        } 
	        return flag;       
	   }
	/**
	 *  Check the given string whether equal target tag's value which exist in the xmlContents. e.g.
	 *  xmlContents: "<List>luis</List><name>figo</name>" targetTag: <name> , targetTag's value: figo. 
	 *  When the given String is "figo", the result is true.
	 * @param xmlContents 
	 * @param tag
	 * @param tagValues
	 * @return
	 */
	public static boolean checkGivenStringEqualTagValue(String xmlContents, String targetTag, String givenString)
	{
		boolean flag = false;
		
		ArrayList<String> tagValues = new ArrayList<String>();
		
		Pattern p = Pattern.compile("(?<=<" + targetTag + ">).*?(?=</" + targetTag + ">)");
		Matcher m = p.matcher(xmlContents);
		while(m.find())
		{
			tagValues.add(m.group());
		}
		
		for(int i=0; i< tagValues.size(); i++)
		{
			if(tagValues.get(i).equals(givenString)){
				flag = true;
				break;
			}
		}
		return flag;
		
	}
	public static Boolean chekFileOrString (String filePath) {
		Boolean isFile;
		File f = new File(filePath);
		if(f.exists()) {
			isFile = true;
		}
		else 
			isFile = false;
		
		return isFile;
	}
	public static Boolean checkConfigVsGetXmlValue (String...args) {
		
		String xmlResults = args[0];
		
		
		for (int i=1; i<args.length; i++) {
			String xmlValue = readValueFromXmlString(xmlResults, args[++i]);
			if(xmlValue == null || xmlValue == "") {
					return false;				
			}
		}
		
		return true;
		
	}
}


