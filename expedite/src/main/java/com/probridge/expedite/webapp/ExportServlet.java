package com.probridge.expedite.webapp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.orbeon.oxf.xml.dom4j.Dom4jUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportServlet extends HttpServlet {

	private static final long serialVersionUID = -5761849563881652897L;
	private static String EXPORT_ERROR = "/pages/export.jsp";
	private static final Logger logger = LoggerFactory.getLogger(ExportServlet.class);

	public ExportServlet() {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String appName = req.getParameter("appName");
		String formName = req.getParameter("formName");

		String searchString = "<search xmlns=\"\"><query/><page-size>10000</page-size><page-number>1</page-number><lang /></search>";
		StringEntity requestBody = new StringEntity(searchString);
		//
		ArrayList<Document> dataList = new ArrayList<Document>();
		Document formDefinition = null;
		LinkedHashMap<String, String> fieldMapping = new LinkedHashMap<String, String>();
		HashSet<String> outputControl = new HashSet<String>();
		ArrayList<HashMap<String, String>> outputDataList = new ArrayList<HashMap<String, String>>();
		//
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpPost httpPostRequest = new HttpPost("http://localhost:8080/expedite/fr/service/persistence/search/"
					+ appName + "/" + formName);
			httpPostRequest.setHeader("Cookie", "JSESSIONID=" + req.getSession().getId());
			httpPostRequest.setHeader("Content-Type", "application/xml");
			httpPostRequest.setEntity(requestBody);
			//
			HttpResponse httpResponse = httpClient.execute(httpPostRequest);
			//
			HttpEntity entity = httpResponse.getEntity();
			//
			Document document = null;
			if (entity != null) {
				InputStream inputStream = entity.getContent();
				BufferedInputStream bis = new BufferedInputStream(inputStream);
				document = Dom4jUtils.readDom4j(bis);
				bis.close();
			}
			//
			Element root = document.getRootElement();
			List<Element> docs = root.elements();
			//
			for (Element eachFormData : docs) {
				if ("N".equalsIgnoreCase(eachFormData.attributeValue("draft"))) {
					String documentId = eachFormData.attributeValue("name");
					HttpGet httpGetRequest1 = new HttpGet("http://localhost:8080/expedite/fr/service/persistence/crud/"
							+ appName + "/" + formName + "/data/" + documentId + "/data.xml");
					httpGetRequest1.setHeader("Cookie", "JSESSIONID=" + req.getSession().getId());
					HttpResponse httpResponse1 = httpClient.execute(httpGetRequest1);
					HttpEntity entity1 = httpResponse1.getEntity();
					if (entity1 != null) {
						InputStream ins = entity1.getContent();
						try {
							dataList.add(Dom4jUtils.readDom4j(ins));
						} finally {
							ins.close();
						}
					}
				}
			}
			//
			HttpGet httpGetRequest2 = new HttpGet("http://localhost:8080/expedite/fr/service/persistence/crud/"
					+ appName + "/" + formName + "/form/form.xhtml");
			httpGetRequest2.setHeader("Cookie", "JSESSIONID=" + req.getSession().getId());
			HttpResponse httpResponse2 = httpClient.execute(httpGetRequest2);
			HttpEntity entity2 = httpResponse2.getEntity();
			if (entity2 != null) {
				InputStream ins = entity2.getContent();
				try {
					formDefinition = Dom4jUtils.readDom4j(ins);
				} finally {
					ins.close();
				}
			}
			//
			String formTitle = formDefinition.selectSingleNode("//metadata/title").getText();
			String formDescription = formDefinition.selectSingleNode("//metadata/description").getText();
			//
			Node resourceNode = formDefinition
					.selectSingleNode("//xf:instance[@id='fr-form-resources']/resources/resource");
			List<Element> resourceElements = ((Element) resourceNode).elements();
			for (Element eachControl : resourceElements) {
				String controlName = eachControl.getName();
				String controlLabel = eachControl.element("label").getText();
				fieldMapping.put(controlName, controlLabel);
			}
			//
			for (Document eachFormDocument : dataList) {
				HashMap<String, String> dataMap = new HashMap<String, String>();
				treeWalk(eachFormDocument, dataMap);
				outputControl.addAll(dataMap.keySet());
				outputDataList.add(dataMap);
			}
			//
			String fName = appName + "-" + formName + ".xls";
			resp.setHeader("Content-Disposition", "attachment; filename=\"" + fName + "\"; filename*=UTF-8''"
					+ rfc5987_encode(fName));

			WritableWorkbook workbook = Workbook.createWorkbook(resp.getOutputStream());
			//
			WritableFont fmtFont = new WritableFont(WritableFont.ARIAL, 10);
			WritableFont fmtFontBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
			//
			WritableCellFormat fmtTitleCell = new WritableCellFormat(fmtFont);
			fmtTitleCell.setAlignment(Alignment.CENTRE);

			WritableCellFormat fmtHeaderCell = new WritableCellFormat(fmtFontBold);
			fmtHeaderCell.setAlignment(Alignment.CENTRE);

			WritableCellFormat fmtCell = new WritableCellFormat(fmtFont);
			//
			WritableSheet worksheet = workbook.createSheet(formTitle, 0);
			worksheet.mergeCells(0, 0, 7, 0);
			//
			for (int i = 0; i < Math.max(outputControl.size() + 1, 8); i++) {
				CellView cellView = new CellView();
				cellView.setSize(20 * 256);
				worksheet.setColumnView(i, cellView);
			}
			//
			int currentRow = 0, currentCol = 0;
			worksheet.getSettings().setHorizontalFreeze(0);
			worksheet.getSettings().setVerticalFreeze(2);
			//
			String printDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			// Print title
			String printTitle = formTitle + " (" + appName + "-" + formName + ") - " + formDescription + " "
					+ printDate + " (C)ProBridge Expedite";
			worksheet.addCell(new Label(currentCol++, currentRow, printTitle, fmtTitleCell));
			currentRow++;
			currentCol = 0;
			// Print header
			for (Entry<String, String> eachCol : fieldMapping.entrySet()) {
				if (outputControl.contains(eachCol.getKey()))
					worksheet.addCell(new Label(currentCol++, currentRow, (String) eachCol.getValue(), fmtHeaderCell));
			}
			currentRow++;
			currentCol = 0;
			// Print data
			for (HashMap<String, String> eachDocumentMap : outputDataList) {
				for (Entry<String, String> eachCol : fieldMapping.entrySet()) {
					if (outputControl.contains(eachCol.getKey())) {
						worksheet.addCell(new Label(currentCol++, currentRow, (String) eachDocumentMap.get(eachCol
								.getKey()), fmtCell));
					}
				}
				currentRow++;
				currentCol = 0;
			}
			//
			workbook.write();
			workbook.close();
			resp.getOutputStream().close();
		} catch (Exception e) {
			logger.error("error processing export", e);
			req.setAttribute("error", e.getMessage());
			RequestDispatcher view = req.getRequestDispatcher(EXPORT_ERROR);
			view.forward(req, resp);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		RequestDispatcher view = req.getRequestDispatcher(EXPORT_ERROR);
		view.forward(req, resp);
	}

	private void treeWalk(Document document, HashMap<String, String> dataMap) {
		treeWalk(document.getRootElement(), dataMap);
	}

	private void treeWalk(Element element, HashMap<String, String> dataMap) {
		for (int i = 0, size = element.nodeCount(); i < size; i++) {
			Node node = element.node(i);
			if (node instanceof Element) {
				if (node.getText() != null && node.getText().length() > 0)
					dataMap.put(node.getName(), node.getText());
				treeWalk((Element) node, dataMap);
			} else {
				// skip non element node
			}
		}
	}

	private String rfc5987_encode(final String s) throws UnsupportedEncodingException {
		final byte[] s_bytes = s.getBytes("UTF-8");
		final int len = s_bytes.length;
		final StringBuilder sb = new StringBuilder(len << 1);
		final char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		final byte[] attr_char = { '!', '#', '$', '&', '+', '-', '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
				'U', 'V', 'W', 'X', 'Y', 'Z', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
				'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '|', '~' };
		for (int i = 0; i < len; ++i) {
			final byte b = s_bytes[i];
			if (Arrays.binarySearch(attr_char, b) >= 0)
				sb.append((char) b);
			else {
				sb.append('%');
				sb.append(digits[0x0f & (b >>> 4)]);
				sb.append(digits[b & 0x0f]);
			}
		}
		return sb.toString();
	}
}
