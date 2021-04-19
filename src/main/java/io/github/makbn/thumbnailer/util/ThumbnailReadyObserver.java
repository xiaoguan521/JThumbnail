package io.github.makbn.thumbnailer.util;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Not quite sure if this is necessary:
 * This is intended to give awt a chance to draw image asynchronously.
 *
 * @author Benjamin
 */
public class ThumbnailReadyObserver implements ImageObserver {

    private final Thread toNotify;

    /**
     * The logger for this class
     */
    private final static Logger mLog = Logger.getLogger(ThumbnailReadyObserver.class);

    public volatile boolean ready = false;

    public ThumbnailReadyObserver(Thread toNotify) {
        this.toNotify = toNotify;
        ready = false;
    }

    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {

        mLog.info("Observer debug info: imageUpdate: " + infoflags);
        if ((infoflags & ImageObserver.ALLBITS) > 0) {
            ready = true;
            mLog.info("Observer says: Now ready!");
            toNotify.notify();
            return true;
        }
        return false;
    }
}
