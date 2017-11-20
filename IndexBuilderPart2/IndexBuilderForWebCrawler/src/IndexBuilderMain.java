import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.rabbitmq.client.*;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.json.JSONArray;
import org.json.JSONObject;

public class IndexBuilderMain {

    private final static String IN_QUEUE_NAME = "q_product";
    //private final static String ERR_QUEUE_NAME = "q_error";

    private static ObjectMapper mapper;
    //private static Channel errChannel;

    public static void main(String[] args) throws IOException,TimeoutException {
        if(args.length < 3)
        {
            System.out.println("Usage: AdsServer <budgetDataFilePath> <memcachedServer> <memcachedPortal>");
            System.exit(0);
        }

        String budgetDataFilePath = args[0];
        String memcachedServer = args[1];
        int memcachedPortal = Integer.parseInt(args[2]);
        String mysql_host = "127.0.0.1:3306";
        String mysql_db = "searchads";
        String mysql_user = "root";
        String mysql_pass = "yourpassword";

        AdsEngine adsEngine = new AdsEngine(budgetDataFilePath,memcachedServer,memcachedPortal,mysql_host,mysql_db,mysql_user,mysql_pass);
        final IndexBuilder indexBuilder = new IndexBuilder(memcachedServer, memcachedPortal, mysql_host, mysql_db, mysql_user, mysql_pass);

        if(adsEngine.init())
        {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel inChannel = connection.createChannel();
            inChannel.queueDeclare(IN_QUEUE_NAME, true, false, false, null);
            inChannel.basicQos(10); // Per consumer limit
            mapper = new ObjectMapper();

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            //errChannel = connection.createChannel();
            //errChannel.queueDeclare(ERR_QUEUE_NAME, true, false, false, null);

            Consumer consumer = new DefaultConsumer(inChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    try {
                        String message = new String(body, "UTF-8");
                        System.out.println(" [x] Received '" + message + "'");

                        JSONObject adJson = new JSONObject(message);
                        Ad ad = new Ad();

                        ad.adId = adJson.getLong("adId");
                        ad.campaignId = adJson.getLong("campaignId");
                        ad.brand = adJson.isNull("brand") ? "" : adJson.getString("brand");
                        ad.price = adJson.isNull("price") ? 100.0 : adJson.getDouble("price");
                        ad.thumbnail = adJson.isNull("thumbnail") ? "" : adJson.getString("thumbnail");
                        ad.title = adJson.isNull("title") ? "" : adJson.getString("title");
                        ad.detail_url = adJson.isNull("detail_url") ? "" : adJson.getString("detail_url");
                        ad.bidPrice = adJson.isNull("bidPrice") ? 1.0 : adJson.getDouble("bidPrice");
                        ad.pClick = adJson.isNull("pClick") ? 0.0 : adJson.getDouble("pClick");
                        ad.category = adJson.isNull("category") ? "" : adJson.getString("category");
                        ad.description = adJson.isNull("description") ? "" : adJson.getString("description");
                        ad.keyWords = new ArrayList<String>();
                        JSONArray keyWords = adJson.isNull("keyWords") ? null : adJson.getJSONArray("keyWords");
                        for (int j = 0; j < keyWords.length(); j++) {
                            ad.keyWords.add(keyWords.getString(j));
                        }
                        //indexBuilder.buildInvertIndex(ad);

                        if (!indexBuilder.buildInvertIndex(ad) || !indexBuilder.buildForwardIndex(ad)) {
                            //log
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            inChannel.basicConsume(IN_QUEUE_NAME, true, consumer);
        }
    }
}