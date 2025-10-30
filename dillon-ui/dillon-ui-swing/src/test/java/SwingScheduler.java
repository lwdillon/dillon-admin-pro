
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.disposables.DisposableHelper;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class SwingScheduler extends Scheduler {

    private static final SwingScheduler INSTANCE = new SwingScheduler();

    public static SwingScheduler edt() {
        return INSTANCE;
    }

    @Override
    public Worker createWorker() {
        return new SwingWorker();
    }

    static class SwingWorker extends Worker {
        private volatile boolean disposed;

        @Override
        public Disposable schedule(Runnable run, long delay, TimeUnit unit) {
            if (disposed) return DisposableHelper.DISPOSED;

            if (delay == 0) {
                SwingUtilities.invokeLater(run);
            } else {
                Timer timer = new Timer((int) unit.toMillis(delay), e -> run.run());
                timer.setRepeats(false);
                timer.start();
            }
            return Disposable.fromRunnable(() -> disposed = true);
        }

        @Override
        public void dispose() {
            disposed = true;
        }

        @Override
        public boolean isDisposed() {
            return disposed;
        }
    }
}