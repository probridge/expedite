package com.probridge.expedite.webapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ServletRequestWrapper extends
		javax.servlet.http.HttpServletRequestWrapper {

	private Map headerMap;

	public ServletRequestWrapper(HttpServletRequest request) {
		super(request);
		headerMap = new HashMap();
	}

	public void addHeader(String name, String value) {
		headerMap.put(name, new String(value));
	}

	public Enumeration getHeaderNames() {
		HttpServletRequest request = (HttpServletRequest) getRequest();
		List list = new ArrayList<String>();
		for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
			list.add(e.nextElement().toString());
		}

		for (Iterator i = headerMap.keySet().iterator(); i.hasNext();) {
			list.add(i.next());
		}
		return Collections.enumeration(list);
	}

	public String getHeader(String name) {
		Object value;
		if ((value = headerMap.get("" + name)) != null)
			return value.toString();
		else
			return ((HttpServletRequest) getRequest()).getHeader(name);
	}

	@Override
	public Enumeration getHeaders(String name) {
		Enumeration e = super.getHeaders(name);
		if (e != null && e.hasMoreElements()) {
			return e;
		} else {
			List l = new ArrayList();
			if (headerMap.get(name) != null) {
				l.add(headerMap.get(name));
			}
			return Collections.enumeration(l);
		}
	}
}