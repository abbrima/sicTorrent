import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounter {
        private final AtomicInteger counter = new AtomicInteger(0);

        public int getValue() {
            return counter.get();
        }
        public void increment() {
            while(true) {
                int existingValue = getValue();
                int newValue = existingValue + 1;
                if(counter.compareAndSet(existingValue, newValue)) {
                    return;
                }
            }
        }
        public void decrement() {
            while(true){
                int existingValue = getValue();
                int newValue = existingValue - 1;
                if (counter.compareAndSet(existingValue,newValue)){
                    return;
                }
            }
        }

}
