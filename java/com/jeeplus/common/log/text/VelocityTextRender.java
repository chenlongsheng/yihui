package com.jeeplus.common.log.text;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class VelocityTextRender implements TextRender {

	public void render(Reader reader, Map<String, Object> context, Writer writer) {
		try {
			Velocity.init();
			VelocityContext velocityContext = new VelocityContext();
			for(Map.Entry<String, Object> entry : context.entrySet()) {
				velocityContext.put(entry.getKey(), entry.getValue());
			}
			Velocity.evaluate(velocityContext, writer, VelocityTextRender.class.getName(), reader);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String render(String templateString, Map<String, Object> context) {
		StringWriter writer = new StringWriter();
		render(new StringReader(templateString), context, writer);
		return writer.toString();
	}

}
