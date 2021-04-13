package kafka.examples;

public class Test {
    private static final String INPUT_TOPIC = "input-topic";
    private static final String OUTPUT_TOPIC = "output-topic";
    public static void main(String[] args) {
        Producer producerThread = new Producer(INPUT_TOPIC, false, null, true, 2, -1, null);
        producerThread.start();
    }
}
