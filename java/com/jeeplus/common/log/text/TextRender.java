package com.jeeplus.common.log.text;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

public interface TextRender {
	
	void render(Reader reader, Map<String, Object> context, Writer writer);
	
	String render(String templateString, Map<String, Object> context);

}
