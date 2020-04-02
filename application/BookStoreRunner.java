package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        Gson gson = new Gson();
        ResourcesHolder rhInst = ResourcesHolder.getInstance();
        Inventory invInst = Inventory.getInstance();
        MoneyRegister mrInst = MoneyRegister.getInstance();
        HashMap<Integer, Customer> customerHashMap = new HashMap<>(); //customer Id, customer

        try {
            FileReader reader = new FileReader(args[0]);
            HashMap details = gson.fromJson(reader, HashMap.class);
            // Initial Inventory
            ArrayList init_inventory = ((ArrayList) details.get("initialInventory"));
            int num = init_inventory.size();
            BookInventoryInfo[] inventory = new BookInventoryInfo[init_inventory.size()];
            for (int i = 0; i < init_inventory.size(); i++) {
                LinkedTreeMap bookMap = (LinkedTreeMap) init_inventory.get(i);
                String BookTitle = (String) bookMap.get("bookTitle");
                Double Doubamount = (Double) bookMap.get("amount");
                int Amount = Doubamount.intValue();
                Double Doubprice = (Double) bookMap.get("price");
                int price = Doubprice.intValue();
                inventory[i] = new BookInventoryInfo(BookTitle, Amount, price);
            }
            invInst.load(inventory);

            //Initial Resources
            ArrayList init_resources = ((ArrayList) details.get("initialResources"));
            LinkedTreeMap vehiclesMap = (LinkedTreeMap) init_resources.get(0);
            ArrayList init_vehicles = ((ArrayList) vehiclesMap.get("vehicles"));
            DeliveryVehicle[] vehicles = new DeliveryVehicle[init_vehicles.size()];
            for (int i = 0; i < init_vehicles.size(); i++) {
                LinkedTreeMap vehiclesTree = (LinkedTreeMap) init_vehicles.get(i);
                Double Doublicense = (Double) vehiclesTree.get("license");
                int license = Doublicense.intValue();
                Double Doubspeed = (Double) vehiclesTree.get("speed");
                int speed = Doubspeed.intValue();
                vehicles[i] = new DeliveryVehicle(license, speed);
            }
            rhInst.load(vehicles);

            //InitialServices
            LinkedTreeMap init_services = ((LinkedTreeMap) details.get("services"));
            //SellingServices
            Double sellingNum = (Double) init_services.get("selling");
            int selling = sellingNum.intValue();
            Thread[] sellArr = new Thread[selling];
            for (int i = 0; i < selling; i++) {
                sellArr[i] = new Thread(new SellingService(i + 1));
                sellArr[i].start();
            }
            //InventoryServices
            Double invNum = (Double) init_services.get("inventoryService");
            int inven = invNum.intValue();
            Thread[] invArr = new Thread[inven];
            for (int i = 0; i < inven; i++) {
                invArr[i] = new Thread(new InventoryService(i + 1));
                invArr[i].start();
            }
            //LogisticsServices
            Double logNum = (Double) init_services.get("logistics");
            int logis = logNum.intValue();
            Thread[] logArr = new Thread[logis];
            for (int i = 0; i < logis; i++) {
                logArr[i] = new Thread(new LogisticsService(i + 1));
                logArr[i].start();
            }
            //ResourcesService
            Double resNum = (Double) init_services.get("resourcesService");
            int res = resNum.intValue();
            Thread[] resArr = new Thread[res];
            for (int i = 0; i < res; i++) {
                resArr[i] = new Thread(new ResourceService(i + 1));
                resArr[i].start();
            }
            //Customers
            ArrayList cust_arr = ((ArrayList) init_services.get("customers"));
            Thread[] apiArr = new Thread[cust_arr.size()];
            for (int i = 0; i < cust_arr.size(); i++) {
                ConcurrentHashMap<Integer, LinkedList<String>> ordersMap = new ConcurrentHashMap<>(); //map for orders
                LinkedTreeMap custMap = (LinkedTreeMap) cust_arr.get(i);
                Double Did = (Double) custMap.get("id");
                int id = Did.intValue();
                String name = (String) custMap.get("name");
                String address = (String) custMap.get("address");
                Double Ddistance = (Double) custMap.get("distance");
                int distance = Ddistance.intValue();
                LinkedTreeMap creditMap = (LinkedTreeMap) custMap.get("creditCard");
                Double DcreditNum = (Double) creditMap.get("number");
                int creditNum = DcreditNum.intValue();
                Double Damount = (Double) creditMap.get("amount");
                int amount = Damount.intValue();
                ArrayList orderSchedule = ((ArrayList) custMap.get("orderSchedule"));
                Customer customer = new Customer(id, name, address, distance, creditNum, amount);
                customerHashMap.put(id, customer); //adding the customer to the hashmap
                for (int j = 0; j < orderSchedule.size(); j++) { //book orders
                    LinkedTreeMap bookiMap = (LinkedTreeMap) orderSchedule.get(j);
                    String bookTitle = (String) bookiMap.get("bookTitle");
                    Double Dtick = (Double) bookiMap.get("tick");
                    Integer tick = Dtick.intValue();
                    LinkedList<String> l;
                    if (ordersMap.get(tick) == null) //checking if there is already a list with that tick
                        l = new LinkedList<>();
                    else
                        l = ordersMap.remove(tick);
                    l.add(bookTitle);
                    ordersMap.put(tick, l);
                }
                apiArr[i] = new Thread(new APIService(i + 1, customer, ordersMap));
                apiArr[i].start();
            }

            //TimeService
            AtomicInteger total = new AtomicInteger(selling + inven + logis + res + cust_arr.size());
            ServicesCounter counter = ServicesCounter.getInstance();
            counter.SetTotal(total);
            //waiting until all the micro services are init
            while (!counter.isDone()) {
            }
            LinkedTreeMap timeMap = (LinkedTreeMap) init_services.get("time");
            Double Dospeed = (Double) timeMap.get("speed");
            int speed = Dospeed.intValue();
            Double Doduration = (Double) timeMap.get("duration");
            int duration = Doduration.intValue();
            Thread time = new Thread(new TimeService(speed, duration));
            time.start();

            for (int i = 0; i < sellArr.length; i++) {
                try {
                    sellArr[i].join();
                    counter.Decrement();
                } catch (InterruptedException e) {
                }
            }

            for (int i = 0; i < invArr.length; i++) {
                try {
                    invArr[i].join();
                    counter.Decrement();
                } catch (InterruptedException e) {
                }
            }
            for (int i = 0; i < apiArr.length; i++) {
                try {
                    apiArr[i].join();
                    counter.Decrement();
                } catch (InterruptedException e) {
                }
            }
            for (int i = 0; i < logArr.length; i++) {
                try {
                    logArr[i].join();
                    counter.Decrement();
                } catch (InterruptedException e) {
                }
            }
            for (int i = 0; i < resArr.length; i++) {
                try {
                    resArr[i].join();
                    counter.Decrement();
                } catch (InterruptedException e) {
                }
            }

            counter.initialize(); //put 0 in total
            try {
                time.join();
            } catch (InterruptedException e) {
            }

        } catch (FileNotFoundException e) {
        }
        //Print SERIALIZED FILES

        try {
            FileOutputStream f = new FileOutputStream((args[1]));
            ObjectOutputStream object = new ObjectOutputStream(f);
            object.writeObject(customerHashMap);
            object.close();
            f.close();
        } catch (IOException e) {
            System.out.println("Error writing file customerHS ");
        }

        invInst.printInventoryToFile(args[2]);
        mrInst.printOrderReceipts(args[3]);


        try {
            FileOutputStream f = new FileOutputStream((args[4]));
            ObjectOutputStream object = new ObjectOutputStream(f);
            object.writeObject(MoneyRegister.getInstance());
            object.close();
            f.close();
        } catch (IOException e) {
            System.out.println("Error writing file MoneyRegister ");
        }

    }

}
