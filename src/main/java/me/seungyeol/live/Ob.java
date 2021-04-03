package me.seungyeol.live;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("deprecation")
public class Ob {

    //    Iterable<Integer> integers = () ->
//            new Iterator<Integer>() {
//                int i = 0;
//                final static int MAX = 10;
//
//                public boolean hasNext() {
//                    return i < MAX;
//                }
//
//                public Integer next() {
//                    return ++i;
//                }
//            };
//
//        for (Integer integer : integers) {
//        System.out.println(integer);
//    }
//
//        for(Iterator<Integer> it = integers.iterator(); it.hasNext();) {
//
//    }
    static class IntObservable extends Observable implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i <= 10; ++i) {
                setChanged();
                notifyObservers(i);   // push
                // int i = it.next();  // pull
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        Observer ob = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread() + "    " + arg);
            }
        };

        IntObservable io = new IntObservable();
        io.addObserver(ob);

        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(io);

        System.out.println(Thread.currentThread().getName() + " " + "EXIT");
        es.shutdown();
    }
}
