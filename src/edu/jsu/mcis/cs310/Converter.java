package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
        
            // INSERT YOUR CODE HERE
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> records = reader.readAll();  // from 10_DataExchange.pdf
            reader.close();
            
            if (records.isEmpty()) {
                return result; //returns empty
            }
            
            // time to start to extract the headers
            String[] headers = records.get(0);
            JsonArray prodNums = new JsonArray();
            JsonArray dataArray = new JsonArray();
            
            // process data rows
            for (int i = 1; i < records.size(); i++) {
                String[] row = records.get(i);
                prodNums.add(row[0]); // First column is ProdNum
                
                JsonArray rowData = new JsonArray();
                for (int j = 1; j < row.length; j++) {
                    if (j == 2 || j == 3) { // Convert Season and Episode to integers
                        rowData.add(Integer.parseInt(row[j]));
                    } else {
                        rowData.add(row[j]);
                    }
                }
                dataArray.add(rowData);
            }
            
            // making the JSON object
            JsonObject jsonOutput = new JsonObject();
            jsonOutput.put("ProdNums", prodNums);
            jsonOutput.put("ColHeadings", new JsonArray(Arrays.asList(headers)));
            jsonOutput.put("Data", dataArray);

            // convert JSON object to string
            result = Jsoner.serialize(jsonOutput);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            
            // INSERT YOUR CODE HERE
            JsonObject jsonObject = Jsoner.deserialize(jsonString, new JsonObject());

            JsonArray colHeaders = (JsonArray) jsonObject.get("ColHeadings");
            JsonArray prodNums = (JsonArray) jsonObject.get("ProdNums");
            JsonArray data = (JsonArray) jsonObject.get("Data");

            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\\', "\n");

            // Write column headers
            csvWriter.writeNext((String[]) colHeaders.toArray(new String[0]));

            // time to write data rows
            for (int i = 0; i < data.size(); i++) {
                JsonArray row = (JsonArray) data.get(i);
                List<String> rowValues = new ArrayList<>();
                rowValues.add(prodNums.get(i).toString()); // add ProdNum before continuing 

                for (Object value : row) {
                    rowValues.add(value.toString()); // ... into String
                }

                csvWriter.writeNext(rowValues.toArray(new String[0]));
            }

            // get output
            csvWriter.close();
            result = writer.toString();


        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
