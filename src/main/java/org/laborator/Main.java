package org.laborator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.laborator.Generator.*;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        long start = System.nanoTime();

        ExecutorService executor = Executors.newFixedThreadPool(Config.numThreads);
        List<Future<List<String>>> pubFutures = new ArrayList<>();
        List<Future<List<String>>> subFutures = new ArrayList<>();

        int perThread = Config.numMessages / Config.numThreads;

        for (int i = 0; i < Config.numThreads; i++) {
            final int startIndex = i * perThread;
            final int endIndex = (i == Config.numThreads - 1) ? Config.numMessages : startIndex + perThread;

            pubFutures.add(executor.submit(() -> {
                List<String> pubs = new ArrayList<>();
                for (int j = startIndex; j < endIndex; j++) {
                    pubs.add(generatePublication().toString());
                }
                return pubs;
            }));

            subFutures.add(executor.submit(() -> {
                List<String> subs = new ArrayList<>();
                for (int j = startIndex; j < endIndex; j++) {
                    subs.add(generateSubscription(j).toString());
                }
                return subs;
            }));
        }

        List<String> pubLines = new ArrayList<>();
        List<String> subLines = new ArrayList<>();

        for (Future<List<String>> f : pubFutures) pubLines.addAll(f.get());
        for (Future<List<String>> f : subFutures) subLines.addAll(f.get());

        executor.shutdown();

        int threadsUsed = Config.numThreads;

        writeToFile("publications " + threadsUsed + " threads.txt", pubLines);
        writeToFile("subscriptions "+ threadsUsed + " threads.txt", subLines);

        long end = System.nanoTime();
        System.out.println("The data was successfully generated!");
        System.out.printf("Total time: %.2f ms\n", (end - start) / 1_000_000.0);

        int totalMessages = Config.numMessages;
        System.out.println("Total messages to generate: " + totalMessages);

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024); // in MB

        System.out.println("CPU cores available: " + availableProcessors);
        System.out.println("Threads used: " + threadsUsed);
        System.out.println("Total RAM (JVM allocated): " + totalMemory + " MB");


        //printStats();
    }
}