public class MyThread extends Thread{
    private static boolean valiation = true;
    @Override
    public void run() {
        try {
            Thread.sleep(10000);
            valiation = false;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    public static boolean isValiation() {
        return valiation;
    }

    public static void setValiation(boolean valiation) {
        MyThread.valiation = valiation;
    }
}
