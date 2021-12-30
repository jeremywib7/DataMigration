package sample.watcher;

import java.io.File;
import java.util.TimerTask;

public abstract class Database1Watcher extends TimerTask {
    private long timeStamp;
    private File file;

    public Database1Watcher(File file ) {
        this.file = file;
        this.timeStamp = file.lastModified();
    }

    public final void run() {
        long timeStamp = file.lastModified();
//        System.out.println("Refreshed");
        if( this.timeStamp != timeStamp ) {
            this.timeStamp = timeStamp;
            onChange(file);
        }
    }
    protected abstract void onChange( File file );
}
