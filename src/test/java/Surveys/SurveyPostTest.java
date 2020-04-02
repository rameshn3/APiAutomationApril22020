package Surveys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.base.TestBase;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class SurveyPostTest extends TestBase{
private String fpath_206="C:/VetaHealth/MigrationComparision/ExcelSurveys/OneDrive_2019-10-11/sourceExcelFiles/Src-survey-206.xlsx";

private String fpath_207="C:/VetaHealth/MigrationComparision/ExcelSurveys/OneDrive_2019-10-11/sourceExcelFiles/Src-survey-207.xlsx";
private String fpath_209="C:/VetaHealth/MigrationComparision/ExcelSurveys/OneDrive_2019-10-11/sourceExcelFiles/Src-survey-209.xlsx";

private String fpath_92="C:/VetaHealth/MigrationComparision/ExcelSurveys/OneDrive_2019-10-11/sourceExcelFiles/Src-survey-92.xlsx";

private String fpath_53="C:/VetaHealth/MigrationComparision/ExcelSurveys/OneDrive_2019-10-11/sourceExcelFiles/Src-survey-53.xlsx";

Map<String,List<String>>srchmp=null;
List<String>srcValues=null;
public SurveyPostTest() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	private String endpointUrl=null;
	@BeforeClass
	public void setup() {
		RestAssured.baseURI=prop.getProperty("stage3url");
		RestAssured.useRelaxedHTTPSValidation();
		endpointUrl=prop.getProperty("serviceApi");
		
		
	}
	
	
	@Test(dataProvider="surveyData")
	public void fetchSubmitedResponseTest(String UsrScrnId,String patientId,String ResponderUserId,String questionid,String questionType, String respText) {
		String surveyNo="92";
		System.out.println("********Starting Validation for *Survey -"+surveyNo+" *********************");
		 UsrScrnId=String.valueOf(UsrScrnId);
		String tokenId=UsrScrnId+"_"+patientId+"_"+ResponderUserId;
		
		Map<String, Object>  jsonAsMap = new HashMap<>();
		jsonAsMap.put("surveyId", surveyNo);
		jsonAsMap.put("token", tokenId);
		System.out.println(jsonAsMap);
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		Response resp=given().
				    body(jsonAsMap).
				    when().contentType (ContentType.JSON)
				   .post(endpointUrl).
				    then().assertThat().statusCode(200).and().
				    contentType(ContentType.JSON).and().
				    extract().response();
		    
		System.out.println("status code-->"+resp.getStatusCode());
		System.out.println("response-->"+resp.asString());
		
		JsonPath jPath=new JsonPath(resp.asString());
		ArrayList<String> lst = jPath.get("message.answeredQuestions");
        JSONArray jAry = new JSONArray(lst);
        JSONObject jObj = new JSONObject();
        Map<String, List<String>> hmp = null;
        List<String> values = null;
       
        for (int i = 0; i < jAry.length(); i++) {
            hmp = new HashMap<String, List<String>>();
            values = new ArrayList<String>();
            jObj = jAry.getJSONObject(i);
            
            values.add(jObj.get("submittedResponse").toString());
           // values.add(jObj.get("question").toString());
            hmp.put(jObj.get("title").toString(), values);

       
        }
        
        System.out.println("hashmap-->"+hmp);
        System.out.println("hashmap values-->"+values);
		
    
        
   if(questionType.equals("mc")||questionType.equals("CALCULATION")||questionType.equals(" ")||questionType.equals("EVALUATION")) {
    	   for(int i=0;i<3;i++) {
    		   srchmp=new HashMap<String,List<String>>();
    		   srcValues=new ArrayList<String>();
    		   srcValues.add(respText);
    		   String final_questionid="SQ"+surveyNo+questionid;
    		   srchmp.put(final_questionid, srcValues);
    	   }
     
       System.out.println("Source hashmap-->"+srchmp);
       System.out.println("Source hashmap values-->"+srcValues); 
       
       if (srchmp.equals(hmp)) {
    	   System.out.println("both maps are equal!");
    	} else {
    	 System.out.println("maps are NOT equal!");
    	}
    }
	System.out.println("********END OF ***90413_PT002670_US002744 and question id :18798************************");
	}
	
@DataProvider
public Object[][] surveyData() throws InvalidFormatException, IOException{
	Object[][] data=new ExcelUtil().getTestData(fpath_92, "survey-92");
	return data;
}




}