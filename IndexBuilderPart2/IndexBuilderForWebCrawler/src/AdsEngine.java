import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

import org.json.*;

public class AdsEngine {

   // private String mAdsDataFilePath;
    private String mBudgetFilePath;
    private IndexBuilder indexBuilder;
    private String mMemcachedServer;
    private int mMemcachedPortal;
    private String mysql_host;
    private String mysql_db;
    private String mysql_user;
    private String mysql_pass;

    public AdsEngine(String budgetDataFilePath,String memcachedServer,int memcachedPortal,String mysqlHost,String mysqlDb,String user,String pass)
    {
        //mAdsDataFilePath = adsDataFilePath;
        mBudgetFilePath = budgetDataFilePath;
        mMemcachedServer = memcachedServer;
        mMemcachedPortal = memcachedPortal;
        mysql_host = mysqlHost;
        mysql_db = mysqlDb;
        mysql_user = user;
        mysql_pass = pass;
        indexBuilder = new IndexBuilder(memcachedServer,memcachedPortal,mysql_host,mysql_db,mysql_user,mysql_pass);
    }

    public Boolean init()
    {
        //load budget data
        try (BufferedReader brBudget = new BufferedReader(new FileReader(mBudgetFilePath))) {
            String line;
            while ((line = brBudget.readLine()) != null) {
                JSONObject campaignJson = new JSONObject(line);
                Long campaignId = campaignJson.getLong("campaignId");
                double budget = campaignJson.getDouble("budget");
                Campaign camp = new Campaign();
                camp.campaignId = campaignId;
                camp.budget = budget;
                if(!indexBuilder.updateBudget(camp))
                {
                    //log
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        indexBuilder.Close();
        return true;
    }
}