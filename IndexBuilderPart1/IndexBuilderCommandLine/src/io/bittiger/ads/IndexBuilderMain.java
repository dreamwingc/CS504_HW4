package io.bittiger.ads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class IndexBuilderMain {

    public static void main(String[] args) throws IOException {
        if(args.length < 4)
        {
            System.out.println("Usage: AdsServer <adsDataFilePath> <budgetDataFilePath> <memcachedServer> <memcachedPortal>");
            System.exit(0);
        }
        String adsDataFilePath = args[0];
        String budgetDataFilePath = args[1];
        String memcachedServer = args[2];
        int memcachedPortal = Integer.parseInt(args[3]);
        String mysql_host = "127.0.0.1:3306";
        String mysql_db = "searchads";
        String mysql_user = "root";
        String mysql_pass = "yourPassWord";
        AdsEngine adsEngine = new AdsEngine(adsDataFilePath,budgetDataFilePath,memcachedServer,memcachedPortal,mysql_host,mysql_db,mysql_user,mysql_pass);
        if(adsEngine.init())
        {
            System.out.println("Ready to take query");
        }
    }
}
