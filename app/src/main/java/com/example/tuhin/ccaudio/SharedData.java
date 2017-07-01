package com.example.tuhin.ccaudio;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Tuhin on 2/27/2017.
 */

public class SharedData {
    public static BlockingQueue<short[]> recordQueue = new ArrayBlockingQueue<short[]>(5000);
    public static BlockingQueue<short[]> playerQueue = new ArrayBlockingQueue<short[]>(5000);
    //public static BlockingQueue<Integer> test = new ArrayBlockingQueue<Integer>(5000);
}
