package mx.cubiccoding.model.pubsub;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import timber.log.Timber;

public enum Pubsub implements Runnable {
    INSTANCE;

    private static final int NUMBER_OF_THREADS = 1;

    public class Operation {
        public Operation(String type, Object o) {
            this.type = type;
            this.payload = o;
        }

        public final String type;

        public final Object payload;
    }

    ExecutorService ex;
    private final BlockingQueue<Operation> mQueue;
    private Map<String, Set<Listener>> listeners;

    Pubsub() {
        listeners = new ConcurrentHashMap<>();
        mQueue = new LinkedBlockingQueue<>();
        ex = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        ex.submit(this);
    }

    public void addListener(Listener listener, String type) {
        add(type, listener);
    }

    public void addListeners(Listener listener, List<String> types) {
        for (String type : types) {
            add(type, listener);
        }
    }

    private void add(String type, Listener listener) {
        Set<Listener> list;
        list = listeners.get(type);
        if (list == null) {
            synchronized (this) // take a smaller lock
            {
                if ((list = listeners.get(type)) == null) {
                    list = new CopyOnWriteArraySet<>();
                    listeners.put(type, list);
                }
            }
        }
        list.add(listener);
    }

    public void removeListener(String type, Listener listener) {
        remove(type, listener);
    }

    public void removeListeners(Listener listener, List<String> types) {
        for (String type : types) {
            remove(type, listener);
        }
    }

    private void remove(String type, Listener listener) {
        Set<Listener> listeners = this.listeners.get(type);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public boolean publish(PubsubData data) {
        Set<Listener> l = listeners.get(data.eventType);
        if (l != null && l.size() >= 0) {
            mQueue.add(new Operation(data.eventType, data.data));
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        Operation op;
        while (true) {
            try {
                op = mQueue.take();
            } catch (InterruptedException e) {
                continue;
            }

            String type = op.type;
            Object o = op.payload;

            Set<Listener> list = listeners.get(type);

            if (list == null || list.isEmpty()) {
                continue;
            }

            for (Listener l : list) {
                try {
                    l.onEventReceived(new PubsubData(type, o));
                } catch (Exception e) {
                    Timber.wtf(e,"Exception in pubsub thread");
                }
            }
        }
    }

    public interface Listener {
        void onEventReceived(PubsubData data);
    }

    public static class PubsubData {
        private String eventType;
        private Object data;

        public PubsubData(@NonNull String eventType, @Nullable Object data) {
            this.eventType = eventType;
            this.data = data;
        }

        public String getEventType() {
            return eventType;
        }

        @SuppressWarnings("unchecked")
        public <T> T getData() throws IllegalArgumentException {
            return (T) data;
        }
    }
}
