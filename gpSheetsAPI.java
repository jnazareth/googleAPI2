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
import com.google.api.services.sheets.v4.model.AppendValuesResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

//import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

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
   private final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);

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
		if (in == null) System.out.println("InputStream::in: NULL");
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

	private Sheets createSheetsService() throws IOException, GeneralSecurityException {
	    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

	    // TODO: Change placeholder below to generate authentication credentials. See
	    // https://developers.google.com/sheets/quickstart/java#step_3_set_up_the_sample
	    //
	    // Authorize using one of the following scopes:
	    //   "https://www.googleapis.com/auth/drive"
	    //   "https://www.googleapis.com/auth/drive.file"
	    //   "https://www.googleapis.com/auth/spreadsheets"
	    GoogleCredential credential = null;

	    return new Sheets.Builder(httpTransport, jsonFactory, credential)
	        .setApplicationName(APPLICATION_NAME)
	        .build();
	  }

	private List<List<Object>> getData2()
	{
		// declarations
		// constants
		String READ_SEPARATOR = "\t" ;
		String DEFAULT_GROUP = "default" ;
		String fileName = "default.may.mint.tab.out.csv" ;

		// members
		Hashtable<String, ArrayList<String>> m_exportLinesGroup ;
		List<List<Object>> m_SheetsData = null ;

		// open file
		FileReader fileReader = null;
		try {
			File aFile = new File(fileName);
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
				m_SheetsData = new ArrayList<List<Object>>() ;
				ArrayList<String> exportLines = m_exportLinesGroup.get(group) ;
				if (exportLines == null) System.err.println("exportLines == null");
				for (String aLine : exportLines) {
					List<Object> aRow = new ArrayList<Object>();
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


	private List<List<Object>> getData ()  {
		List<Object> R1 = new ArrayList<Object>();
		R1.add ("A1");
		R1.add ("B1");
		R1.add ("C1");
		R1.add ("D1");

		List<Object> R2 = new ArrayList<Object>();
		R2.add ("A2");
		R2.add ("B2");
		R2.add ("C2");
		R2.add ("D2");

		List<List<Object>> data = new ArrayList<List<Object>>();
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

			List<List<Object>> arrData = getData2();

			ValueRange requestBody = new ValueRange();
			requestBody.setRange(range);
			requestBody.setValues(arrData);

			List<ValueRange> oList = new ArrayList<>();
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
			//System.out.println(response);

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

			List<List<Object>> values = response.getValues();
			if (values == null || values.size() == 0) {
				System.out.println("No data found.");
			} else {
			  //System.out.println("Name, Budget, Balance, Actual, Actual Balance, Diff"); 	// budgeting
			  System.out.println("Date, Item, Desc, , , Amt");								// rima
			  //System.out.println("Indian store,	Sams,	aldis, 	Tj  maxx,	Walmart,	Ikea,	chinese store, 	$ store");	// toDo
			  for (List row : values) {
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