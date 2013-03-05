package chrriis.common;

public abstract class RunnableReturn<T> implements Runnable {

    private T ret;

    protected abstract T createReturn() throws Exception;

    protected void onReturn() {
        ;
    }

    protected void onException(Exception ex) {
        ;
    }

    @Override
    public final void run() {
        synchronized (this) {
            try {
                ret = createReturn();
            }
            catch (Exception ex) {
                onException(ex);
            }
        }
        onReturn();
    }

    public final T getReturn() {
        return ret;
    }

}