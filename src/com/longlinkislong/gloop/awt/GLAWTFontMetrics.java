/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.awt;

import com.longlinkislong.gloop.GLFontGlpyhSet;
import com.longlinkislong.gloop.GLFontMetrics;
import com.longlinkislong.gloop.GLTools;
import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.util.Objects;

/**
 * A font metrics object for GLOOP that wraps the AWT FontMetrics object
 *
 * @author zmichaels
 * @since 15.06.11
 */
public class GLAWTFontMetrics implements GLFontMetrics {

    private final Font font;
    private final GLFontGlpyhSet supportedGlyphs;
    private final int texWidth;
    private final int texHeight;
    private final int sqr;
    
    private final FontMetrics metrics;
    
    private GlyphVector getGlyph(char c){
        return font.createGlyphVector(new FontRenderContext(new AffineTransform(), true, true), "" + c);
    }

    /**
     * Constructs a new GLAWTFontMetrics from the AWTFontMetrics object.
     * @param font the font to use
     * @param supportedGlyphs
     * @since 15.06.11
     */
    public GLAWTFontMetrics(final Font font, final GLFontGlpyhSet supportedGlyphs) {
        this.font = Objects.requireNonNull(font);
        this.supportedGlyphs = supportedGlyphs;
        
        final Canvas dummy = new Canvas();
        this.metrics = dummy.getFontMetrics(font);
        
        final float maxWidth = metrics.getHeight();
        final float maxHeight = metrics.getHeight();
        
        sqr = (int)Math.sqrt(supportedGlyphs.size()) + 1;

        final int requiredWidth = (int) (maxWidth * sqr);
        final int requiredHeight = (int) (maxHeight * sqr);
        
        this.texWidth = requiredWidth;
        this.texHeight = requiredHeight;
    }
    
    @Override
    public boolean isCharSupported(char c) {
        return supportedGlyphs.contains(c);
    }

    @Override
    public float getCharWidth(char c) {
        // NOTE, this is because the uvs tile the glyphs this way
        return getLineHeight();
    }
    
    @Override
    public float getCharHeight(char c) {
        // // NOTE, this is because the uvs tile the glyphs this way
        return getLineHeight();
    }

    @Override
    public float getLineHeight() {
        return (int) this.metrics.getHeight();
    }

    @Override
    public float getOffX(char c) {
        //return (float) getGlyph(c).getVisualBounds().getMinX() + getLineHeight()/2;
        return 0;
    }

    @Override
    public float getOffY(char c) {
        //return (float) getGlyph(c).getVisualBounds().getMinY() + getLineHeight()/2;
        return 0;
    }

    @Override
    public float getCharAdvancement(char c) {
        return (int) getGlyph(c).getGlyphMetrics(0).getAdvance();
    }

    @Override
    public float getU0(char c) {
        final float left = ((supportedGlyphs.indexOf(c)) % sqr) * getLineHeight();
        
        return left / texWidth;
    }

    @Override
    public float getU1(char c) {
        return getU0(c) + getLineHeight() / texWidth;
    }

    @Override
    public float getV0(char c) {
        final float top = ((supportedGlyphs.indexOf(c)) / sqr) * getLineHeight();
        
        return top / texHeight;
    }

    @Override
    public float getV1(char c) {
        return getV0(c) + getLineHeight() / texWidth;
    }

}
