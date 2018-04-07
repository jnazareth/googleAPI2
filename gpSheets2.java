//package gp;

/*
 * BEFORE RUNNING:
 * ---------------
 * 1. If not already done, enable the Google Sheets API
 *    and check the quota for your project at
 *    https://console.developers.google.com/apis/api/sheets
 * 2. Install the Java client library on Maven or Gradle. Check installation
 *    instructions at https://github.com/google/google-api-java-client.
 *    On other build systems, you can add the jar files to your project from
 *    https://developers.google.com/resources/api-libraries/download/sheets/v4/java
 */
//package testSheets;

public class gpSheets2 {
	public static void main(String[] args) {
		try {
			gpSheetsAPI mySheets = new gpSheetsAPI() ;
			mySheets.addPivot();
			//mySheets.getBoundaryGPSheet();
			//mySheets.formatGPSheet();
			//mySheets.locateGPSheet("gpSheet1");
			//mySheets.updateGPSheet() ;
			//mySheets.readGPSheet() ;
			//mySheets.addGPSheet() ;
			//mySheets.writeGPSheet() ;
		} catch (Exception ioe) {
		}
	}
}
