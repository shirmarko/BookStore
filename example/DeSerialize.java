package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * #Hello , Our names are Tal Cohen and Amir Loewenthal And we wrote this DeSerialize For 1 Reason, Help you!
 *
 * Instructions:
 * add to the program the paths to the 4 files you serialized in the bookStoreRunner
 *
 * the 4 output files will be in your project folder
 *
 */
public class DeSerialize {
    public static void main(String[] args) {
      HashMap<Integer,Customer> h =  deser (args[0]);//Customers Hash map
      HashMap<String,Integer> I = deser2(args[1]);//Inventory Hash map
      LinkedList<OrderReceipt> A =  deser3(args[2]);//RecieptList
      MoneyRegister M = deser4(args[3]);//The money register


        writeTo("Customers.txt", h.toString());
        writeTo("Inventory.txt", I.toString());
        writeTo("RecieptList.txt", A.toString());
        writeTo("Moneyl.txt", M.toString());


    }

    private static  void writeTo(String path , String write){

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            String content = write;

            fw = new FileWriter(path);
            bw = new BufferedWriter(fw);
            bw.write(content);

            System.out.println("Done");

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }

    }

    private static MoneyRegister deser4(String arg) {
       MoneyRegister  o1=null;

        try
        {
            FileInputStream file = new FileInputStream(arg);
            ObjectInputStream in = new ObjectInputStream(file);
            o1 = (MoneyRegister) in.readObject();

            in.close();
            file.close();

        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught" + ex);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //return null;
        return o1;

    }

    private static LinkedList<OrderReceipt> deser3(String arg) {
        LinkedList<OrderReceipt> o1=null;

        try
        {
            FileInputStream file = new FileInputStream(arg);
            ObjectInputStream in = new ObjectInputStream(file);
            o1 = (LinkedList<OrderReceipt> )in.readObject();

            in.close();
            file.close();

        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught" + ex);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //return null;
        return o1;
    }


    private static HashMap<String, Integer> deser2(String arg) {
        HashMap<String,Integer>  o1=null;

        try
        {
            FileInputStream file = new FileInputStream(arg);
            ObjectInputStream in = new ObjectInputStream(file);
            o1 = (HashMap<String,Integer>)in.readObject();

            in.close();
            file.close();

        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught" + ex);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //return null;
        return o1;
    }

    public static   HashMap<Integer,Customer>  deser(String path){
            HashMap<Integer,Customer>  o1=null;

            try
            {
                FileInputStream file = new FileInputStream(path);
                ObjectInputStream in = new ObjectInputStream(file);
                 o1 = (HashMap<Integer,Customer>)in.readObject();

                in.close();
                file.close();

            }

            catch(IOException ex)
            {
               System.out.println("IOException is caught" + ex);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //return null;
                return o1;
        }

}
