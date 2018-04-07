//package gp;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.UpdateSpreadsheetPropertiesRequest;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties ;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.DeleteSheetRequest;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Sheet ;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.BatchUpdate ;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest ;
import com.google.api.services.sheets.v4.model.RowData ;
import com.google.api.services.sheets.v4.model.PivotTable ;
import com.google.api.services.sheets.v4.model.PivotGroup ;
import com.google.api.services.sheets.v4.model.PivotValue ;
import com.google.api.services.sheets.v4.model.GridCoordinate ;


import com.google.api.services.sheets.v4.model.RepeatCellRequest ;
import com.google.api.services.sheets.v4.model.GridRange ;
import com.google.api.services.sheets.v4.model.CellData ;
import com.google.api.services.sheets.v4.model.CellFormat ;
import com.google.api.services.sheets.v4.model.NumberFormat ;
import com.google.api.services.sheets.v4.model.ExtendedValue ;

import com.google.api.services.drive.DriveScopes ;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.List ;
import com.google.api.services.drive.model.FileList ;
import com.google.api.services.drive.model.File ;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.ArrayList;
//import java.util.List;
import java.util.Iterator;

import java.io.BufferedReader;
//import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

//import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

//import javax.json.Json;

public class gpSheetsAPI {
	final java.util.logging.Logger buggyLogger = java.util.logging.Logger.getLogger(FileDataStoreFactory.class.getName());

	/** Application name. */
	private final String APPLICATION_NAME = "gpSheets";

	/** Directory to store user credentials for this application. */
	private final java.io.File DATA_STORE_DIR = new java.io.File(
		System.getProperty("user.home"), ".credentials/gpSheets");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private HttpTransport HTTP_TRANSPORT;

	/** Global instance of the scopes required by this quickstart.
	*
	* If modifying these scopes, delete your previously saved credentials
	* at ~/.credentials/gpSheets
	*/
	//SPREADSHEETS_READONLY | SPREADSHEETS | DRIVE_READONLY  | DRIVE_FILE  | DRIVE
	private final String[] arrSCOPES = { SheetsScopes.SPREADSHEETS, DriveScopes.DRIVE_METADATA_READONLY };
	private final java.util.List<String> SCOPES = Arrays.asList(arrSCOPES);

	/**
	* Creates an authorized Credential object.
	* @return an authorized Credential object.
	* @throws IOException
	*/
	private Credential authorize() throws IOException {
		//System.out.println("Credential::authorize()");

		// Load client secrets.
		//System.out.println("Load client secrets");
		InputStream in = gpSheets2.class.getResourceAsStream("client_secret.json");
		if (in == null) System.out.println("client_secret.json: Key not found. InputStream::in: NULL");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		buggyLogger.setLevel(java.util.logging.Level.SEVERE);

		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(DATA_STORE_FACTORY)
				.setAccessType("offline")
				.build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		//System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	/**
	* Build and return an authorized Sheets API client service.
	* @return an authorized Sheets API client service
	* @throws IOException
	*/
	private Sheets getSheetsService() throws IOException {
		Credential credential = authorize();
		return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}

	/**
	* Build and return an authorized Drive API client service.
	* @return an authorized Drive API client service
	* @throws IOException
	*/
	private Drive getDriveService() throws IOException {
		//credentials = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES));
		Credential credential = authorize();
		return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}

	private Sheets createSheetsService() throws IOException, GeneralSecurityException {
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

		// TODO: Change placeholder below to generate authentication credentials. See
		// https://developers.google.com/sheets/quickstart/java#step_3_set_up_the_sample
		//
		// Authorize using one of the following scopes:
		//	"https://www.googleapis.com/auth/drive"
		//	"https://www.googleapis.com/auth/drive.file"
		//	"https://www.googleapis.com/auth/spreadsheets"
		GoogleCredential credential = null;

		return new Sheets.Builder(httpTransport, jsonFactory, credential)
			.setApplicationName(APPLICATION_NAME)
			.build();
	  }

	public void addPivot()
	{
		try {
			// Build a new authorized API client service.
			Sheets service = getSheetsService();

			// The ID of the spreadsheet to update.
			String spreadsheetId = "1ACO-DitYe4g9oxa8QcSNjUfnhiWcSvvvXO-5YCWidQc";
			final int sheetIDSheet1 = 0 ;
			final int sheetIDDefault = 1417346082 ;

			String sortOrder = "ASCENDING" ;
			/*
			java.util.List<PivotGroup> pivotColumnsList = new ArrayList<>();
			pivotColumnsList.add(new PivotGroup()
				.setSourceColumnOffset(8)
				.setSortOrder(sortOrder)
				.setShowTotals(false)
			) ;
			*/

			// y-axis / rows of pivot
			java.util.List<PivotGroup> pivotRowsList = new ArrayList<>();
			pivotRowsList.add(new PivotGroup()
				.setSourceColumnOffset(0)
				.setSortOrder(sortOrder)
				.setShowTotals(true)
			) ;

			String summarizeFunction = "SUM" ;
			// x-axis / columns of pivot
			java.util.List<PivotValue> pivotValuesList = new ArrayList<>();
			pivotValuesList.add(new PivotValue()
				.setSummarizeFunction(summarizeFunction)
				.setSourceColumnOffset(7)
			) ;
			pivotValuesList.add(new PivotValue()
				.setSummarizeFunction(summarizeFunction)
				.setSourceColumnOffset(8)
			) ;

			// data source of pivot
			int sheetID = sheetIDSheet1 ;
			String valueLayout = "HORIZONTAL";
			PivotTable aPivotTable = new PivotTable() ;
			aPivotTable.setSource(new GridRange()
				.setSheetId(sheetID)
				.setStartRowIndex(1)
				.setStartColumnIndex(1)
				.setEndRowIndex(51)
				.setEndColumnIndex(10)
			);
			//aPivotTable.setColumns(pivotColumnsList) ;
			aPivotTable.setRows(pivotRowsList) ;
			aPivotTable.setValues(pivotValuesList) ;
			aPivotTable.setValueLayout(valueLayout) ;

			java.util.List<CellData> cellDataList = new ArrayList<>();
			cellDataList.add(new CellData()
				.setPivotTable(aPivotTable)
			) ;
			java.util.List<RowData> rowDataList = new ArrayList<>();
			rowDataList.add(new RowData()
				.setValues(cellDataList)
			) ;

			String fieldMask = "pivotTable" ;
			java.util.List<Request> requests = new ArrayList<>();
			requests.add(new Request()								// 1
				.setUpdateCells(new UpdateCellsRequest()			// 2
					.setRows(rowDataList)
					.setStart(new GridCoordinate()
						.setSheetId(sheetID)
						.setRowIndex(53)
						.setColumnIndex(1)
					)
					.setFields(fieldMask)
				)													// 2
			) ;														// 1

			BatchUpdateSpreadsheetRequest request = new BatchUpdateSpreadsheetRequest().setRequests(requests);
			BatchUpdateSpreadsheetResponse response = service.spreadsheets().batchUpdate(spreadsheetId, request).execute();

			System.out.println(response);
		} catch (Exception ioe) {
			System.err.println("error:" + ioe.getMessage());
		}
	}

	public void getBoundaryGPSheet()
	{
		try {
			// Build a new authorized API client service.
			Sheets service = getSheetsService();

			String spreadsheetId = "1ACO-DitYe4g9oxa8QcSNjUfnhiWcSvvvXO-5YCWidQc";
			String range = "Sheet1!A1:Z";

			// True if grid data should be returned.
			// This parameter is ignored if a field mask was set in the request.
			boolean includeGridData = false;

			ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute() ;
			//System.out.println(response);

			java.util.List<java.util.List<Object>> values = response.getValues();
			if (values == null || values.size() == 0) {
				System.out.println("No data found.");
			} else {
			  int nMaxCol = 0 ;
			  for (java.util.List row : values) {
				  if (row.size() > nMaxCol) nMaxCol = row.size() ;
			  } // for
			  System.out.println("MaxRow = " + values.size() + ", MaxCol = " + nMaxCol);
			} // else
		} catch (Exception ioe) {
			System.err.println("error:" + ioe.getMessage());
		}
	}

	public void addGPSheetJSON()
	{	/*
		{	// 1
		  "requests": [	// 2
			{	// 2.1
			  "updateSheetProperties": {	// 2.1.1
				"properties": {		// 2.2.1.1
					"sheetId": 0, "title": "New Sheet Name"},
				"fields": "title"
			  }	// 2.1.1
			}	// 2.1
		  ]	// 2
		}	// 1

		try {
			JsonBuilderFactory factory = Json.createBuilderFactory(); //config
			JsonObject value = factory.createObjectBuilder()		// 1
					 .add("requests", factory.createArrayBuilder() 	// 2
						 .add(factory.createObjectBuilder()		// 2.1
							.add("updateSheetProperties", factory.createObjectBuilder() 	// 2.1.1
								.add("properties", factory.createObjectBuilder() 	// 2.1.1.1
								 .add("sheetId", 0)
								 .add("title", "New York")
							) // 2.1.1.1
						.add("fields", "title")
					) // 2.1.1
				) // 2.1
			) // 2
			.build() ; // 1
		} catch (Exception ioe) {
			System.err.println("error:" + ioe.getMessage());
		}
		*/
	}


	public void addGPSheet()
	{
		try {
			// Build a new authorized API client service.
			Sheets service = getSheetsService();

			// The ID of the spreadsheet to update.
			String spreadsheetId = "1ACO-DitYe4g9oxa8QcSNjUfnhiWcSvvvXO-5YCWidQc";

			String spreadsheetTitle = "gpSheet1" ;
			String fieldMask = "title" ;

			java.util.List<Request> requests = new ArrayList<>();

			/* tested & works
			// Change the spreadsheet's title.
			requests.add(new Request()
				.setUpdateSpreadsheetProperties(new UpdateSpreadsheetPropertiesRequest()
					.setProperties(new SpreadsheetProperties()
				.setTitle(spreadsheetTitle))
			.setFields(fieldMask)));
			*/

			/* untested
			// Find and replace text.
			requests.add(new Request()
					.setFindReplace(new FindReplaceRequest()
						.setFind(find)
						.setReplacement(replacement)
						.setAllSheets(true)));
			*/

			// add new sheet
			String sheetTitle = "default2" ;
			int	idToDelete = -1 ;

			//locate sheet ID
			Spreadsheet sheetsResponse = service.spreadsheets().get(spreadsheetId).execute() ;
			//System.out.println(sheetsResponse);

			java.util.List<Sheet> values = sheetsResponse.getSheets();
			if (values == null || values.size() == 0) {
				System.out.println("No data found.");
			} else {
				Iterator<Sheet> sheetIterator = values.iterator();
				while (sheetIterator.hasNext()) {
					SheetProperties sp = sheetIterator.next().getProperties() ;
					System.out.println("sp.getTitle(): " + sp.getTitle() + ", getSheetId():" + sp.getSheetId());

					if (sheetTitle.equals(sp.getTitle()))
					{
						idToDelete = sp.getSheetId() ;
						System.out.println("idToDelete: " + idToDelete);
						break ;
					}
				}
			}

			if (idToDelete != -1) {
			requests.add(new Request()
				.setDeleteSheet(new DeleteSheetRequest()
					.setSheetId(idToDelete)));
			}

			requests.add(new Request()
				.setAddSheet(new AddSheetRequest()
					.setProperties(new SheetProperties()
					.setTitle(sheetTitle))));

			BatchUpdateSpreadsheetRequest request = new BatchUpdateSpreadsheetRequest().setRequests(requests);
			BatchUpdateSpreadsheetResponse response = service.spreadsheets().batchUpdate(spreadsheetId, request).execute();

			System.out.println(response);
		} catch (Exception ioe) {
			System.err.println("error:" + ioe.getMessage());
		}
	}

	private java.util.List<java.util.List<Object>> getData2()
	{
		// declarations
		// constants
		String READ_SEPARATOR = "\t" ;
		String DEFAULT_GROUP = "default" ;
		String fileName = "default.may.mint.tab.out.csv" ;

		// members
		Hashtable<String, ArrayList<String>> m_exportLinesGroup ;
		java.util.List<java.util.List<Object>> m_SheetsData = null ;

		// open file
		FileReader fileReader = null;
		try {
			java.io.File aFile = new java.io.File(fileName);
			if (!aFile.exists()) throw new FileNotFoundException("File  " + fileName + " does not exist.");

			//read file
			fileReader = new FileReader(aFile);
			BufferedReader buffReader = new BufferedReader(fileReader);
			String sLine = "";

			String group = DEFAULT_GROUP ;
			m_exportLinesGroup = new Hashtable<String, ArrayList<String>>() ;
			ArrayList<String> aGrp = new ArrayList<String>() ;

			try {
				// read into Hashtable
				while ((sLine = buffReader.readLine()) != null) aGrp.add(sLine) ;
				m_exportLinesGroup.put(group, aGrp) ;
				buffReader.close() ;	fileReader.close() ;

				// load List from Hashtable
				m_SheetsData = new ArrayList<java.util.List<Object>>() ;
				ArrayList<String> exportLines = m_exportLinesGroup.get(group) ;
				if (exportLines == null) System.err.println("exportLines == null");
				for (String aLine : exportLines) {
					java.util.List<Object> aRow = new ArrayList<Object>();
					String[] pieces = aLine.split(READ_SEPARATOR);
					for (String p : pieces) aRow.add(p);	// add each item as a column item
					m_SheetsData.add(aRow);					// add full row
				}
			} catch (IOException e) {
				System.err.println("There was a problem reading:" + fileName);
			}
		} catch (FileNotFoundException e) {
			System.err.println("Could not locate a file: " + e.getMessage());
		}

		return m_SheetsData ;
	}


	private java.util.List<java.util.List<Object>> getData ()  {
		java.util.List<Object> R1 = new ArrayList<Object>();
		R1.add ("A1");
		R1.add ("B1");
		R1.add ("C1");
		R1.add ("D1");

		java.util.List<Object> R2 = new ArrayList<Object>();
		R2.add ("A2");
		R2.add ("B2");
		R2.add ("C2");
		R2.add ("D2");

		java.util.List<java.util.List<Object>> data = new ArrayList<java.util.List<Object>>();
		data.add (R1);
		data.add (R2);

		return data;
	}

	public void updateGPSheet()
	{
		try {
			// Build a new authorized API client service.
			Sheets service = getSheetsService();

			// The ID of the spreadsheet to update.
			String spreadsheetId = "1ACO-DitYe4g9oxa8QcSNjUfnhiWcSvvvXO-5YCWidQc";

			//String range = RowStart+":"+RowEnd;
			String range = "Sheet1"; //"Sheet1!A1"	"Sheet1!A1:B1";

			java.util.List<java.util.List<Object>> arrData = getData2();

			ValueRange requestBody = new ValueRange();
			requestBody.setRange(range);
			requestBody.setValues(arrData);

			java.util.List<ValueRange> oList = new ArrayList<>();
			oList.add(requestBody);

			BatchUpdateValuesRequest request = new BatchUpdateValuesRequest();
			// How the input data should be interpreted.
			String valueInputOption = "USER_ENTERED"; //"RAW";// John

			request.setValueInputOption(valueInputOption);
			request.setData(oList);

			BatchUpdateValuesResponse response = service.spreadsheets().values().batchUpdate(spreadsheetId, request).execute();
			System.out.println(response);
		} catch (Exception ioe) {
			System.err.println("error:" + ioe.getMessage());
		}
	}

	public void locateGPSheet(String sheetName)
	{
		try {
			Drive driveService = getDriveService() ;

			// Build a new authorized API client service.
			//Sheets service = getSheetsService();

			Drive.Files.List request = driveService.files().list()
						.setPageSize(10)
						// Available Query parameters here:
						//https://developers.google.com/drive/v3/web/search-parameters
						.setQ("mimeType = 'application/vnd.google-apps.spreadsheet' and trashed = false" + " and name = " + "\'" + sheetName + "\'") //and name contains 'gpSheets1'
						.setFields("nextPageToken, files(id, name)");

			FileList result = request.execute();
			System.out.println("FileList result = " + result);

			java.util.List<File> files = result.getFiles();
			String spreadsheetId = null;
			if (files != null) {
				for (File file : files) {
					if (file.getName().equals(sheetName)) {
						spreadsheetId = file.getId();
						System.out.println("sheetName = " + sheetName + " ,spreadsheetId = " + spreadsheetId);
					}
				}
			}
		} catch (Exception ioe) {
			System.err.println("error:" + ioe.getMessage());
		}
	}


	public void formatGPSheet()
	{
		try {
			// Build a new authorized API client service.
			Sheets service = getSheetsService();

			// The ID of the spreadsheet to update.
			String spreadsheetId = "1ACO-DitYe4g9oxa8QcSNjUfnhiWcSvvvXO-5YCWidQc";

			//String range = RowStart+":"+RowEnd;
			String range = "Sheet1!K"; //"Sheet1!A1"	"Sheet1!A1:B1";


			java.util.List<Request> requests = new ArrayList<>();

			final int sheetIDSheet1 = 0 ;
			final int sheetIDDefault = 1417346082 ;
			int sheetID = sheetIDSheet1 ; 		// Sheet1
			String fieldMask = "userEnteredFormat.numberFormat" ;
			String sType = "NUMBER"; ;
			String sPattern = "" ;

			sheetID = 0; 	// Sheet1
			//sPattern = "#,##0.00" ;
			sPattern = "_($* #,##0.00_);_($* (#,##0.00);_($* \"-\"??_);_(@_)" ;

			requests.add(new Request()								// 1
				.setRepeatCell(new RepeatCellRequest()				// 2
					.setRange(new GridRange()						// 3
						.setSheetId(sheetID)
						.setStartRowIndex(2)
						.setStartColumnIndex(4)
						.setEndRowIndex(51)
						.setEndColumnIndex(16)
					)												// 3
					.setCell(new CellData()							// 4
						.setUserEnteredFormat(new CellFormat()		// 5
							.setNumberFormat(new NumberFormat()		// 6
								.setType(sType)
								.setPattern(sPattern)
							)										// 6
						)											// 5
					)												// 4
				.setFields(fieldMask)
				)													// 2
			);														// 1

			sheetID = sheetIDDefault; 	// default
			fieldMask = "userEnteredValue" ;
			String sFormulaValue = "= A1 + 1" ;

			requests.add(new Request()								// 1
				.setRepeatCell(new RepeatCellRequest()				// 2
					.setRange(new GridRange()						// 3
						.setSheetId(sheetID)
						.setStartRowIndex(1)
						.setStartColumnIndex(0)
						.setEndRowIndex(6)
						.setEndColumnIndex(3)
					)												// 3
					.setCell(new CellData()							// 4
						.setUserEnteredValue(new ExtendedValue()	// 5
							.setFormulaValue(sFormulaValue)
						)											// 5
					)												// 4
				.setFields(fieldMask)
				)													// 2
			);														// 1

			BatchUpdateSpreadsheetRequest request = new BatchUpdateSpreadsheetRequest().setRequests(requests);
			BatchUpdateSpreadsheetResponse response = service.spreadsheets().batchUpdate(spreadsheetId, request).execute();

			System.out.println(response);
		} catch (Exception ioe) {
			System.err.println("error:" + ioe.getMessage());
		}
	}


	public void writeGPSheet()
	{
		try {
			// TODO: Assign values to desired fields of `requestBody`:
			Spreadsheet requestBody = new Spreadsheet();

			// Build a new authorized API client service.
			Sheets service = getSheetsService();
			Sheets.Spreadsheets.Create request = service.spreadsheets().create(requestBody);

			Spreadsheet response = request.execute();

			// TODO: Change code below to process the `response` object:
			System.out.println(response);
		} catch (Exception ioe) {
			System.err.println("error:" + ioe.getMessage());
		}
	}

	public void readGPSheet()
	{
		try {
			// Build a new authorized API client service.
			Sheets service = getSheetsService();

			//System.out.println("ready to read sheet ...");


			// Prints the names and majors of students in a sample spreadsheet:
			// https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
			//String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
			//String range = "Class Data!A2:E";

			String spreadsheetId = "1qjGyUmxiYC3jG1knpd2NxtQY7Nl15UbD44bAOeyJ6TA" ;	// Budget Planning 2017
			//String range = "budgeting!A5:G19";	// budgeting
			String range = "rimatracker!A2:G32";	// rima

			//String spreadsheetId = "12dy6WyB1xRYS9p3jO-301OZdCEG8qdqzn8y3s0EFBaQ" ;		// toDo
			//String range = "Groceries!A2:H7";		// toDo

			// The ranges to retrieve from the spreadsheet.
			//List<String> range = new ArrayList<>(); // TODO: Update placeholder value.
			//List<String> range = new ArrayList<String>(Arrays.asList("budgeting!A5", "budgeting!G5"));

			// True if grid data should be returned.
			// This parameter is ignored if a field mask was set in the request.
			boolean includeGridData = false;

			//Sheets.Spreadsheets.Get request = service.spreadsheets().get(spreadsheetId);
			//Sheets.Spreadsheets.Get request = service.spreadsheets().values().get(spreadsheetId);
			//System.out.println("request:" + request.toString());
			ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute() ;
			System.out.println(response);

			/*
			//request.setRanges(range);
			request.setIncludeGridData(includeGridData);

			//Spreadsheet response = request.execute();
			//ValueRange response = request.execute();

			// TODO: Change code below to process the `response` object:
			//System.out.println(response);

			ValueRange response = service.spreadsheets().values()
				.get(spreadsheetId, range)
				.execute();
			*/

			java.util.List<java.util.List<Object>> values = response.getValues();
			if (values == null || values.size() == 0) {
				System.out.println("No data found.");
			} else {
			  //System.out.println("Name, Budget, Balance, Actual, Actual Balance, Diff"); 	// budgeting
			  System.out.println("Date, Item, Desc, , , Amt");								// rima
			  //System.out.println("Indian store,	Sams,	aldis, 	Tj  maxx,	Walmart,	Ikea,	chinese store, 	$ store");	// toDo
			  for (java.util.List row : values) {
				Iterator<String> sheetRowIterator = row.iterator();
				while (sheetRowIterator.hasNext()) {
					System.out.print(sheetRowIterator.next() + ",");
				}
				System.out.println("");
			  } // for
			} // else
		} catch (Exception ioe) {
			System.err.println("error:" + ioe.getMessage());
		}
	}


} // end of class