package com.URLChecker;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.SystemOutLogger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CSVRead {

	// String fileName = System.getProperty("fileName") ;
	String fileName = "TestURL.csv";
	String filepath = System.getProperty("user.dir")+File.separator+fileName;

	CSVRead() throws IOException {

	}

	// Returns a list of URL Values
	public List<String> getURLVals() throws IOException {
		CSVParser parser = new CSVParser(new FileReader(filepath), CSVFormat.DEFAULT);
		List<CSVRecord> list = parser.getRecords();
		List<String> urlLst = new ArrayList<>();
		int cnt = 0;
		for (CSVRecord record : list) {
			if (cnt > 0)
				urlLst.add(record.get(0));
			cnt++;
		}
		parser.close();
		return urlLst;
	}

	// Return a Reg EXP from Excel
	public String getRegExp() throws IOException {
		CSVParser parser = new CSVParser(new FileReader(filepath), CSVFormat.DEFAULT);
		List<CSVRecord> list = parser.getRecords();
		return list.get(0).get(4).split("=")[1].trim();
	}

	// Write Data in CSV AS true and false
	public void setFlag(int rowNo, int col, String value) throws IOException {

		CSVReader reader = new CSVReader(new FileReader(filepath));
		List<String[]> csvBody = reader.readAll();
		csvBody.get(rowNo)[col] = value;
		reader.close();
		CSVWriter writer = new CSVWriter(new FileWriter(filepath), ',');
		writer.writeAll(csvBody);
		writer.flush();
		writer.close();
	}

	// Check Reg Exp Pattern
	public boolean checkFlag(String pattern, String text) {
		return Pattern.compile(pattern).matcher(text).matches();
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		CSVRead read = new CSVRead();
		System.out.println(read.checkFlag(read.getRegExp(), "Symptoms of McArdle’s Disease | McArdlesDisease.org"));
		System.out.println("Symptoms of McArdle’s Disease | McArdlesDisease.org".length());
	}

}
