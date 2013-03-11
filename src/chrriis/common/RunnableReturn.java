/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.common;

/**
 * Runnable that returns a typed object.
 * @param <T> type of the return
 * @author Zoltán Farkas
 */
public abstract class RunnableReturn<T> implements Runnable {

    /**
     * The return.
     */
    private T ret;

    /**
     * Implementable method that returns a typed object.
     * @throws Exception if an exception occured
     * @return a typed object
     */
    protected abstract T createReturn() throws Exception;

    /**
     * Called after {@link #createReturn()} finished.
     */
    protected void onReturn() {
        ;
    }

    /**
     * Called if an exception occures during {@link #createReturn()}.
     */
    protected void onException(Exception ex) {
        ;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Returns the object that was created by {@link #run()}.
     * @return the created object if {@link #run()} finished,
     * otherwise <code>null</code>
     */
    public final T getReturn() {
        return ret;
    }

}
