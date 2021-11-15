import static spark.Spark.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.*;
import java.io.FileInputStream;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.*;

public class Main {


    public static void main(String[] args) throws IOException{

        HashMap<String, ArrayList<Double>> hashmap = new HashMap<String, ArrayList<Double>>();
        HashMap<String, Double> stock = new HashMap<>();
        HashMap<String, Double> capacity = new HashMap<>();
        String inventory_path = "C:\\Users\\Peter\\Desktop\\code-challenge\\server\\src\\main\\java\\Inventory.xlsx";
        File f = new File(inventory_path);
		FileInputStream fis = new FileInputStream(f);
		XSSFWorkbook excelWorkbook = new XSSFWorkbook(fis);
		XSSFSheet excelSheet = excelWorkbook.getSheetAt(0);
		int rows = excelSheet.getPhysicalNumberOfRows();
		int cols = excelSheet.getRow(0).getPhysicalNumberOfCells();
		XSSFCell cell;
		for(int i =1 ; i< rows;i++)
		{
            ArrayList<Double> arraylist = new ArrayList<Double>();
			for(int j=1;j<cols;j++)
			{
				cell = excelSheet.getRow(i).getCell(j);
				Double cellContents=cell.getNumericCellValue();
                arraylist.add(cellContents);
			}
            stock.put(excelSheet.getRow(i).getCell(0).getStringCellValue(), arraylist.get(0));
            capacity.put(excelSheet.getRow(i).getCell(0).getStringCellValue(), arraylist.get(1));
            hashmap.put(excelSheet.getRow(i).getCell(0).getStringCellValue(), arraylist);
		}

        String inventoryjson = new Gson().toJson(hashmap);

        ArrayList<String> lowStock = new ArrayList<String>();

        for (String key : hashmap.keySet()) {
            ArrayList<Double> arraylist = new ArrayList<Double>();
            arraylist = hashmap.get(key);
            if (arraylist.get(0)/arraylist.get(1)< .25) {
                lowStock.add(key);
            }
        }

        String json = new Gson().toJson(lowStock);

		fis.close();

        String distributors_path = "C:\\Users\\Peter\\Desktop\\code-challenge\\server\\src\\main\\java\\Distributors.xlsx";
        File f1 = new File(distributors_path);
		FileInputStream fis1 = new FileInputStream(f1);
		XSSFWorkbook excelWorkbook1 = new XSSFWorkbook(fis1);

        HashMap<String, Double> cheapest = new HashMap<>();


        for(int q =0 ; q< excelWorkbook1.getNumberOfSheets();q++){

        HashMap<String, ArrayList<Double>> hashmap1 = new HashMap<String, ArrayList<Double>>();
        XSSFSheet excelSheet1 = excelWorkbook1.getSheetAt(q);
        int rows1 = excelSheet1.getPhysicalNumberOfRows();
        int cols1 = excelSheet1.getRow(0).getPhysicalNumberOfCells();
        XSSFCell cell1;
        for(int i =1 ; i< rows1;i++)
        {
            ArrayList<Double> arraylist1 = new ArrayList<Double>();
            for(int j=1;j<cols1;j++)
            {
                cell1 = excelSheet1.getRow(i).getCell(j);
                Double cellContents1=cell1.getNumericCellValue();
                arraylist1.add(cellContents1);
            }
            hashmap1.put(excelSheet1.getRow(i).getCell(0).getStringCellValue(), arraylist1);
        }

        for (String item : stock.keySet()) {

        if(hashmap1.containsKey(item) && cheapest.containsKey(item)){
            if(hashmap1.get(item).get(1) < cheapest.get(item)){
                cheapest.put(item, hashmap1.get(item).get(1));
            }
        }
        else if (hashmap1.containsKey(item) && !(cheapest.containsKey(item))){
            cheapest.put(item, hashmap1.get(item).get(1));
        }

        }

        }

        double cost = 0;
        for (String key : lowStock) {
            cost += (capacity.get(key) - stock.get(key)) * cheapest.get(key);
        }

        String costjson = new Gson().toJson(cost);

        fis1.close();


        //This is required to allow GET and POST requests with the header 'content-type'
        options("/*",
                (request, response) -> {
                        response.header("Access-Control-Allow-Headers",
                                "content-type");

                        response.header("Access-Control-Allow-Methods",
                                "GET, POST");


                    return "OK";
                });

        //This is required to allow the React app to communicate with this API
        before((request, response) -> response.header("Access-Control-Allow-Origin", "http://localhost:3000"));

        //TODO: Return JSON containing the candies for which the stock is less than 25% of it's capacity
        get("/low-stock", (request, response) -> {
            return json;
        });

        get("/inventory", (request, response) -> {
            return inventoryjson;
        });

        //TODO: Return JSON containing the total cost of restocking candy
        post("/restock-cost", (request, response) -> {
            return costjson;
        });

    }

}