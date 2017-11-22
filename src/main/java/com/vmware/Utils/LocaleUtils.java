package com.vmware.Utils;

public class LocaleUtils {
	private String inputLanguage;
	
	public LocaleUtils(String inputLanguage) {
		this.inputLanguage = inputLanguage;
	}
	
	public String getLocale(){
		String locale = "";
		switch(inputLanguage){
			case "DE":
				locale = "de_de";
				break;
			case "FR":
				locale = "fr_fr";
				break;
			case "CN":
				locale = "zh_cn";
				break;
			case "JA":
				locale = "ja_jp";
				break;
			case "KO":
				locale = "ko_kr";
				break;
			default:
				locale = "en_us";
		}
		return locale;
	}
	
	public String getLanguage(){
		String lanuage = "";
		switch(inputLanguage){
			case "DE":
				lanuage = "German";
				break;
			case "FR":
				lanuage = "French";
				break;
			case "CN":
				lanuage = "Chinese Simplify";
				break;
			case "JA":
				lanuage = "Japanese";
				break;
			case "KO":
				lanuage = "Korean";
				break;
			default:
				lanuage = "English";
		}
		return lanuage;
	}
	
}
