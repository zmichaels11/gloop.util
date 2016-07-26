/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.glfx;

import com.longlinkislong.gloop.GLQuery;
import com.longlinkislong.gloop.GLWindow;
import java.util.function.LongSupplier;
import com.runouw.util.Lazy;

import static org.lwjgl.glfw.GLFW.GLFW_ARROW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CROSSHAIR_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_HAND_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_HRESIZE_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_IBEAM_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_VRESIZE_CURSOR;
import static org.lwjgl.glfw.GLFW.glfwCreateStandardCursor;

public enum GLFXCursor {
    WAIT(() -> GLQuery.create(() -> glfwCreateStandardCursor(GLFW_ARROW_CURSOR)).glCall()),
    DEFAULT(() -> GLQuery.create(() -> glfwCreateStandardCursor(GLFW_ARROW_CURSOR)).glCall()),
    OPEN_HAND(() -> GLQuery.create(() -> glfwCreateStandardCursor(GLFW_HAND_CURSOR)).glCall()),
    MOVE(() -> GLQuery.create(() -> glfwCreateStandardCursor(GLFW_HAND_CURSOR)).glCall()),
    CROSSHAIR(() -> GLQuery.create(() -> glfwCreateStandardCursor(GLFW_CROSSHAIR_CURSOR)).glCall()),
    DISAPPEAR(() -> GLQuery.create(() -> glfwCreateStandardCursor(GLFW_CURSOR_HIDDEN)).glCall()),
    E_RESIZE(() -> GLQuery.create(() -> glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR)).glCall()),
    W_RESIZE(() -> GLQuery.create(() -> glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR)).glCall()),
    N_RESIZE(() -> GLQuery.create(() -> glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR)).glCall()),
    NE_RESIZE(() -> GLQuery.create(() -> glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR)).glCall()),
    NW_RESIZE(() -> GLQuery.create(() -> glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR)).glCall()),
    S_RESIZE(() -> GLQuery.create(() -> glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR)).glCall()),
    SE_RESIZE(() -> GLQuery.create(() -> glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR)).glCall()),
    SW_RESIZE(() -> GLQuery.create(() -> glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR)).glCall()),
    TEXT(() -> GLQuery.create(() -> glfwCreateStandardCursor(GLFW_IBEAM_CURSOR)).glCall());
    
    final Lazy<Long> glfwHandle;

    GLFXCursor(LongSupplier initializer) {
        this.glfwHandle = new Lazy<>(initializer::getAsLong);
    }

    public void apply(GLWindow window) {
        window.setCursor(glfwHandle.get());
    }
}