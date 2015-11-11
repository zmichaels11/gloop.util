/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
class GLApplicationLauncher {
    private static final Logger LOGGER = LoggerFactory.getLogger(GLApplicationLauncher.class);
    private static class Holder {

        private static final GLApplicationLauncher INSTANCE = new GLApplicationLauncher();
    }

    static GLApplicationLauncher getInstance() {
        return Holder.INSTANCE;
    }

    private GLApplicationLauncher() {
    }

    private final AtomicBoolean launchCalled = new AtomicBoolean(false);
    private volatile RuntimeException launchException = null;

    void launchApplication(final Class<? extends GLApplication> appClass, final String[] args) {
        if (launchCalled.getAndSet(true)) {
            throw new IllegalStateException("GLApplication launch must not be called more than once");
        }

        if (!GLApplication.class.isAssignableFrom(appClass)) {
            throw new IllegalArgumentException(String.format("Error: %s is not a subclass of com.longlinkislong.gloop.GLApplication", appClass));
        }

        final CountDownLatch launchLatch = new CountDownLatch(1);
        Thread launchThread = new Thread(() -> {
            try {
                doLaunchApplication(appClass, args);
            } catch (RuntimeException rte) {
                launchException = rte;
            } catch (Exception ex) {
                launchException = new RuntimeException("Exception occurred while launching GLApplication", ex);
            } catch (Error err) {
                launchException = new RuntimeException("Error occurrend while launching GLApplication", err);
            } finally {
                launchLatch.countDown();
            }
        });

        launchThread.setName("Gloop-Launcher");
        launchThread.start();

        try {
            launchLatch.await();
        } catch (InterruptedException ex) {
            throw new RuntimeException("GLApplication launch thread interrupted!", ex);
        }

        if (launchException != null) {
            throw launchException;
        }
    }
    
    private volatile Throwable stopException = null;

    void doLaunchApplication(final Class<? extends GLApplication> appClass, final String[] args) {        
        GLApplication app = null;
        try {
            final Constructor<? extends GLApplication> c = appClass.getConstructor();
            
            app = c.newInstance();            
            app.setParameters(new GLApplication.Parameters(args));
            LOGGER.trace("Constructed {}", app.getClass().getName());
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException t) {
            throw new RuntimeException("Unable to construct GLApplication", t);
        }        

        try {
            app.init();
            LOGGER.trace("Initialized {}", app.getClass().getName());
        } catch (Throwable t) {
            throw new RuntimeException("Unable to initialize GLApplication", t);
        }

        final CountDownLatch shutdownLatch = new CountDownLatch(1);

        try {
            app.gloopInit.set(true);
            
            final GLWindow window = new GLWindow(app.initialWindowWidth, app.initialWindowHeight, app.initialWindowTitle);
            final GLApplication theApp = app;

            window.setOnClose(() -> {
                try {
                    theApp.stop();
                    theApp.setWindow(null);
                    LOGGER.trace("Stopped {}", theApp.getClass().getName());
                } catch (Throwable t) {                    
                    stopException = t;                    
                } finally {
                    shutdownLatch.countDown();
                }
            });

            app.setWindow(window);
            LOGGER.trace("Starting {}", app.getClass().getName());
            app.start(window);            
            
            window.getGLThread().scheduleGLTask(GLTask.create(app::draw));
            window.getGLThread().scheduleGLTask(window.new UpdateTask());
        } catch (Throwable t) {
            throw new RuntimeException("Unable to start GLApplication", t);
        }

        try {
            shutdownLatch.await();
        } catch (InterruptedException ex) {
            throw new RuntimeException("GLApplication launch thread interrupted!", ex);
        }
        
        if(stopException != null) {
            throw new RuntimeException("Unable to stop GLApplication", stopException);
        }
    }
}
