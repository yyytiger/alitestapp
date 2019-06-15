import alitest.AliWriter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

public class App {

    private static Vector<String> collection = new Vector<>();
    private static final String stringToWrite = "Ali";

    public static void main(String[] args){
        //Get number of Ali writing times
        int times = getInput();

        //CountDownLatch for waiting all the threads to complete their work
        CountDownLatch signal = new CountDownLatch(stringToWrite.length());

        //Initialize writers
        ArrayList<AliWriter> writers = new ArrayList<>();
        for(char c : stringToWrite.toCharArray()) {
            AliWriter writer = new AliWriter(String.valueOf(c), collection, times, signal);
            Thread thread = new Thread(writer);
            writer.setCurrentThread(thread);
            writers.add(writer);
        }

        //Set next thread to build the chain
        for(int i=0; i<writers.size(); i++) {
            AliWriter writer = writers.get(i);
            if (i < writers.size() - 1)
                writer.setNextThread(writers.get(i + 1).getCurrentThread());
            else
                writer.setNextThread(writers.get(0).getCurrentThread());
        }

        //Start all the threads
        List<Thread> threads = writers.stream().map(w -> w.getCurrentThread()).collect(Collectors.toList());
        for (Thread t : threads){
            t.start();
        }

        //Trigger the first thread
        LockSupport.unpark(threads.get(0));

        //Wait for all the threads to complete their work
        try {
            signal.await();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

        //Output the strings from the collection
        for (String s : collection){
            System.out.print(s);
        }
    }

    private static int getInput(){
        while (true) {
            System.out.println("Please input count:");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String s = null;
            int times;
            try {
                s = reader.readLine();
                times = Integer.parseInt(s);
            } catch (Exception ex) {
                System.out.println(String.format("Invalid input: %s", s));
                continue;
            }

            return times;
        }
    }
}
