/**
 * Copyright (C) Altimetrik 2018. All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Altimetrik. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms and conditions
 * entered into with Altimetrik.
 */

package Surveys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * The Class ExcelUtil.
 * 
 * @author rchennuboina
 */
public class ExcelUtil {

	/** The data map. */
	public static HashMap<String, String> dataMap;
	/** The linked data map. */
	public static LinkedHashMap<String, String> linkedDataMap;

	private Map<String, Set<Integer>> invalidQtns = new HashMap();

	public ExcelUtil() {
		Set<Integer> invalidQtnsIds106 = new HashSet<>();
		invalidQtnsIds106.addAll(Arrays.asList(10374, 25, 11357, 11358, 11359, 11360, 14724, 5317, 6788));
		invalidQtns.put("106", invalidQtnsIds106);
		Set<Integer> invalidQtnsIds53 = new HashSet<>();
		invalidQtnsIds53.addAll(Arrays.asList(3572));
		invalidQtns.put("53", invalidQtnsIds53);
		
		/*Set<Integer> invalidQtnsIds224 = new HashSet<>();
		invalidQtnsIds224.addAll(Arrays.asList());
		invalidQtns.put("224", invalidQtnsIds224);*/
		
		Set<Integer> invalidQtnsIds138 = new HashSet<>();
		invalidQtnsIds138.addAll(Arrays.asList(10374, 25, 5317));
		invalidQtns.put("138", invalidQtnsIds138);
		Set<Integer> invalidQtnsIds38 = new HashSet<>();
		invalidQtnsIds38.addAll(Arrays.asList(5317, 25, 4060, 4081));
		invalidQtns.put("38", invalidQtnsIds38);

		Set<Integer> invalidQtnsIds36 = new HashSet<>();
		invalidQtnsIds36.addAll(Arrays.asList(5317, 25, 3572, 6098, 6099, 6188, 6189, 6190, 6191, 10165, 10167, 3418,
				7141, 7142, 7143, 7144, 8114));
		invalidQtns.put("36", invalidQtnsIds36);

	}

	/** The logger. */

	/**
	 * The method readDataAsKeyValue(String,String) reads the data from excel sheet
	 * and stores it in a HashMap.
	 * 
	 * @param filePath  The relative path where the excel file is stored.
	 * @param sheetName The name of the sheet for reading the data.
	 * @return A Hashmap containing the sheet's data as a key-value pair in random
	 *         order.
	 */
	public Map<String, Map<String, String>> readDataAsKeyValue(String filePath, String sheetName, String metadataFile,
			String surveyId) throws Exception {
		Map<String, Map<String, String>> srcDataMap = new HashMap<>();

		File file = new File(filePath);
		Workbook workBook = null;
		InputStream fip = null;
		if (file.exists()) {
			try {
				fip = new FileInputStream(file);
				workBook = new XSSFWorkbook(fip);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Sheet trivoxDataSheet = workBook.getSheet(sheetName);
			if (trivoxDataSheet != null) {
				// get QtnId -> QCode Map
				Map<Integer, String> qidToQCodeMap = getQtnIdToQCodeMap(metadataFile);

				// dataMap = new HashMap<String, String>();
				for (int rowIndex = trivoxDataSheet.getFirstRowNum() + 1; rowIndex <= trivoxDataSheet
						.getLastRowNum(); rowIndex++) {
					Row dataRow = trivoxDataSheet.getRow(rowIndex);

					Integer dataQId = Integer.parseInt(dataRow.getCell(10).toString());

					if (invalidQtns.get(surveyId) != null) {
						Set<Integer> invalidIds = invalidQtns.get(surveyId);
						if (invalidIds.contains(dataQId)) {
							continue;
						}
					}

					Cell qTypeCell = dataRow.getCell(11);
					String qType = qTypeCell == null ? "" : dataRow.getCell(11).getStringCellValue();
					if (qType.equalsIgnoreCase("mmc") || qType.equalsIgnoreCase("continue")
							|| qType.equalsIgnoreCase("myn") || qType.equalsIgnoreCase("dynamic_symptom")
							|| qType.equalsIgnoreCase("medication")) {
						continue;
					}

					String screeningId = getCellValAsString(dataRow.getCell(0));
					String subjectId = dataRow.getCell(4).getStringCellValue();
					String reponseUserId = dataRow.getCell(1).getStringCellValue();

					String token = screeningId + "_" + subjectId + "_" + reponseUserId;

					Map<String, String> qidResponseMap = srcDataMap.get(token);
					if (srcDataMap.get(token) == null) {
						qidResponseMap = new HashMap<>();
						srcDataMap.put(token, qidResponseMap);
					}

					if (dataRow.getCell(13) == null || dataRow.getCell(12) == null) {
						//System.out.println("Token :" + token + "  QID : " + dataQId);
					}

					String dataValueText = StringUtils.isEmpty(qType) ? dataRow.getCell(13).toString().trim()
							: dataRow.getCell(12).toString().trim();
					boolean numeric = true;
					try {
						dataValueText=String.valueOf(Float.parseFloat(dataValueText));	
					}catch(NumberFormatException e) {
			            numeric = false;
			        }
					
					if (qidToQCodeMap.get(dataQId) == null) {
						System.out.println("OMG QCode is null for " + dataQId);
						System.exit(0);
					} else {
						if (!"Skip this question".equalsIgnoreCase(dataValueText)) {
							qidResponseMap.put(qidToQCodeMap.get(dataQId), dataValueText);
						}
					}
				}
			} // Condition for Sheet.
			else {
				return null;
			}

			try {
				workBook.close();
				fip.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} // condition for file exists.
		else {
			System.out.println("FILE DOESN'T EXISTS IN THE GIVEN PATH::{}" + filePath);
			return null;
		}
		return srcDataMap;
	}

	private Map<Integer, String> getQtnIdToQCodeMap(String fileName) throws Exception {
		Map<Integer, String> map = new HashMap();
		File metadataFile = new File(fileName);
		try (Workbook workbook = new XSSFWorkbook(metadataFile)) {
			// Get first/desired sheet from the workbook
			Sheet sheet = workbook.getSheetAt(0);

			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next(); // skip title row

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				Integer qId = getCellValAsInteger(row.getCell(3));
				String qCode = row.getCell(2).getStringCellValue();

				if (qId == null) {
					System.out.println("QID is missing for " + qCode);
					continue;
				}

				map.put(qId, qCode);
			}
		}

		return map;
	}

	private Integer getCellValAsInteger(Cell cell) {
		if (cell == null)
			return null;

		if (cell.getCellTypeEnum() == CellType.NUMERIC) {
			return ((int) cell.getNumericCellValue());
		}

		if (cell.getStringCellValue() != null) {
			String idStr = null;
			if (cell.getStringCellValue().indexOf("-") > -1) {
				idStr = cell.getStringCellValue().trim().split("-")[1];
			} else {
				idStr = cell.getStringCellValue().trim();
			}
			return Integer.parseInt(idStr);
		}

		return null;
	}

	/**
	 * This Method reads the data from excel sheet and stores it in a LinkedHashMap
	 * for order retrieval.
	 * 
	 * @param filePath  The relative path where the excel file is stored.
	 * @param sheetName The name of the sheet for reading the data
	 * @return A LinkedHashmap containing the sheet's data as key-value pair and
	 *         stored in the same order as read from sheet.
	 */
	public static LinkedHashMap<String, String> readDataAsOrderedKeyValuePair(String filePath, String sheetName) {
		File trivoxFile = new File(filePath);
		Workbook trivoxWorkBook = null;
		InputStream fip = null;
		if (trivoxFile.exists()) {
			try {
				fip = new FileInputStream(trivoxFile);
				trivoxWorkBook = new XSSFWorkbook(fip);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Sheet trivoxSheet = trivoxWorkBook.getSheet(sheetName);
			if (trivoxSheet != null) {
				Map trivoxDataMap = new LinkedHashMap<String, String>();
				for (int rowIndex = trivoxSheet.getFirstRowNum(); rowIndex <= trivoxSheet.getLastRowNum(); rowIndex++) {
					Row trivoxRow = trivoxSheet.getRow(rowIndex);
					for (int cellIndex = trivoxRow.getFirstCellNum(); cellIndex < trivoxRow.getLastCellNum();) {
						String key = trivoxRow.getCell(cellIndex).toString();
						String value = trivoxRow.getCell(cellIndex + 1).toString();
						trivoxDataMap.put(key, value);
						break;
					}
				}
			} else {
				System.out.println("INVALID SHEET NAME::: {} " + sheetName);
				return null;
			}
			try {
				trivoxWorkBook.close();
				fip.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("FILE DOESN'T EXISTS IN THE GIVEN PATH:::{}" + filePath);
			return null;
		}
		return linkedDataMap;
	}

	/**
	 * The Method readDataAsList(String,String) reads the data from excel sheet and
	 * stores it in a List.
	 * 
	 * @parate m filePath The relative path where the excel file is stored.
	 * @param sheetName The name of the sheet for reading the data.
	 * @return A List containing the sheet's data.
	 */
	/*
	 * public static List<String> readDataAsList(String filePath, String sheetName)
	 * { file = new File(filePath); if (file.exists()) { try { fip = new
	 * FileInputStream(file); workBook = new XSSFWorkbook(fip); } catch (Exception
	 * e) { e.printStackTrace(); } sheet = workBook.getSheet(sheetName); if (sheet
	 * != null) { dataList = new ArrayList<String>(); for (int rowIndex =
	 * sheet.getFirstRowNum() + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
	 * row = sheet.getRow(rowIndex); for (int cellIndex = row.getFirstCellNum();
	 * cellIndex < row.getLastCellNum(); cellIndex++) { String value =
	 * row.getCell(cellIndex).toString(); dataList.add(value); } } } // Sheet exists
	 * condition else { System.out.println("INVALID SHEET NAME::: {} " + sheetName);
	 * return null; } try { workBook.close(); fip.close(); } catch (Exception e) {
	 * e.printStackTrace(); } } // File exists condition else {
	 * System.out.println("FILE DOESN'T EXISTS IN THE GIVEN PATH:::{}" + filePath);
	 * return null; } return dataList; }
	 */

	public Object[][] getTestData(String fpath, String sheetName) throws InvalidFormatException, IOException {

		// Specify the path of file
		File srcFile = new File(fpath);

		// load file
		FileInputStream fis = new FileInputStream(srcFile);
		// Load workbook
		XSSFWorkbook wb = new XSSFWorkbook(fis);

		// Load sheet- Here we are loading first sheetonly
		XSSFSheet sh1 = wb.getSheet(sheetName);
		// two d array declaration
		Object[][] data = new Object[sh1.getLastRowNum()][sh1.getRow(0).getLastCellNum()];
		// System.out.println(sheet.getLastRowNum() + "--------" +
		// sheet.getRow(0).getLastCellNum());
		for (int i = 0; i < sh1.getLastRowNum(); i++) {
			for (int k = 0; k < sh1.getRow(0).getLastCellNum(); k++) {
				data[i][k] = getCellValAsString(sh1.getRow(i + 1).getCell(k));
				// System.out.println(data[i][k]);
			}
		}
		return data;
	}

	private String getCellValAsString(Cell cell) {
		if (cell == null)
			return null;

		if (cell.getCellTypeEnum() == CellType.NUMERIC) {
			return ((int) cell.getNumericCellValue()) + "";
		}

		return cell.getStringCellValue();
	}

	// Runner Method for testing.
	/*
	 * public static void main(String[] a) throws IOException{
	 * readDataAsKeyValue("D:\\CarePlanBuilder_TemplateV2.xlsx", "ScheduleEvents");
	 * }
	 */

}
